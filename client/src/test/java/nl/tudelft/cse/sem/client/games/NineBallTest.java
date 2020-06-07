package nl.tudelft.cse.sem.client.games;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import nl.tudelft.cse.sem.client.gamelogic.Table;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.balls.Ball;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.cue.Cue;
import nl.tudelft.cse.sem.client.screens.GameScreen;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class NineBallTest {

    @Mock
    transient GameScreen gameScreen;

    @Mock
    transient Table table;

    @Mock
    transient Cue cue;

    @Mock
    transient Ball cueBall;

    transient NineBall nineBall;

    @BeforeEach
    void before() {
        Game.Builder gameBuilder = new Game.Builder()
                .withGameScreen(gameScreen)
                .withTable(table)
                .withCue(cue)
                .withCueBall(cueBall)
                .withBalls(new ArrayList<>());
        nineBall = new NineBall(gameBuilder);
    }

    @Test
    void constructor() {
        List<Ball> balls = new ArrayList<>();
        balls.add(cueBall);
        Game.Builder gameBuilder = new Game.Builder()
                .withGameScreen(gameScreen)
                .withTable(table)
                .withCue(cue)
                .withCueBall(cueBall)
                .withBalls(balls);
        NineBall nineBall = new NineBall(gameBuilder);

        assertTrue(nineBall.getRemainingBalls().contains(cueBall));
        assertFalse(nineBall.isPlayer1Won());
        assertFalse(nineBall.isPlayer2Won());
        assertEquals(0, nineBall.getPlayer1Potted());
        assertEquals(0, nineBall.getPlayer2Potted());
    }

    //region pocketBall

    @Test
    void potNineBall1() {
        //Switch to player 1 turn as they were switched before
        nineBall.setPlayer1(false);
        Ball ball = mock(Ball.class);
        when(ball.getNumber()).thenReturn(9);
        nineBall.pocketBall(ball);

        assertEquals(1, nineBall.getPlayer1Potted());
        assertEquals(0, nineBall.getPlayer2Potted());
        assertTrue(nineBall.isPlayer1Won());
        assertFalse(nineBall.isPlayer2Won());
    }

    @Test
    void potNineBall2() {
        Ball ball = mock(Ball.class);
        when(ball.getNumber()).thenReturn(9);
        nineBall.pocketBall(ball);

        assertEquals(0, nineBall.getPlayer1Potted());
        assertEquals(1, nineBall.getPlayer2Potted());
        assertFalse(nineBall.isPlayer1Won());
        assertTrue(nineBall.isPlayer2Won());
    }

    @Test
    void potOtherBall() {
        Ball ball = mock(Ball.class);
        when(ball.getNumber()).thenReturn(5);
        nineBall.pocketBall(ball);

        assertEquals(0, nineBall.getPlayer1Potted());
        assertEquals(1, nineBall.getPlayer2Potted());
        assertFalse(nineBall.isPlayer1Won());
        assertFalse(nineBall.isPlayer2Won());
    }
    //endregion

    //region handleTurn

    @Test
    void noBallsPotted() {
        List<Ball> balls = new ArrayList<>();
        balls.add(cueBall);
        Game.Builder gameBuilder = new Game.Builder()
                .withGameScreen(gameScreen)
                .withTable(table)
                .withCue(cue)
                .withCueBall(cueBall)
                .withBalls(balls);
        NineBall nineBall = new NineBall(gameBuilder);
        nineBall.handleTurn();
        assertTrue(nineBall.isFoul());
        //It didnt switch the turn
        assertTrue(nineBall.isPlayer1());
        assertEquals(1, nineBall.getRemainingBalls().size());
    }

    @Test
    void lowestBallHitAndPotted1() {
        Ball lowBall = mock(Ball.class);
        Ball highBall = mock(Ball.class);
        when(lowBall.getNumber()).thenReturn(3);
        when(highBall.getNumber()).thenReturn(9);
        when(cueBall.getNumber()).thenReturn(0);
        List<Ball> balls = new ArrayList<>();
        balls.add(lowBall);
        balls.add(cueBall);
        balls.add(highBall);
        Game.Builder gameBuilder = new Game.Builder()
                .withGameScreen(gameScreen)
                .withTable(table)
                .withCue(cue)
                .withCueBall(cueBall)
                .withBalls(balls);
        NineBall nineBall = new NineBall(gameBuilder);

        nineBall.addTouchedBall(lowBall);
        nineBall.pocketBall(lowBall);
        nineBall.handleTurn();

        assertFalse(nineBall.isFoul());
        //Turn has switched
        assertFalse(nineBall.isPlayer1());
        assertEquals(2, nineBall.getRemainingBalls().size());
        assertTrue(highBall == nineBall.getRemainingBalls().get(0)
                || nineBall.getRemainingBalls().get(0) == cueBall);
    }

    @Test
    void lowestBallHitAndPotted2() {
        List<Ball> balls = new ArrayList<>();
        Ball lowBall = mock(Ball.class);
        Ball highBall = mock(Ball.class);
        when(lowBall.getNumber()).thenReturn(3);
        when(highBall.getNumber()).thenReturn(9);
        balls.add(lowBall);
        balls.add(highBall);
        Game.Builder gameBuilder = new Game.Builder()
                .withGameScreen(gameScreen)
                .withTable(table)
                .withCue(cue)
                .withCueBall(cueBall)
                .withBalls(balls);
        NineBall nineBall = new NineBall(gameBuilder);

        //Set turn to player 2
        nineBall.setPlayer1(false);

        nineBall.addTouchedBall(lowBall);
        nineBall.pocketBall(lowBall);
        nineBall.handleTurn();

        assertFalse(nineBall.isFoul());
        //Turn has switched
        assertTrue(nineBall.isPlayer1());
        assertEquals(1, nineBall.getRemainingBalls().size());
        assertEquals(highBall, nineBall.getRemainingBalls().get(0));
    }

    @Test
    void highBallHitAndPotted() {
        List<Ball> balls = new ArrayList<>();
        Ball lowBall = mock(Ball.class);
        Ball highBall = mock(Ball.class);
        when(lowBall.getNumber()).thenReturn(3);
        when(highBall.getNumber()).thenReturn(9);
        balls.add(lowBall);
        balls.add(highBall);
        Game.Builder gameBuilder = new Game.Builder()
                .withGameScreen(gameScreen)
                .withTable(table)
                .withCue(cue)
                .withCueBall(cueBall)
                .withBalls(balls);
        NineBall nineBall = new NineBall(gameBuilder);

        nineBall.addTouchedBall(highBall);
        nineBall.pocketBall(highBall);
        nineBall.handleTurn();

        assertTrue(nineBall.isFoul());
        //Turn has not been switched
        assertTrue(nineBall.isPlayer1());
        assertEquals(1, nineBall.getRemainingBalls().size());
        assertEquals(lowBall, nineBall.getRemainingBalls().get(0));
    }

    @Test
    void highBallHit() {
        List<Ball> balls = new ArrayList<>();
        Ball lowBall = mock(Ball.class);
        Ball highBall = mock(Ball.class);
        when(lowBall.getNumber()).thenReturn(3);
        when(highBall.getNumber()).thenReturn(9);
        balls.add(lowBall);
        balls.add(highBall);
        Game.Builder gameBuilder = new Game.Builder()
                .withGameScreen(gameScreen)
                .withTable(table)
                .withCue(cue)
                .withCueBall(cueBall)
                .withBalls(balls);
        NineBall nineBall = new NineBall(gameBuilder);

        nineBall.addTouchedBall(highBall);
        nineBall.handleTurn();

        assertTrue(nineBall.isFoul());
        //Turn has not been switched
        assertTrue(nineBall.isPlayer1());
        assertEquals(2, nineBall.getRemainingBalls().size());
    }
    //endregion

    //region checkVictory

    @Test
    void checkVictoryNoFoul1() {
        nineBall.setPlayer1Won(true);
        nineBall.setPlayer1Potted(3);
        nineBall.setPlayer2Potted(4);
        nineBall.setPlayer1turnCounter(5);

        nineBall.checkForVictory();

        verify(gameScreen, times(1)).goToScoreScreen(175);
    }

    @Test
    void checkVictoryNoFoul2() {
        nineBall.setPlayer2Won(true);
        nineBall.setPlayer1Potted(3);
        nineBall.setPlayer2Potted(4);
        nineBall.setPlayer2turnCounter(5);

        nineBall.checkForVictory();

        verify(gameScreen, times(1)).goToScoreScreen(140);
    }

    @Test
    void checkVictoryFoul1() {
        nineBall.setPlayer1Won(true);
        nineBall.setPlayer1Potted(3);
        nineBall.setPlayer2Potted(4);
        nineBall.setPlayer2turnCounter(5);
        nineBall.setFoul(true);

        nineBall.checkForVictory();

        //As player1turnCounter is 0 we can verify like this that player2 won.
        verify(gameScreen, times(1)).goToScoreScreen(140);
    }

    @Test
    void checkVictoryFoul2() {
        nineBall.setPlayer2Won(true);
        nineBall.setPlayer1Potted(3);
        nineBall.setPlayer2Potted(4);
        nineBall.setPlayer1turnCounter(5);
        nineBall.setFoul(true);

        nineBall.checkForVictory();

        //As player2turnCounter is 0 we can verify like this that player1 won.
        verify(gameScreen, times(1)).goToScoreScreen(175);
    }

    @Test
    void checkVictoryNoVictory() {
        nineBall.checkForVictory();
        verify(gameScreen, times(0)).goToScoreScreen(anyInt());
    }

    @Test
    void displayTypeTest() {
        List<Ball> balls = new ArrayList<>();
        Ball ball = mock(Ball.class);
        when(ball.getNumber()).thenReturn(1);
        balls.add(ball);
        Game.Builder gameBuilder = new Game.Builder()
                .withGameScreen(gameScreen)
                .withTable(table)
                .withCue(cue)
                .withCueBall(cueBall)
                .withBalls(balls);
        NineBall nineBall = new NineBall(gameBuilder);

        nineBall.processGame();

        verify(gameScreen, times(1)).displayLowestBall(1);
    }
    //endregion
}
