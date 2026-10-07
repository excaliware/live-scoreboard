package com.livefootball.scoreboard;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LiveScoreboardTest {
    private final LiveScoreboard scoreboard = new LiveScoreboard();

    @Test
    void startsMultipleMatchesWithIndependentScores() {
        long firstId = scoreboard.startMatch("Mexico", "Canada");
        long secondId = scoreboard.startMatch("Spain", "Brazil");

        scoreboard.updateScore(firstId, 0, 5);
        scoreboard.updateScore(secondId, 10, 2);

        assertThat(firstId).isNotEqualTo(secondId);
        assertThat(scoreboard.getSummary())
                .containsExactly(
                        new MatchSnapshot(secondId, "Spain", "Brazil", 10, 2),
                        new MatchSnapshot(firstId, "Mexico", "Canada", 0, 5));
    }

    @Test
    void updatesReplaceBothScores() {
        long matchId = scoreboard.startMatch("Mexico", "Canada");
        scoreboard.updateScore(matchId, 2, 3);
        scoreboard.updateScore(matchId, 1, 0);

        assertThat(scoreboard.getSummary())
                .containsExactly(new MatchSnapshot(matchId, "Mexico", "Canada", 1, 0));
    }

    @Test
    void finishingRemovesOnlyThatMatchFromSummary() {
        long finishedId = scoreboard.startMatch("Mexico", "Canada");
        long activeId = scoreboard.startMatch("Spain", "Brazil");

        scoreboard.finishMatch(finishedId);

        assertThat(scoreboard.getSummary())
                .containsExactly(new MatchSnapshot(activeId, "Spain", "Brazil", 0, 0));
    }

    @Test
    void getsAnImmutableSnapshotForAnActiveMatch() {
        long matchId = scoreboard.startMatch("Mexico", "Canada");
        scoreboard.updateScore(matchId, 0, 5);

        assertThat(scoreboard.getMatch(matchId))
                .isEqualTo(new MatchSnapshot(matchId, "Mexico", "Canada", 0, 5));
    }

    @Test
    void summaryIsEmptyWhenNoMatchesAreActive() {
        assertThat(scoreboard.getSummary()).isEmpty();
    }

    @Test
    void summaryMatchesTheOrderingExampleFromTheSpecification() {
        long mexicoCanada = scoreboard.startMatch("Mexico", "Canada");
        long spainBrazil = scoreboard.startMatch("Spain", "Brazil");
        long germanyFrance = scoreboard.startMatch("Germany", "France");
        long uruguayItaly = scoreboard.startMatch("Uruguay", "Italy");
        long argentinaAustralia = scoreboard.startMatch("Argentina", "Australia");

        scoreboard.updateScore(mexicoCanada, 0, 5);
        scoreboard.updateScore(spainBrazil, 10, 2);
        scoreboard.updateScore(germanyFrance, 2, 2);
        scoreboard.updateScore(uruguayItaly, 6, 6);
        scoreboard.updateScore(argentinaAustralia, 3, 1);

        assertThat(scoreboard.getSummary())
                .extracting(MatchSnapshot::homeTeam, MatchSnapshot::awayTeam)
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple("Uruguay", "Italy"),
                        org.assertj.core.groups.Tuple.tuple("Spain", "Brazil"),
                        org.assertj.core.groups.Tuple.tuple("Mexico", "Canada"),
                        org.assertj.core.groups.Tuple.tuple("Argentina", "Australia"),
                        org.assertj.core.groups.Tuple.tuple("Germany", "France"));
    }

    @Test
    void mostRecentlyStartedMatchComesFirstWhenTotalsTie() {
        long earlierId = scoreboard.startMatch("Mexico", "Canada");
        long laterId = scoreboard.startMatch("Spain", "Brazil");
        scoreboard.updateScore(earlierId, 1, 2);
        scoreboard.updateScore(laterId, 2, 1);

        assertThat(scoreboard.getSummary())
                .extracting(MatchSnapshot::matchId)
                .containsExactly(laterId, earlierId);
    }

    @Test
    void computesTotalScoreWithoutIntegerOverflow() {
        long highScoreId = scoreboard.startMatch("Mexico", "Canada");
        long lowerScoreId = scoreboard.startMatch("Spain", "Brazil");
        scoreboard.updateScore(highScoreId, Integer.MAX_VALUE, Integer.MAX_VALUE);
        scoreboard.updateScore(lowerScoreId, Integer.MAX_VALUE, 0);

        assertThat(scoreboard.getSummary())
                .extracting(MatchSnapshot::matchId)
                .containsExactly(highScoreId, lowerScoreId);
    }

    @Test
    void summaryIsImmutable() {
        scoreboard.startMatch("Mexico", "Canada");
        List<MatchSnapshot> summary = scoreboard.getSummary();

        assertThatThrownBy(() -> summary.add(new MatchSnapshot(99, "A", "B", 0, 0)))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void duplicateFixturesHaveSeparateIdsAndState() {
        long firstId = scoreboard.startMatch("Mexico", "Canada");
        long secondId = scoreboard.startMatch("Mexico", "Canada");
        scoreboard.updateScore(firstId, 1, 0);

        assertThat(scoreboard.getSummary())
                .containsExactly(
                        new MatchSnapshot(firstId, "Mexico", "Canada", 1, 0),
                        new MatchSnapshot(secondId, "Mexico", "Canada", 0, 0));
    }

    @Test
    void trimsTeamNamesAndRejectsTeamsThatOnlyDifferByCase() {
        long matchId = scoreboard.startMatch("  Mexico  ", "Canada ");

        assertThat(scoreboard.getSummary())
                .containsExactly(new MatchSnapshot(matchId, "Mexico", "Canada", 0, 0));
        assertThatThrownBy(() -> scoreboard.startMatch("Mexico", " mexico "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsNullBlankAndNegativeInputs() {
        assertThatThrownBy(() -> scoreboard.startMatch(null, "Canada"))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> scoreboard.startMatch(" ", "Canada"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> scoreboard.startMatch("Mexico", "\t"))
                .isInstanceOf(IllegalArgumentException.class);

        long matchId = scoreboard.startMatch("Mexico", "Canada");
        assertThatThrownBy(() -> scoreboard.updateScore(matchId, -1, 0))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> scoreboard.updateScore(matchId, 0, -1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsOperationsForUnknownOrFinishedMatches() {
        long matchId = scoreboard.startMatch("Mexico", "Canada");
        scoreboard.finishMatch(matchId);

        assertThatThrownBy(() -> scoreboard.getMatch(matchId))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> scoreboard.updateScore(matchId, 1, 0))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> scoreboard.finishMatch(matchId))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> scoreboard.getMatch(999))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> scoreboard.updateScore(999, 1, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
