package com.livefootball.scoreboard;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * In-memory scoreboard for concurrently active football matches.
 *
 * <p>Each public operation is synchronized so updates and returned snapshots
 * observe a consistent scoreboard state.</p>
 */
public final class LiveScoreboard {
    private final Map<Long, Match> activeMatches = new LinkedHashMap<>();
    private long lastMatchId;

    /**
     * Starts a match at 0-0 and returns its unique ID.
     *
     * @throws IllegalArgumentException if either team name is blank or the
     *                                  same team is supplied twice
     */
    public synchronized long startMatch(String homeTeam, String awayTeam) {
        String normalizedHomeTeam = validateTeamName(homeTeam, "homeTeam");
        String normalizedAwayTeam = validateTeamName(awayTeam, "awayTeam");
        if (normalizedHomeTeam.equalsIgnoreCase(normalizedAwayTeam)) {
            throw new IllegalArgumentException("A team cannot play against itself");
        }

        long matchId = Math.incrementExact(lastMatchId);
        Match match = new Match(matchId, normalizedHomeTeam, normalizedAwayTeam, 0, 0);
        activeMatches.put(matchId, match);
        lastMatchId = matchId;
        return matchId;
    }

    /**
     * Replaces both scores for an active match.
     *
     * @throws IllegalArgumentException if either score is negative or the ID
     *                                  does not identify an active match
     */
    public synchronized void updateScore(long matchId, int homeScore, int awayScore) {
        validateScore(homeScore, "homeScore");
        validateScore(awayScore, "awayScore");
        Match match = requireActiveMatch(matchId);
        activeMatches.put(matchId, match.withScore(homeScore, awayScore));
    }

    /**
     * Removes a match from the in-progress scoreboard.
     *
     * @throws IllegalArgumentException if the ID does not identify an active match
     */
    public synchronized void finishMatch(long matchId) {
        requireActiveMatch(matchId);
        activeMatches.remove(matchId);
    }

    /**
     * Returns an immutable snapshot of an active match.
     *
     * @throws IllegalArgumentException if the ID does not identify an active match
     */
    public synchronized MatchSnapshot getMatch(long matchId) {
        return requireActiveMatch(matchId).toSnapshot();
    }

    /**
     * Returns an immutable summary ordered by total score descending, then by
     * most recently started first for equal totals.
     */
    public synchronized List<MatchSnapshot> getSummary() {
        List<Match> matches = new ArrayList<>(activeMatches.values());
        matches.sort(Comparator
                .comparingLong(Match::totalScore)
                .reversed()
                .thenComparing(Comparator.comparingLong(Match::matchId).reversed()));
        return matches.stream()
                .map(Match::toSnapshot)
                .toList();
    }

    private static String validateTeamName(String teamName, String parameterName) {
        Objects.requireNonNull(teamName, parameterName + " must not be null");
        String normalizedTeamName = teamName.trim();
        if (normalizedTeamName.isEmpty()) {
            throw new IllegalArgumentException(parameterName + " must not be blank");
        }
        return normalizedTeamName;
    }

    private static void validateScore(int score, String parameterName) {
        if (score < 0) {
            throw new IllegalArgumentException(parameterName + " must not be negative");
        }
    }

    private Match requireActiveMatch(long matchId) {
        Match match = activeMatches.get(matchId);
        if (match == null) {
            throw new IllegalArgumentException("No active match with ID " + matchId);
        }
        return match;
    }

    private record Match(
            long matchId,
            String homeTeam,
            String awayTeam,
            int homeScore,
            int awayScore) {

        private Match withScore(int updatedHomeScore, int updatedAwayScore) {
            return new Match(matchId, homeTeam, awayTeam, updatedHomeScore, updatedAwayScore);
        }

        private long totalScore() {
            return (long) homeScore + awayScore;
        }

        private MatchSnapshot toSnapshot() {
            return new MatchSnapshot(matchId, homeTeam, awayTeam, homeScore, awayScore);
        }
    }
}
