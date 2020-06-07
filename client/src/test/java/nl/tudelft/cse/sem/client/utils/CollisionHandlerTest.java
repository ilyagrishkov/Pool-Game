package nl.tudelft.cse.sem.client.utils;

import static nl.tudelft.cse.sem.client.utils.Constants.BALL_USER_VALUE;
import static nl.tudelft.cse.sem.client.utils.Constants.POCKET_VALUE;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import nl.tudelft.cse.sem.client.gamelogic.Table;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.balls.Ball;
import nl.tudelft.cse.sem.client.games.Game;
import nl.tudelft.cse.sem.client.screens.GameScreen;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CollisionHandlerTest {

    transient CollisionHandler collisionHandler;
    transient GameScreen gameScreen;
    transient PhysicsEngine physicsEngine;

    @BeforeAll
    static void setUp() {
        Bullet.init();
    }

    @BeforeEach
    void before() {
        gameScreen = mock(GameScreen.class);
        physicsEngine = mock(PhysicsEngine.class);
        when(gameScreen.getPhysicsEngine()).thenReturn(physicsEngine);
        collisionHandler = new CollisionHandler(gameScreen);
    }

    @Test
    void handlePocketCollision() {
        Game game = mock(Game.class);
        when(gameScreen.getGame()).thenReturn(game);

        Ball cueBall = mock(Ball.class);
        when(physicsEngine.getCueBall()).thenReturn(cueBall);

        assertTrue(collisionHandler.onContactAdded(POCKET_VALUE, 0,0,true,
                BALL_USER_VALUE, 0,0,true));

        verify(game, times(1)).setFoul(true);
        verify(cueBall, times(1)).resetCueBall();
    }

    @Test
    void movePocketedBall() {
        Game game = mock(Game.class);
        when(gameScreen.getGame()).thenReturn(game);

        Ball ball = mock(Ball.class);
        when(physicsEngine.getCueBall()).thenReturn(ball);
        ball.transform = new Matrix4();

        Table table = mock(Table.class);
        when(physicsEngine.getTable()).thenReturn(table);
        when(table.getBall(anyInt())).thenReturn(ball);

        btRigidBody body = mock(btRigidBody.class);
        when(ball.getBody()).thenReturn(body);

        btDynamicsWorld dynamicsWorld = mock(btDynamicsWorld.class);
        when(physicsEngine.getDynamicsWorld()).thenReturn(dynamicsWorld);

        collisionHandler.onContactAdded(BALL_USER_VALUE + 1, 0,0,true,
                POCKET_VALUE, 0,0, true);

        verify(game, times(1)).pocketBall(ball);
        verify(body,times(1)).setLinearVelocity(any());
        verify(dynamicsWorld, times(1)).removeRigidBody(body);

        verify(game, times(0)).setFoul(true);
    }

    @Test
    void touchBall1() {
        Game game = mock(Game.class);
        when(gameScreen.getGame()).thenReturn(game);

        Table table = mock(Table.class);
        when(physicsEngine.getTable()).thenReturn(table);

        collisionHandler.onContactAdded(BALL_USER_VALUE + 1, 0, 0, true,
                BALL_USER_VALUE + 1, 0, 0, false);

        verify(table).getBall(1);
        verify(game).addTouchedBall(any());
    }

    @Test
    void touchBall2() {
        Game game = mock(Game.class);
        when(gameScreen.getGame()).thenReturn(game);

        Table table = mock(Table.class);
        when(physicsEngine.getTable()).thenReturn(table);

        collisionHandler.onContactAdded(BALL_USER_VALUE + 1, 0, 0, false,
                BALL_USER_VALUE + 1, 0, 0, true);

        verify(table).getBall(1);
        verify(game).addTouchedBall(any());
    }
}
