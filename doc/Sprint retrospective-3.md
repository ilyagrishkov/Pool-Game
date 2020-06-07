 # Sprint retrospective, iteration 3
 ## Development time  
   
 | User story # | Task # | Assigned to | Expected time | Actual time | Done | Notes |  
 |---|---|---|---|---|---|---|  
 | 25 (tutorial) | Create screens with text support for the rule explanations | Ivar | 3h | 2h | Yes | Expected to need more time to create the screens |  
 | 25 (tutorial) | Add rule explanation | Ivar | 2h | 2h 30m | Yes | Getting text on a screen was more difficult then I expected |  
 | | Increase test coverage for gameobjects | Ivar | 5h | 6h | Yes | LibGdx is a horrible library to test. |  
 | | Bug fixes | Ivar | - | 1h | Yes | We discovered a few bugs that needed to be fixed immediately. |  
 | | Designing new GUI and displays |  Marc | 6h | 7h | Yes | Have designed everything. |  
 | | Added new design to the GUI | Marc | 3h | 6h | No | Ran into quite a lot of annoying libgdx things but I fixed them, only need to update leaderboard and rules screen (all designs are already done will take approx. 1 hour. |  
 | | Finish server test suite | Tim | 6h | 5h | Yes | |
 | | Refactor server code | Tim | 4h | 4h50m | Yes | |
 | | Finalize user object modifications | Tim | 4h | 2h | Yes | |
 | 3 (help line) | Create help line | Ilya | 3h | 4h | Yes | Creaated a fixed length help line that shows aiming direction |
 | | Refactor game logic | Ilya | 1h | 1h | Yes | Added some minor changes to improve testability of game objects |
 | 10, 13, 17 (cue ball position) | Moving cue ball after foul | Ilya | 3h | 3h | Yes | Allow player to put a cue ball to a desired place after the other player commited a foul |
 | | Leaderboard | Laura | 5h | 8h | No | Allow the player to view the scores of the top 5 players. Done, but not merged into development |
 | | Write request tests| Laura | 3h | 1h | No | Properly test everything in the requests package. |
   
 | Task  | Who? | Expected time | Actual time | Done | Notes |  
 |---|---|---|---|---|---|  
 | Reviewing MR's etc. | Ivar | - | 3h | Yes | |  
 | Working on final presentation | Ivar | - | 3h | Yes ||  
 | Writing text for final presentation | | 1h | 2h | Yes | will need to update slides with the new GUI pictures. |  
 | Design pattern assignment | Tim | 3h | 3h30m | Yes | |
 | Working on final presentation | Tim | - | 2h | Yes  | | 
 | Preparing for the final presentation | Ilya | - | 2h | Yes | |
 | Making a template for slides | Ilya | - | 2h | Yes | |
 | Preparing for final presentation| Laura | - | 2h | Yes | |
 | UML Architecture diagram + description of project architecture| Laura | - | 3h | Yes | |
 
 Project: pool  
 Group: 86
 
 # Main problems
Description: LibGdx is very difficult to unit test. It uses a lot of public fields, which can't be mocked as well as every constructor using a model that can't be mocked nor created without a full 3d world being created.  
Solution: Next time we use LibGdx we should at the start set up some libraries to aid us in testing it.

 # Review
 ## Ivar
 During this sprint we worked on finalising many things. For me this meant creating a rule explanation and making sure we had enough test coverage. 
 I expected to encounter some difficulties during this sprint so I did not go too much over my expected time. However LibGdx is a lot worse to unit test then I expected.

 ## Marc
 During this sprint I redesigned and refactored the GUI. There is less duplicated code and everything looks much cleaner. Also the GUI has been totally revamped and looks way better.
 It took more time then expected due to libGdx having a few small annoying things that I needed to figure out. Such as resizing screens (they kept on crashing the application) and designing buttons (very predetermined way of designing them had to search a lot to understand it). 
 
 ## Tim
 I mostly did refactoring and testing this week (if only we knew the next assignment would be refactoring...), which took about as long as expected.
 The design pattern assignment proved to be a little more work than expected, but nothing we couldn't handle.
 The most annoying thing this sprint was probably figuring out why the CI suddenly broke, but luckily it was a full disk. 
 
 ## Ilya
 During this sprint I've worked on several client features and doing some refactoring of game logic. I've implemented a possiblity to move a cue ball in case of a foul as well as a help line that points in an aiming direction.
 I went a little bit over the expected time, but in general time management was good during this sprint.
 Testing help line took some time to figure out as libGDX is not the most convinient library to unit test. 
 
 ## Laura
 During this sprint I worked on implementing the leaderboard and improving the authentication system, which now uses tokens and refresh tokens whenever an user logs in.
 However, that made sending scores a lot more complex, so it took a lot more time than I expected, and I worked on non-code related tasks a lot more this week. I should have probably started earlier
 and managed my time better during this sprint.