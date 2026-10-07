# Live Scoreboard

## Overview
A library that supports multiple simultaneous matches, developed for a sports data company

### Core Operations
1. Start a new match
2. Update the score
3. Finish a match
4. Get a summary of matches in progress  
   Return the matches in progress ordered by:  
   * Total score (descending)
   * If tied → most recently started match first
5. Add exactly one additional operation of your choice  
   Add one feature of your own choice to the scoreboard. Include documentation in 
   the README.md explaining your feature and why you chose it. Please ensure that 
   there is a distinct git commit that introduces the feature.


### Example Scenario
If the following matches are started in the specified order and updated with these scores:  
* Mexico 0 – Canada 5
* Spain 10 – Brazil 2
* Germany 2 – France 2
* Uruguay 6 – Italy 6
* Argentina 3 – Australia 1

Expected summary ordering:
* Uruguay 6 – Italy 6
* Spain 10 – Brazil 2
* Mexico 0 – Canada 5
* Argentina 3 – Australia 1
* Germany 2 – France 2


### Assumptions

- The scoreboard tracks active matches only and stores them in memory for the lifetime of a `LiveScoreboard` instance.
- Starting a match creates a unique match ID. Duplicate team fixtures are allowed and are distinguished by ID.
- Scores are nonnegative integers. Updating a score replaces both team scores; scores need not increase monotonically.
- Team names are trimmed when a match starts. Blank names and a team playing itself (case-insensitively) are rejected.
- Unknown or finished match IDs are invalid for operations that require an active match.

### Reasoning

Team names are not a reliable identifier because the same fixture could be started more than once. A generated ID makes updates and lifecycle operations unambiguous. The scoreboard does not retain completed-match history because the requirements only ask for matches in progress.

### Decisions

- `startMatch(homeTeam, awayTeam)` starts a 0-0 match and returns its `long` ID.
- `updateScore(matchId, homeScore, awayScore)` replaces both scores.
- `finishMatch(matchId)` removes the match from the active scoreboard.
- `getSummary()` returns an immutable list of immutable match snapshots. Invalid scores and unknown/inactive IDs throw `IllegalArgumentException`; null team names throw `NullPointerException`.
- Summary ordering is total score descending, followed by most recently started first for equal totals. A monotonic match ID doubles as the start sequence, avoiding wall-clock precision and clock-change issues.
- Public operations are synchronized so each update and summary snapshot is atomic with respect to other calls on the same scoreboard instance.

### Trade-offs made

The API intentionally avoids persistence, history, external dependencies, and a more elaborate match lifecycle. Synchronization keeps the in-memory implementation straightforward and consistent, but may limit throughput if a future use case needs very high write concurrency.

### Public API

```java
LiveScoreboard scoreboard = new LiveScoreboard();
long matchId = scoreboard.startMatch("Mexico", "Canada");
scoreboard.updateScore(matchId, 0, 5);
List<MatchSnapshot> matches = scoreboard.getSummary();
scoreboard.finishMatch(matchId);
```

The API also includes an active-match lookup, `getMatch(matchId)`, which is being added in a separate follow-up commit as the one additional operation required by the brief.

`MatchSnapshot` exposes `matchId()`, `homeTeam()`, `awayTeam()`, `homeScore()`, `awayScore()`, and `totalScore()`. The snapshot and summary list are immutable.

### Testing

Tests cover normal operation, match lifecycle, invalid inputs and IDs, independent duplicate fixtures, immutable results, overflow-safe totals, and the full ordering example above. Run them with `mvn test`.
