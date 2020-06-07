package nl.tudelft.cse.sem.client.gamelogic.gameobjects.balls;

import static nl.tudelft.cse.sem.client.utils.Constants.BALL_FLAG;
import static nl.tudelft.cse.sem.client.utils.Constants.CUE_BALL_FLAG;
import static nl.tudelft.cse.sem.client.utils.Constants.EIGHT_BALL_GAME;
import static nl.tudelft.cse.sem.client.utils.Constants.NINE_BALL_GAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btDispatcher;
import com.badlogic.gdx.physics.bullet.dynamics.btConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import java.util.HashMap;
import java.util.Map;

import nl.tudelft.cse.sem.client.gamelogic.gameobjects.util.MaterialLoader;
import nl.tudelft.cse.sem.client.utils.Vector3f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


class BallFactoryTest {

    private transient btDynamicsWorld dynamicsWorld;

    private transient BallFactory ballFactory;

    @BeforeEach
    void setUp() {
        Bullet.init();
        btCollisionConfiguration collisionConfig = new btDefaultCollisionConfiguration();
        btDispatcher dispatcher = new btCollisionDispatcher(collisionConfig);
        btBroadphaseInterface broadPhase = new btDbvtBroadphase();
        btConstraintSolver constraintSolver = new btSequentialImpulseConstraintSolver();
        dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, broadPhase,
                constraintSolver, collisionConfig);

        ballFactory = mock(BallFactory.class, withSettings().useConstructor(dynamicsWorld,
                new MaterialLoader()));

    }

    @Test
    void constructEightBallBalls() {
        when(ballFactory.construct(Mockito.anyInt(), Mockito.anyMap()))
                .thenReturn(mock(Ball.class));
        doCallRealMethod().when(ballFactory).constructBalls(anyInt());
        doCallRealMethod().when(ballFactory).createCentersEightBall();
        doCallRealMethod().when(ballFactory).constructEightBallBalls();

        ballFactory.constructBalls(EIGHT_BALL_GAME);

        verify(ballFactory, times(16))
                .construct(Mockito.anyInt(), Mockito.anyMap());
        verify(ballFactory, times(1)).createCentersEightBall();
    }

    @Test
    void constructNineBallBalls() {
        when(ballFactory.construct(Mockito.anyInt(), Mockito.anyMap()))
                .thenReturn(mock(Ball.class));
        doCallRealMethod().when(ballFactory).constructBalls(anyInt());
        doCallRealMethod().when(ballFactory).createCentersNineBall();
        doCallRealMethod().when(ballFactory).constructNineBallBalls();

        ballFactory.constructBalls(NINE_BALL_GAME);

        verify(ballFactory, times(10)).construct(Mockito.anyInt(), Mockito.anyMap());
        verify(ballFactory, times(1)).createCentersNineBall();
    }

    @Test
    void constructCueBall() {
        when(ballFactory.createModel(Mockito.anyInt())).thenReturn(new Model());
        doCallRealMethod().when(ballFactory).construct(Mockito.anyInt(), Mockito.anyMap());

        Map<Integer, Vector3f> centers = new HashMap<>();
        centers.put(0, new Vector3f(0, 0, 0));

        Ball ball = ballFactory.construct(0, centers);

        assertThat(ball.getNumber()).isEqualTo(0);
        assertThat(ball.getBody().getContactCallbackFlag()).isEqualTo(CUE_BALL_FLAG);
    }

    @Test
    void constructBall() {
        when(ballFactory.createModel(Mockito.anyInt())).thenReturn(new Model());
        doCallRealMethod().when(ballFactory).construct(Mockito.anyInt(), Mockito.anyMap());

        Map<Integer, Vector3f> centers = new HashMap<>();
        centers.put(1, new Vector3f(0, 0, 0));

        Ball ball = ballFactory.construct(1, centers);

        assertThat(ball.getNumber()).isEqualTo(1);
        assertThat(ball.getBody().getContactCallbackFlag()).isEqualTo(BALL_FLAG);
    }
}