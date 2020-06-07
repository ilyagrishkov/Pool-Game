package nl.tudelft.cse.sem.client.games;

import static nl.tudelft.cse.sem.client.utils.Constants.BLACK_BALL;
import static nl.tudelft.cse.sem.client.utils.Constants.SOLIDS_TYPE;
import static nl.tudelft.cse.sem.client.utils.Constants.STRIPES_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
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
public class EightBallTest {
    @Mock
    transient GameScreen gameScreen;

    @Mock
    transient Table table;

    @Mock
    transient Cue cue;

    @Mock
    transient Ball cueBall;

    transient EightBall game;

    @BeforeEach
    void before() {
        Game.Builder gameBuilder = new Game.Builder()
                .withGameScreen(gameScreen)
                .withTable(table)
                .withCue(cue)
                .withCueBall(cueBall)
                .withBalls(new ArrayList<>());
        game = new EightBall(gameBuilder);
    }

    @Test
    void constructor() {
        assertTrue(game.isFirstHit());
        assertFalse(game.isPlayer1lastHit());
        assertFalse(game.isPlayer2lastHit());
        assertFalse(game.isPlayer1lost());
        assertFalse(game.isPlayer2lost());
    }

    //region handleTurn

    @Test
    void noBallHit() {
        game.setFirstHit(false);
        game.handleTurn();
        assertTrue(game.isFoul());
    }

    //region BlackBall
    @Test
    void correctBlackBallPot() {
        game.setFirstHit(false);
        game.getTouchedBalls().add(8);
        game.setPlayer1(false);
        game.setPlayer1lastHit(true);
        game.setPlayer1type(SOLIDS_TYPE);

        Ball ball = mock(Ball.class);
        when(ball.getType()).thenReturn(BLACK_BALL);

        game.pocketBall(ball);
        game.handleTurn();

        assertFalse(game.isPlayer1lost());
        assertFalse(game.isPlayer2lost());
    }

    @Test
    void player1BlackHitWrong() {
        game.getTouchedBalls().add(8);

        game.handleTurn();

        assertTrue(game.isFoul());
    }

    @Test
    void player2BlackHitWrong() {
        game.getTouchedBalls().add(8);
        game.setPlayer1(false);

        game.handleTurn();

        assertTrue(game.isFoul());
    }

    @Test
    void blackHitCorrect1() {
        game.setPlayer1(false);
        game.setPlayer1lastHit(true);
        game.getTouchedBalls().add(8);

        game.handleTurn();

        assertFalse(game.isFoul());
    }

    @Test
    void blackHitCorrect2() {
        game.setPlayer2lastHit(true);
        game.getTouchedBalls().add(8);

        game.handleTurn();

        assertFalse(game.isFoul());
    }

    @Test
    void hitWhenOtherOnLast1() {
        game.setPlayer1(false);
        game.setPlayer2lastHit(true);
        game.getTouchedBalls().add(8);

        game.handleTurn();

        assertTrue(game.isFoul());
    }

    @Test
    void hitWhenOtherOnLast2() {
        game.setPlayer1lastHit(true);
        game.getTouchedBalls().add(8);

        game.handleTurn();

        assertTrue(game.isFoul());
    }

    @Test
    void nonEightBall() {
        game.setPlayer1lastHit(false);
        game.getTouchedBalls().add(3);
        game.setPlayer1type(SOLIDS_TYPE);

        game.handleTurn();

        assertFalse(game.isFoul());
    }

    @Test
    void potBlackAndWhite() {
        Ball ball = mock(Ball.class);
        when(ball.getType()).thenReturn(BLACK_BALL);
        game.setPlayer1lastHit(true);
        game.setPlayer1(false); //Turn switch
        game.setFoul(true);

        game.pocketBall(ball);
        game.handleTurn();

        assertTrue(game.isPlayer1lost());
    }

    @Test
    void potBlackAndWhite2() {
        Ball ball = mock(Ball.class);
        when(ball.getType()).thenReturn(BLACK_BALL);
        game.setPlayer2lastHit(true);
        game.setFoul(true);

        game.pocketBall(ball);
        game.handleTurn();

        assertTrue(game.isPlayer2lost());
    }
    //endregion

    @Test
    void distributeBalls() {
        Ball solid = mock(Ball.class);
        Ball stripes = mock(Ball.class);
        when(solid.getType()).thenReturn(SOLIDS_TYPE);
        when(stripes.getType()).thenReturn(STRIPES_TYPE);
        game.getPottedBalls().add(solid);
        game.getPottedBalls().add(solid);
        game.getPottedBalls().add(stripes);
        game.setPlayer1type(SOLIDS_TYPE);

        assertEquals(0, game.getPlayer1balls().size());
        assertEquals(0, game.getPlayer2balls().size());

        game.handleTurn();

        assertEquals(2, game.getPlayer1balls().size());
        assertEquals(1, game.getPlayer2balls().size());
    }

    //region foul check

    @Test
    void firstHit() {
        game.getTouchedBalls().add(3);
        game.handleTurn();
        assertFalse(game.isFoul());
    }

