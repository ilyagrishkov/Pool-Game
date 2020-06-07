 # Sprint retrospective, iteration 2
 ## Development time
 | User story # | Task # | Assigned to | Expected time | Actual time | Done | Notes |
 |---|---|---|---|---|--|--|
 | - | Refactor Gamescreen class into a screen class and a game class. | Ivar | 3 | 3 | Yes | As expected it took me quite some time to figure out what was happening where but in the end I got a decent split |
 | - | Refactor game class into an abstract base class and an 8-ball class | Ivar | 2 | 2 | Yes | Same as the other refactor |
| 31 (9-ball) | Implement 9 ball | Ivar | 4 | 4 | Yes | After the refactor of the game classes, implementing a new game mode is not very difficult most of the time was spend on testing all of the combinations of the rules |
| - | Add tests for the game logic | Ivar | 2 | 3 | Yes | Testing the 8-ball rules took a lot more time then I expected, I also found some edge case rule mistakes that took some time to fix |
| - | Added displays for 8-ball and 9-ball (foul messages, turns, types, lowest ball) | Marc | 4 | 6 | Yes | Was a bit harder then I expected to figure out the models for the displays |
| - | Added tests for displays | Marc | <1 | <1 | Yes | Tested the display methods |
| 5 | Make a menu where users can see and submit their score | Marc | 1 | 1 | yes | Completely functional but doesn't look good yet. |
| - | Design a better looking GUI | Marc | 8 | 0 | No | Had no time this sprint will be done next sprint. |
| - | Handle Register requests | Laura | 1 | 1 | yes | It was about as difficult as I expected. I did not run into any huge problems. |
| - | Add tests for request package | Laura | 1 | 4 | yes | It was pretty difficult to test the request classes, as I couldn't mock everything I needed to|
| - | Add websockets | Laura | 4 | 1 | no | We decided to scrap multiplayer, but i did still do some research into it |
| - | Improve logging and checks | Tim | 4h | 4h50m | Yes | |
| - | Add routes for scores (submit, list, leaderboard) | Tim | 2h30m | 2h5m | Yes | |
| - | Clean up route handling and tests on server | Tim | 9h | 9h30m | Yes | | 
| - | Physics improvements | Ilya | 3 | 3 | no | Improved some physics of balls. Now physics is more realistic, but there is room for improvements |
| - | Testing game logic | Ilya | 5 | 7 | no | Testing game logic turned out to be trickier than I expected as code should be refactored to follow better testing practices |

 ## Other time spent
 | Task  | Who? | Expected time | Actual time | Done | Notes |
 |---|---|---|---|--|--|
 | UML Sequence Diagram | Ivar | 5 | 4 | Yes | A lot of time was spent figuring out how to create these diagrams in a easy to understand way, once I found a good editor it went a lot faster then I expected. |
  | UML Sequence Diagram | Tim | 5 | 6 | Yes | Same as Ivar. |
 | Reviewing MR's, giving feedback, working on backlog and retrospective etc. | Ivar | - | 4 | - | Just general time spent on the project not covered in any of the other rows that Callum wanted to know about after the previous Sprint |
 | Reviewing MR's and discussing things | Marc | 2 | 2 | - | Looking at other members their code and discussing some things about it |
 | Reviewing MR's, meetings, documentation etc | Laura | - | 2 | yes | general project related things that are not necessairily code related|
 | Reviewing MR | Tim | - | 4 | Yes | |
 | Setting up stricter test suite and mutation testing | Tim | 2 | 5 | Yes | Due do to our dependencies (mysql) Pitest was a bit harder to set up than expected |   
 | Reviewing MR's, meetings | Ilya | - | 2 | yes | General project related stuff|
 | Creating screencast | Laura | 1 | 1 | Yes | |
 
 Project: pool
 Group: 86
 # Main problems
 ## Problem 1
 Description: During the previous sprint we had very low test coverage, which meant that when we did some major refactor we couldn't easily test if we broke something.
 Reaction: This sprint we have spent time increasing test coverage meaning this should no longer happen during the final sprint.

 # Review
 ## Ivar
 In the first week of this sprint I created the sequence diagrams for the assignment, then during the second week I refactored the GameScreen class which originaly contained all the screen things and the entire game logic.
 This class know only has the screen related methods and the gamelogic has been moved to an abstract Game class extended by class implemented our game variants (8-ball and 9-ball).
 Then I also created 9-ball and added tests to all of the game logic classes.
 This sprint went mostly as I expected.
 For the remainder of the project I do want to keep the test coverage a lot higher as low test coverage caused some difficulty during the refactors as I had no real way of knowing if I broke something.

 ## Marc
 The first week of this sprint was very busy due to midterms, however the second week I made displays and a score submission screen.
 This sprint I was ambitious as I added working on better looking GUI and leaderboard page to the backlog however due to the midterms I didn't have enough time. Luckily these things were not important for this sprint so I did the things that were definitely needed for this sprint.
 Most of the coding is done for me except for leaderboards screen and a few other minor things. The next sprint will mostly be spent on actually having the GUI look good instead of only functioning.
 
  ## Laura
  This sprint I focused on the user authentication being good instead of just functioning. In the previous sprint a lot of things were implemented in a hacky
  way, but this time around I made sure everything was properly implemented.
  Mext sprint I want to work on the sending/displaying of scores and improving the test coverage even further.
  
  ## Tim
  This sprint I focused on making CI enforce the rubric requirements, adding enpoints, improving endpoint handlers and adding a lot of tests. Next sprint I want to finish up the server, by adding the final tests and tidying it up.
  
  ## Ilya
  The main things I have been focusing on during this sprint were physics improvement, minor code refactoring, and game logic testing. 
  In terms of physics improvements, everything went well. Testing, on the other hand, turned out to be a bit tricky as some code was extremely difficult to reach. 
  Hence, it was not finished, and in order for it to be properly done, the game logic code should be refactored, and then it can be tested.
  Next sprint, I am planning to spend a couple of hours working on testing and solving problems that prevented doing it during this sprint.
