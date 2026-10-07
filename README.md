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

### Reasoning  
### Decisions  
### Trade-offs made  

