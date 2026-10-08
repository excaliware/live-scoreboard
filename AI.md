# AI Tools Usage Summary

## How AI was used

GitHub Copilot was used for:
- requirements analysis
- identifying ambiguities
- API design
- implementation assistance
- test generation
- code review
- README review

## Prompt history

### Prompt 1
Here is a coding task:
Implement a simple library that manages a Live Football World Cup Scoreboard. Please read the full requirements before starting.
Using AI to help with design, reasoning, and code generation is required. If you're selected for an interview, we'll walk through your solution together. We'll ask you to explain key decisions, trade- offs, and code details.
What we expect
Please provide the implementation of the Live Football World Cup Score Board as a simple Java package in a Maven project.
Provide your solution as a git repository exactly as you would submit it for code review to your team-mates at work. Follow your own standards for high quality software engineering.
Requirements
You work for a sports data company and must implement a scoreboard library that supports multiple simultaneous matches. The requirements below intentionally include open questions and design choices; part of the task is deciding how to handle them and documenting your reasoning.
See additional details in README.md.
The additional operation will be getMatch. we'll add it later.
Analyse this task as if you were a senior Java engineer helping me prepare a production-quality code review submission.
Extract the functional requirements.
Identify all ambiguous requirements.
Propose a minimal public API for the library.
Propose a simple domain model and data structures.
Discuss how to implement the required summary ordering.
Identify validation.
Propose a unit-test strategy, including whether TDD would be appropriate here.
Identify potential design choices or trade-offs that I should document in README.md.
The design should remain simple. Do not add libraries.
At the end, provide a concrete implementation plan, but do not write code yet.

### Prompt 2
For unit tests you can add an assertion/matcher library, like hamcrest or assertj.
Could you start implementing the library?
Also, after implementing it, explain the design and the purpose of each class and important data structure.

### Prompt 3
Review the current repository as if you were a senior Java engineer reviewing a pull request.
Look specifically for:
API design problems
unnecessary complexity
incorrect assumptions
edge cases not covered by tests
mutable state leaking through the public API
ordering bugs
poor naming
inappropriate use of collections
exception handling
thread-safety assumptions
test quality
Maven/project structure
anything that would be difficult to defend in a technical interview
Do not make changes yet. First give me the review and explain the recommended changes.

## Artifacts that guided implementation

- Coding exercise specification
- Design notes
- Test cases / acceptance criteria
