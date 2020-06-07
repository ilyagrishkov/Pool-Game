package nl.tudelft.cse.sem.client.games;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import java.util.ArrayList;
import java.util.List;

import nl.tudelft.cse.sem.client.gamelogic.Table;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.balls.Ball;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.cue.Cue;
import nl.tudelft.cse.sem.client.screens.GameScreen;
import nl.tudelft.cse.sem.client.utils.Vector3f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GameTest {
    @Mock
    transient GameScreen gameScreen;

    @Mock
    transient Table table;

    @Mock
    transient Cue cue;

    @Mock
    transient Ball cueBall;

    transient Game game;

    @BeforeEach
    void before() {
        Game.Builder gameBuilder = new Game.Builder()
                .withGameScreen(gameScreen)
                .withTable(table)
                .withCue(cue)
                .withCueBall(cueBall)
                .withBalls(new ArrayList<>());
        game = mock(Game.class, withSettings()
                .useConstructor(gameBuilder)
                .defaultAnswer(CALLS_REAL_METHODS));
    }

    @Test
    void constructor() {
        List<Ball> balls = new ArrayList<>();
        Ball ball = mock(Ball.class);
        balls.add(ball);
        Game.Builder gameBuilder = new Game.Builder()
                .withGameScreen(gameScreen)
                .withTable(table)
                .withCue(cue)
                .withCueBall(cueBall)
                .withBalls(balls);
        Game game = mock(Game.class, withSettings()
                .useConstructor(gameBuilder)
                .defaultAnswer(CALLS_REAL_METHODS));

        assertEquals(gameScreen, game.getScreen());
        assertEquals(table, game.getTable());
        assertEquals(cue, game.getCue());
        assertEquals(cueBall, game.getCueBall());
        assertEquals(balls, game.getBalls());
    }

    //region addTouchedBall

    @Test
    void addTouchedBall() {
        Ball ball = mock(Ball.class);
        when(ball.getNumber()).thenReturn(1);

        assertEquals(0, game.getTouchedBalls().size());
        game.addTouchedBall(ball);
        assertEquals(1, game.getTouchedBalls().size());
    }

    @Test
    void addTouchedBallTwice() {
        Ball ball = mock(Ball.class);
        when(ball.getNumber()).thenReturn(1);

        assertEquals(0, game.getTouchedBalls().size());
        game.addTouchedBall(ball);
        assertEquals(1, game.getTouchedBalls().size());
        game.addTouchedBall(ball);
        assertEquals(1, game.getTouchedBalls().size());
    }

    //endregion

    @Test
    void pocketBall() {
        Ball ball = mock(Ball.class);

        assertEquals(0, game.getPottedBalls().size());
        game.pocketBall(ball);

        assertTrue(game.isPotted());
        assertEquals(1, game.getPottedBalls().size());
    }

    //region shoot

    @Test
    void shoot1() {
        Vector3f v = mock(Vector3f.class);
        when(cue.getDirection(any())).thenReturn(v);
        when(v.inverse()).thenReturn(v);

        assertTrue(game.isPlayer1());
        assertEquals(0, game.getPlayer1turnCounter());
        assertEquals(0, game.getPlayer2turnCounter());

        game.shoot(4);

        assertFalse(game.isPlayer1());
        assertEquals(1, game.getPlayer1turnCounter());
        assertEquals(0, game.getPlayer2turnCounter());
        assertFalse(game.isSetCue());
        assertTrue(game.isMoveInProgress());
        verify(cueBall, times(1)).shoot(any(), anyFloat());
        verify(cue, times(1)).getDirection(any());
        verify(v, times(1)).inverse();
    }

    @Test
    void shoot2() {
        Vector3f v = mock(Vector3f.class);
        when(cue.getDirection(any())).thenReturn(v);
        when(v.inverse()).thenReturn(v);
        game.shoot(4);

        assertFalse(game.isPlayer1());
        assertEquals(1, game.getPlayer1turnCounter());
        assertEquals(0, game.getPlayer2turnCounter());

        game.shoot(4);

        assertTrue(game.isPlayer1());
        assertEquals(1, game.getPlayer1turnCounter());
        assertEquals(1, game.getPlayer2turnCounter());
        assertFalse(game.isSetCue());
        assertTrue(game.isMoveInProgress());
        verify(cueBall, times(2)).shoot(any(), anyFloat());
        verify(cue, times(2)).getDirection(any());
        verify(v, times(2)).inverse();
    }

    //endregion

    //region processGame

    @Test
    void processGameMoving() {
        Game.Builder gameBuilder = new Game.Builder()
                .withGameScreen(gameScreen)
                .withTable(table)
                .withCue(cue)
                .withCueBall(cueBall)
                .withBalls(new ArrayList<>());
        game = mock(Game.class, withSettings()
                .useConstructor(gameBuilder));
        when(game.noMove()).thenReturn(false);
        doCallRealMethod().when(game).processGame();

        game.processGame();

        verify(game, times(0)).handleTurn();
    }

    @Test
    void processGameNotInProgress() {
        Game.Builder gameBuilder = new Game.Builder()
                .withGameScreen(gameScreen)
                .withTable(table)
                .withCue(cue)
                .withCueBall(cueBall)
                .withBalls(new ArrayList<>());
        game = mock(Game.class, withSettings()
                .useConstructor(gameBuilder));
        doCallRealMethod().when(game).processGame();
        when(game.noMove()).thenReturn(true);
        doNothing().when(gameScreen).displayMessages(anyBoolean(), anyBoolean());
        doNothing().when(game).checkForVictory();
        doNothing().when(gameScreen).renderCue(anyBoolean());
        doNothing().when(game).displayType(gameScreen);

        game.processGame();

        assertFalse(game.isPotted());
        assertFalse(game.isFoul());
        assertFalse(game.isSetCue());
        verify(game, times(0)).handleTurn();
        verify(game, times(1)).checkForVictory();
        verify(gameScreen, times(1)).displayMessages(anyBoolean(), anyBoolean());
        verify(gameScreen, times(1)).renderCue(anyBoolean());
        verify(game, times(1)).displayType(gameScreen);
    }

    @Test
    void processGameInProgress() {
        Game.Builder gameBuilder = new Game.Builder()
                .withGameScreen(gameScreen)
                .withTable(table)
                .withCue(cue)
                .withCueBall(cueBall)
                .withBalls(new ArrayList<>());
        game = mock(Game.class, withSettings()
                .useConstructor(gameBuilder));
        Vector3f v = mock(Vector3f.class);

        when(cue.getDirection(any())).thenReturn(v);
        when(v.inverse()).thenReturn(v);
        doCallRealMethod().when(game).shoot(anyFloat());

        //Sets game in progress
        game.shoot(4);

        doCallRealMethod().when(game).processGame();
        when(game.noMove()).thenReturn(true);
        doNothing().when(gameScreen).displayMessages(anyBoolean(), anyBoolean());
        doNothing().when(game).checkForVictory();
        doNothing().when(gameScreen).renderCue(anyBoolean());
        doNothing().when(game).handleTurn();

        game.processGame();

        assertFalse(game.isMoveInProgress());
        assertFalse(game.isPotted());
        assertFalse(game.isFoul());
        assertFalse(game.isSetCue());
        verify(game, times(1)).handleTurn();
        verify(game, times(1)).checkForVictory();
        verify(gameScreen, times(1)).displayMessages(anyBoolean(), anyBoolean());
        verify(gameScreen, times(1)).renderCue(anyBoolean());
    }

    //endregion

    //region noMove

    @Test
    void noMoveMoving() {
        List<Ball> balls = new ArrayList<>();
        Ball ball = mock(Ball.class);
        balls.add(ball);

        when(table.getBalls()).thenReturn(balls);
        when(ball.isMoving()).thenReturn(true);

        assertFalse(game.noMove());
        assertTrue(game.isSetCue());
    }

    @Test
    void noMove() {
        List<Ball> balls = new ArrayList<>();
        Ball ball = mock(Ball.class);
        balls.add(ball);

        when(table.getBalls()).thenReturn(balls);
        when(ball.isMoving()).thenReturn(false);

        assertTrue(game.noMove());
    }

    //endregion
}
