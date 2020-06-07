 # Sprint retrospective, iteration 1
 | User story # | Task # | Assigned to | Expected time | Actual time | Done | Notes |
 |---|---|---|---|---|--|--|
 |2 (game objects) | Ball and cue should be shown on a screen | Ilya | 2 | 7 | yes | Cue was quite diffcult to implement properly, it took me more time, than I expected. Most of it I spend on implementing transformations. |
 |5 (adding score after game) | Setting up database to support scores | Ivar | 1 | 2 | yes |Figuring out a many-to-one relation in hibernate took a bit longer then expected.|
 |6 (local play) | There should be a way to play locally | Ilya | 15-20 | 40 | yes, except for rules/turns GUI | Local game include everyting from collisions to functionality of rule enforcement (GUI is not there yet), so it's difficult to estimate time spent. |
 |7 (leaderboard) | Setting up database support for leaderboard | Ivar | <1 | <1 | yes |With scores fully functional this took little time.|
 |8 (register) | Creating route | Tim | <1 | 4 | yes | Setting up the server and its structure took longer than expected. Especially testing and documentation cost a considerable amount of time.|
 |8 (register) | Setting up the database to support users | Ivar | 4 | 6 | yes | Setting up and configuring hibernate took way more time then I expected. There were quite a few issues regarding connecting to the database that took a lot of time to solve. | 
 |8 (register) | Make a menu where users can register | Marc | 1 | 1 | yes | All buttons work but not connected to the database yet and doesn't look good yet. |
 |9  (login)| Creating route | Tim | <1 | <1 | yes | Went about as expected once the first route (register) was created.|
 |9  (login)| Setting up the database to verify logins | Ivar | <1 | <1 | yes | Password verification was once I had hibernate fully functional.|
 | | Adding JWT | Tim | 2 | 3 | yes | Took a bit longer than expected, mainly due to documentation, testing and CI. |
  |9 (login) | Making requests to the server | Laura | 2 | 3 | yes, but not tested yet | library I used was relatively straight forward, except some issues with async callbacks that I decided against using anyway. |
 |9 (login) | Make a menu where users can login | Marc | 1 | 1 | yes | Completely functional but doesn't look good yet. |
 |11/12 (ball type assignment) | Each player is assigned stripes or solids | Ilya | <1 | <1 | yes | After the first ball is potted it's type is assgned to a player who potted it |
 |13 (hit wrong ball frist) | Foul if a player hit's a ball of a wrong type | Ilya | <1 | <1 | yes, but no cue ball movement | If player touches a wrong type ball first it counts as foul. |
 |14/15 (black ball) | Black ball belongs to no one until the last shot | Ilya | <1 | <1 | yes | In case it was not the last shot of a player and the black ball was potted, the player loses the game. |
 |16 (removing balls) | If a ball is potted it's removed from the table | Ilya | <1 | <1 | yes | If the ball was potted no calculations are done for it and it's no longer on the table. |
 |17(10) (cue ball potted) | If cue ball potted it should return on the table | Ilya | <1 | 2 | yes, but no cue ball movement | Tried to make player choose a place for a ball, ended up reseting it at the start position. |
 |18 (collisions) | Proper collisions between balls | Ilya | 3 | 7 | yes | Trying a couple of different methods of detecting collisions, so it took more time than I expected. |
 |19 (wall collisions) | Ball bouncing off the wall | Ilya | 1 | 1 | yes | Fully functional bounce of the wall. |
 |20 (settings) | Make a menu where users can choose their settings | Marc | <1 | <1 | yes, but no options yet | The menu is there, there are just no real settings yet. |
 |22 (animations of movement) | Balls should have rolling animation | Ilya | 1 | 1 | yes | Balls are 3D, so applying texture and having proper physics was enough to make roling animation. |
 |26 (spin physics) | Balls' spin affects movement | Ilya | 2 | 1 | yes | Proper physics engine allowes to calculate angular velocity and it affect interaction between balls. | 
 || Learning libGDX to make a GUI | Marc | 4 | 7 | yes | Fully understand libGDX now for making a GUI, took a bit longer then expect due to the complexity and different ways of doing it. |
 
 
 Project: pool
 Group: 86
 # Main problems
 ## Problem 1
 Description: Documentation and testing took more time than expected.
 Reaction: We should allow more time next time for 'administrative' tasks.
 
 ## Problem 2
 Description: Setting up libraries took more time then expected.
 Reaction: Everything is now setup so we shouldn't have much trouble with this in the upcoming sprints.

 # Review
 ## Tim
 In this sprint I set up the structure for the entire project, created the server and its dependencies, added routes (/api/login, /api/register, /ping), added docker services for dependencies (MySQL and Redis) for CI and local and implemented everything required for Json Web Tokens. It mostly went well. However, testing and documentation took a lot more time than expected. Setting up docker services for the CI also proved to be more difficult than expected, because I spent a lot of time debugging the test suite, when in the end it just was the CI config of TU Delft. 
 For the next sprint, I'd like to allocate more time for documentation and testing.
 
 ## Ivar
 In this sprint I setup hibernate, created entities to store in the database and added endpoints to interact with the database, these were later updated by tim to conform to a later created interface.
 The actual creation of the entities went well, but as it was the first time I used Hibernate I ran into some trouble getting that to work.
 For the next sprint I'd like to get merge requests reviewed, fixed and merged sooner as during this sprint they were open for a long time.

 ## Laura
 In this sprint I setup Retrofit, the library for making requests from the client to the server. I also made an HttpClient on the client that is able to talk to the server and make both sync and async calls. I also made some classes for the Login requests, mostly as a template for how I want the others to look as well. I also wrote a specification with the requests I want
 In the next sprint, I will try to get to work earlier and respond to Merge Requests more quickly and thoroughly. I would also like to focus more on testing.
 
 ## Ilya
 In this sprint, I setup libGDX, added physics of balls collisions as well as collisions between balls and walls. I also implemented the functionality of a cue and pockets, added enforcement of rules, such that when balls are potted, they are added to a player's list of balls. In case of potting a cue ball, it is being reset at the start position. I also implemented a turn system. A model of a table is also there but requires some adjustments.
 The creation of game logic mostly went well. However, I had never worked with libGDX before, so it took me some time to learn how this library works. Transformations were also quite tricky, especially transformations of a cue, but I managed to get them to work.
 In the next sprint, I want to try spending more time on testing functionality as well as making some improvements and bug fixes.

 ## Marc
 In this sprint I made a working GUI, helped Ilya with the game logic and some math and learned how to use libGDX. The most time was spent on learning libGDX as there were a lot of ways I could make the GUI. I finally decided to use libGDX screens and stages with buttons. I also helped people whenever I had time and had enough knowledge about the subject, mostly Ilya cause I did some research beforehand on pool physics and together we decided how to calculate everything.
 For the next sprint I want to make the GUI look good and implement a leaderboard menu. I also need to make some tests and refresh my memory on how to make tests for a GUI. I also want to look over more merge requests but I don't really have a lot of knowledge on some things so I can't really find errors.