    @Test
    void blackFirst() {
        game.getPlayer2balls().add(1);
        game.getTouchedBalls().add(8);
        game.setPlayer2lastHit(true);

        game.handleTurn();

        assertFalse(game.isFoul());
    }

    @Test
    void wrongTypeHit1() {
        game.setPotted(true);
        disableFirstHitCheck();
        add2Balls();
        //Players are swapped
        game.setPlayer2type(STRIPES_TYPE);

        game.handleTurn();

        assertTrue(game.isFoul());
    }

    @Test
    void wrongTypeHit2() {
        disableFirstHitCheck();
        add2Balls();
        game.setPlayer1(false);
        game.setPlayer1type(STRIPES_TYPE);

        game.handleTurn();

        assertTrue(game.isFoul());
    }

    @Test
    void correctHitType1() {
        disableFirstHitCheck();
        add2Balls();
        //Players are swapped
        game.setPlayer2type(SOLIDS_TYPE);

        game.handleTurn();

        assertFalse(game.isFoul());
    }

    @Test
    void correctHitType2() {
        disableFirstHitCheck();
        add2Balls();
        //Players are swapped
        game.setPlayer1(false);
        game.setPlayer1type(SOLIDS_TYPE);

        game.handleTurn();

        assertFalse(game.isFoul());
    }

    @SuppressWarnings("PMD")//Error that this is not a test case
    void disableFirstHitCheck() {
        game.setFirstHit(false);
        game.getPlayer2balls().add(1);
    }

    @SuppressWarnings("PMD")//Error that this is not a test case
    void add2Balls() {
        game.getTouchedBalls().add(1);
        Ball ball = mock(Ball.class);
        when(ball.getType()).thenReturn(SOLIDS_TYPE);
        game.getBalls().add(cueBall);
        game.getBalls().add(ball);
    }

    //endregion

    @Test
    void swapPlayers() {
        disableFirstHitCheck();
        add2Balls();
        //Players are swapped
        game.setPlayer2type(SOLIDS_TYPE);
        game.setPotted(true);

        assertTrue(game.isPlayer1());

        game.handleTurn();

        assertFalse(game.isPlayer1());
        assertFalse(game.isFoul());
    }

    @Test
    void swapPlayers2() {
        disableFirstHitCheck();
        add2Balls();
        //Players are swapped
        game.setPlayer1(false);
        game.setPlayer1type(SOLIDS_TYPE);
        game.setPotted(true);

        assertFalse(game.isPlayer1());

        game.handleTurn();

        assertTrue(game.isPlayer1());
        assertFalse(game.isFoul());
    }

    @Test
    void setLastHits() {
        game.setFirstHit(false);
        for (int i = 0; i < 7; i++) {
            game.getPlayer1balls().add(i);
            game.getPlayer2balls().add(8 + i);
        }

        assertFalse(game.isPlayer1lastHit());
        assertFalse(game.isPlayer2lastHit());

        game.handleTurn();

        assertTrue(game.isPlayer1lastHit());
        assertTrue(game.isPlayer2lastHit());
    }

    //region firstHitCheck

    @Test
    void firstHitCheckBothTrue() {
        game.setFirstHit(false);
        game.getPlayer1balls().add(1);
        game.getPlayer2balls().add(10);
        add2Balls();
        //Players are swapped
        game.setPlayer2type(STRIPES_TYPE);

        game.handleTurn();

        assertTrue(game.isFoul());
    }

    @Test
    void firstHitCheck1True() {
        game.setFirstHit(false);
        game.getPlayer1balls().add(1);
        add2Balls();
        //Players are swapped
        game.setPlayer2type(STRIPES_TYPE);

        game.handleTurn();

        assertTrue(game.isFoul());
    }

    @Test
    void firstHitCheck2True() {
        game.setFirstHit(false);
        game.getPlayer2balls().add(10);
        add2Balls();
        //Players are swapped
        game.setPlayer2type(STRIPES_TYPE);

        game.handleTurn();

        assertTrue(game.isFoul());
    }

    @Test
    void firstHitCheckBothFalse() {
        game.getTouchedBalls().add(1);
        game.setFirstHit(false);

        game.handleTurn();

        assertFalse(game.isFoul());
    }

    //endregion

    //endregion

    //region checkForVictory

    @Test
    void victoryLost1() {
        game.setPlayer1lost(true);
        doNothing().when(gameScreen).goToScoreScreen(anyInt());
        game.setPlayer2turnCounter(1);
        game.setPlayer1turnCounter(2);

        game.checkForVictory();

        verify(gameScreen, times(1)).goToScoreScreen(3);
    }

    @Test
    void victoryLost2() {
        game.setPlayer2lost(true);
        doNothing().when(gameScreen).goToScoreScreen(anyInt());
        game.setPlayer2turnCounter(1);
        game.setPlayer1turnCounter(2);

        game.checkForVictory();

        verify(gameScreen, times(1)).goToScoreScreen(3);
    }

    @Test
    void victoryWon1() {
        for (int i = 0; i < 8; i++) {
            game.getPlayer1balls().add(i);
        }
        doNothing().when(gameScreen).goToScoreScreen(anyInt());
        game.setPlayer2turnCounter(1);
        game.setPlayer1turnCounter(2);

        game.checkForVictory();

        verify(gameScreen, times(1)).goToScoreScreen(3);
    }

