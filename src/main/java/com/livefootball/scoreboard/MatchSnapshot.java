package com.livefootball.scoreboard;

/**
 * Immutable view of a match currently tracked by a {@link LiveScoreboard}.
 */
public record MatchSnapshot(
        long matchId,
        String homeTeam,
        String awayTeam,
        int homeScore,
        int awayScore) {

    /**
     * Returns the combined score without overflowing an {@code int}.
     */
    public long totalScore() {
        return (long) homeScore + awayScore;
    }
}
