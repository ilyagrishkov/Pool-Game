package nl.tudelft.cse.sem.client.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.balls.Ball;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.cue.Cue;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.helpline.HelpLine;
import nl.tudelft.cse.sem.client.games.Game;
import nl.tudelft.cse.sem.client.screens.GameScreen;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InputHandlerTest {

    transient InputHandler inputHandler;
    transient GameScreen gameScreen;
    transient PhysicsEngine physicsEngine;

    @BeforeEach
    void before() {
        gameScreen = mock(GameScreen.class);
        physicsEngine = mock(PhysicsEngine.class);
        when(gameScreen.getPhysicsEngine()).thenReturn(physicsEngine);
        inputHandler = new InputHandler(gameScreen);
        when(gameScreen.getPhysicsEngine()).thenReturn(physicsEngine);
    }

    @Test
    void constructor() {
        assertEquals(inputHandler.gameScreen, gameScreen);
        assertEquals(inputHandler.physicsEngine, physicsEngine);
    }

    @Test
    void keyMethodsEmpty() {
        assertFalse(inputHandler.keyDown(1));
        assertFalse(inputHandler.keyTyped('a'));
        assertFalse(inputHandler.keyUp(1));
        assertFalse(inputHandler.scrolled(1));
    }

    //region touchDown

    @Test
    void touchDownMoving() {
        Game game = mock(Game.class);
        when(gameScreen.getGame()).thenReturn(game);
        when(gameScreen.getMoveCueBall()).thenReturn(true);
        when(game.noMove()).thenReturn(false);

        assertTrue(inputHandler.touchDown(0,0,0,0));

        verify(physicsEngine, times(0)).getCue();
    }

    @Test
    void onlyCueMoving() {
        Game game = mock(Game.class);
        when(gameScreen.getGame()).thenReturn(game);
        when(gameScreen.getMoveCueBall()).thenReturn(true);
        when(game.noMove()).thenReturn(true);

        assertTrue(inputHandler.touchDown(0,0,0,0));

        verify(physicsEngine, times(0)).getCue();
    }

    @Test
    void onlyBallMoving() {
        Game game = mock(Game.class);
        when(gameScreen.getGame()).thenReturn(game);
        when(gameScreen.getMoveCueBall()).thenReturn(false);
        when(game.noMove()).thenReturn(false);

        assertTrue(inputHandler.touchDown(0,0,0,0));

        verify(physicsEngine, times(0)).getCue();
    }

    @Test
    void noMovement() {
        Game game = mock(Game.class);
        when(gameScreen.getGame()).thenReturn(game);
        when(gameScreen.getMoveCueBall()).thenReturn(false);
        when(game.noMove()).thenReturn(true);

        Cue cue = mock(Cue.class);
        when(cue.getDirection(any())).thenReturn(new Vector3f(1,1,1));
        when(physicsEngine.getCue()).thenReturn(cue);

        assertTrue(inputHandler.touchDown(1,1,1,1));

        verify(cue).setPosition(any(), anyInt(), anyInt());
        verify(cue).setDirection(any());
    }

    //endregion

    //region touchUp

    @Test
    void touchUpMovingCue() {
        Game game = mock(Game.class);
        when(gameScreen.getGame()).thenReturn(game);
        when(gameScreen.getMoveCueBall()).thenReturn(true);
        when(game.noMove()).thenReturn(true);

        Ball ball = mock(Ball.class);
        when(physicsEngine.getCueBall()).thenReturn(ball);

        btDynamicsWorld dynamicsWorld = mock(btDynamicsWorld.class);
        when(physicsEngine.getDynamicsWorld()).thenReturn(dynamicsWorld);

        assertTrue(inputHandler.touchUp(1,1,1,1));

        verify(game, times(0)).shoot(anyFloat());
        verify(gameScreen).setMoveCueBall(false);
        verify(dynamicsWorld).addRigidBody(null);
        verify(gameScreen).renderCue(true);
    }

    @Test
    void touchUpAllMoving() {
        Game game = mock(Game.class);
        when(gameScreen.getGame()).thenReturn(game);
        when(gameScreen.getMoveCueBall()).thenReturn(true);
        when(game.noMove()).thenReturn(false);

        Ball ball = mock(Ball.class);
        when(physicsEngine.getCueBall()).thenReturn(ball);

        btDynamicsWorld dynamicsWorld = mock(btDynamicsWorld.class);
        when(physicsEngine.getDynamicsWorld()).thenReturn(dynamicsWorld);

        assertTrue(inputHandler.touchUp(1,1,1,1));

        verify(game, times(0)).shoot(anyFloat());
        verify(gameScreen).setMoveCueBall(false);
        verify(dynamicsWorld).addRigidBody(null);
        verify(gameScreen).renderCue(true);
    }

    @Test
    void touchUpOnlyBallsMoving() {
        Game game = mock(Game.class);
        when(gameScreen.getGame()).thenReturn(game);
        when(gameScreen.getMoveCueBall()).thenReturn(false);
        when(game.noMove()).thenReturn(false);

        Ball ball = mock(Ball.class);
        when(physicsEngine.getCueBall()).thenReturn(ball);

        btDynamicsWorld dynamicsWorld = mock(btDynamicsWorld.class);
        when(physicsEngine.getDynamicsWorld()).thenReturn(dynamicsWorld);

        assertTrue(inputHandler.touchUp(1,1,1,1));

        verify(game, times(0)).shoot(anyFloat());
        verify(gameScreen, times(0)).setMoveCueBall(false);
        verify(dynamicsWorld, times(0)).addRigidBody(null);
        verify(gameScreen, times(0)).renderCue(true);
    }

    @Test
    void touchUpNoMovingOverSpeedLimit() {
        Game game = mock(Game.class);
        when(gameScreen.getGame()).thenReturn(game);
        when(gameScreen.getMoveCueBall()).thenReturn(false);
        when(game.noMove()).thenReturn(true);

        Cue cue = mock(Cue.class);
        when(physicsEngine.getCue()).thenReturn(cue);
        when(cue.getStrength(any())).thenReturn(10000f);

        assertTrue(inputHandler.touchUp(1,1,1,1));

        verify(game).shoot(anyFloat());
        verify(gameScreen).setShowFoul(false);
    }

    @Test
    void touchUpNoMovingUnderThreshold() {
        Game game = mock(Game.class);
        when(gameScreen.getGame()).thenReturn(game);
        when(gameScreen.getMoveCueBall()).thenReturn(false);
        when(game.noMove()).thenReturn(true);

        Cue cue = mock(Cue.class);
        when(physicsEngine.getCue()).thenReturn(cue);
        when(cue.getStrength(any())).thenReturn(0f);

        assertTrue(inputHandler.touchUp(1,1,1,1));

        verify(game, times(0)).shoot(anyFloat());
        verify(gameScreen, times(0)).setShowFoul(false);
    }

    //endregion

    //region touchDragged
    @Test
    void touchDraggedNoMovement() {
        Game game = mock(Game.class);
        when(gameScreen.getGame()).thenReturn(game);
        when(gameScreen.getMoveCueBall()).thenReturn(false);
        when(game.noMove()).thenReturn(true);

        Cue cue = mock(Cue.class);
        when(physicsEngine.getCue()).thenReturn(cue);

        assertTrue(inputHandler.touchDragged(1,1,1));

        verify(cue).strike(null, 1, 1, null);
    }

    @Test
    void touchDraggedNoCueMove() {
        Game game = mock(Game.class);
        when(gameScreen.getGame()).thenReturn(game);
        when(gameScreen.getMoveCueBall()).thenReturn(false);
        when(game.noMove()).thenReturn(false);

        Cue cue = mock(Cue.class);
        when(physicsEngine.getCue()).thenReturn(cue);

        assertTrue(inputHandler.touchDragged(1,1,1));

        verify(cue, times(0)).strike(null, 1, 1, null);
    }

    @Test
    void touchDraggedNoBallMove() {
        Game game = mock(Game.class);
        when(gameScreen.getGame()).thenReturn(game);
        when(gameScreen.getMoveCueBall()).thenReturn(true);
        when(game.noMove()).thenReturn(true);

        Cue cue = mock(Cue.class);
        when(physicsEngine.getCue()).thenReturn(cue);

        assertTrue(inputHandler.touchDragged(1,1,1));

        verify(cue, times(0)).strike(null, 1, 1, null);
    }


    @Test
    void touchDraggedAllMove() {
        Game game = mock(Game.class);
        when(gameScreen.getGame()).thenReturn(game);
        when(gameScreen.getMoveCueBall()).thenReturn(true);
        when(game.noMove()).thenReturn(false);

        Cue cue = mock(Cue.class);
        when(physicsEngine.getCue()).thenReturn(cue);

        assertTrue(inputHandler.touchDragged(1,1,1));

        verify(cue, times(0)).strike(null, 1, 1, null);
    }


    @Test
    void mouseMovedCueBallMoving() {
        when(gameScreen.getMoveCueBall()).thenReturn(true);

        Cue cue = mock(Cue.class);
        HelpLine helpLine = mock(HelpLine.class);
        when(physicsEngine.getCue()).thenReturn(cue);
        when(physicsEngine.getHelpLine()).thenReturn(helpLine);

        Ball ball = mock(Ball.class);
        when(physicsEngine.getCueBall()).thenReturn(ball);

        btDynamicsWorld dynamicsWorld = mock(btDynamicsWorld.class);
        when(physicsEngine.getDynamicsWorld()).thenReturn(dynamicsWorld);

        assertTrue(inputHandler.mouseMoved(1,1));

        verify(cue, times(0)).rotate(any(), any(), anyInt(), anyInt());
        verify(helpLine, times(0)).rotate(any(), any(), anyInt(), anyInt());

        verify(dynamicsWorld).removeRigidBody(any());
        verify(gameScreen).setCueBallPos(any());
    }

    @Test
    void mouseMovedCueBallNotMoving() {
        when(gameScreen.getMoveCueBall()).thenReturn(false);

        Cue cue = mock(Cue.class);
        HelpLine helpLine = mock(HelpLine.class);
        when(physicsEngine.getCue()).thenReturn(cue);
        when(physicsEngine.getHelpLine()).thenReturn(helpLine);

        Ball ball = mock(Ball.class);
        when(physicsEngine.getCueBall()).thenReturn(ball);
        when(ball.getCenter()).thenReturn(new Vector3(1, 1, 1));

        btDynamicsWorld dynamicsWorld = mock(btDynamicsWorld.class);
        when(physicsEngine.getDynamicsWorld()).thenReturn(dynamicsWorld);

        assertTrue(inputHandler.mouseMoved(1,1));

        verify(cue, times(1)).rotate(any(), any(), anyInt(), anyInt());
        verify(helpLine, times(1)).rotate(any(), any(), anyInt(), anyInt());

        verify(dynamicsWorld, times(0)).removeRigidBody(any());
        verify(gameScreen, times(0)).setCueBallPos(any());
    }

}