    @Test
    void victoryWon2() {
        for (int i = 0; i < 8; i++) {
            game.getPlayer2balls().add(i);
        }
        doNothing().when(gameScreen).goToScoreScreen(anyInt());
        game.setPlayer2turnCounter(1);
        game.setPlayer1turnCounter(2);

        game.checkForVictory();

        verify(gameScreen, times(1)).goToScoreScreen(3);
    }

    @Test
    void bothWin1() {
        game.setPlayer1lost(true);
        for (int i = 0; i < 8; i++) {
            game.getPlayer1balls().add(i);
        }
        doNothing().when(gameScreen).goToScoreScreen(anyInt());
        game.setPlayer2turnCounter(1);
        game.setPlayer1turnCounter(2);

        game.checkForVictory();

        verify(gameScreen, times(1)).goToScoreScreen(27);
    }

    @Test
    void bothWin2() {
        game.setPlayer1(false);
        game.setPlayer2lost(true);
        for (int i = 0; i < 8; i++) {
            game.getPlayer2balls().add(i);
        }
        doNothing().when(gameScreen).goToScoreScreen(anyInt());
        game.setPlayer2turnCounter(1);
        game.setPlayer1turnCounter(2);

        game.checkForVictory();

        verify(gameScreen, times(1)).goToScoreScreen(27);
    }

    //endregion

    //region pocketBall

    @Test
    void blackBallFoul1() {
        Ball ball = mock(Ball.class);
        when(ball.getType()).thenReturn(BLACK_BALL);

        game.setPlayer1(false);

        game.pocketBall(ball);
        //Keep in mind turn switch happens after cue shot.
        assertTrue(game.isPlayer1lost());
        assertFalse(game.isPlayer2lost());
        assertEquals(0, game.getPottedBalls().size());
    }

    @Test
    void blackBallFoul2() {
        Ball ball = mock(Ball.class);
        when(ball.getType()).thenReturn(BLACK_BALL);

        game.pocketBall(ball);
        assertTrue(game.isPlayer2lost());
        assertFalse(game.isPlayer1lost());
        assertEquals(0, game.getPottedBalls().size());
    }

    @Test
    void blackBallCorrect() {
        Ball ball = mock(Ball.class);
        when(ball.getType()).thenReturn(BLACK_BALL);
        game.setPlayer1lastHit(true);
        game.setPlayer1(false);

        game.pocketBall(ball);
        assertFalse(game.isPlayer1lost());
        assertFalse(game.isPlayer2lost());
    }

    @Test
    void blackBallCorrect2() {
        Ball ball = mock(Ball.class);
        when(ball.getType()).thenReturn(BLACK_BALL);
        game.setPlayer2lastHit(true);

        game.pocketBall(ball);
        assertFalse(game.isPlayer1lost());
        assertFalse(game.isPlayer2lost());
    }

    @Test
    void firstHit1() {
        Ball ball = mock(Ball.class);
        when(ball.getType()).thenReturn(SOLIDS_TYPE);
        when(ball.getOppositeType()).thenReturn(STRIPES_TYPE);

        game.pocketBall(ball);

        //Turn is changed at this point so other way around
        assertEquals(SOLIDS_TYPE, game.getPlayer2type());
        assertEquals(STRIPES_TYPE, game.getPlayer1type());
        assertTrue(game.isPotted());
    }

    @Test
    void firstHit2() {
        Ball ball = mock(Ball.class);
        when(ball.getType()).thenReturn(SOLIDS_TYPE);
        when(ball.getOppositeType()).thenReturn(STRIPES_TYPE);
        game.setPlayer1(false);

        game.pocketBall(ball);

        //Turn is changed at this point so other way around
        assertEquals(SOLIDS_TYPE, game.getPlayer1type());
        assertEquals(STRIPES_TYPE, game.getPlayer2type());
        assertTrue(game.isPotted());
    }

    @Test
    void wrongType1() {
        Ball ball = mock(Ball.class);
        when(ball.getType()).thenReturn(SOLIDS_TYPE);

        game.setFirstHit(false);
        game.setPlayer2type(STRIPES_TYPE);

        game.pocketBall(ball);

        assertFalse(game.isFoul());
        assertFalse(game.isPotted());
    }

    @Test
    void wrongType2() {
        Ball ball = mock(Ball.class);
        when(ball.getType()).thenReturn(SOLIDS_TYPE);

        //Turns are reversed when a ball is potted.
        game.setPlayer1(false);
        game.setFirstHit(false);
        game.setPlayer1type(STRIPES_TYPE);

        game.pocketBall(ball);

        assertFalse(game.isFoul());
        assertFalse(game.isPotted());
    }

    @Test
    void displayTypeTest() {
        game.setPlayer1type(SOLIDS_TYPE);
        game.setPlayer2type(STRIPES_TYPE);

        game.processGame();

        verify(gameScreen, times(1)).displayType(SOLIDS_TYPE, STRIPES_TYPE);
    }
    //endregion


}
