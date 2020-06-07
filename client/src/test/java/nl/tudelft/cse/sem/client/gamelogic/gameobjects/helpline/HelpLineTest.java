package nl.tudelft.cse.sem.client.gamelogic.gameobjects.helpline;

import static nl.tudelft.cse.sem.client.utils.Constants.BALL_MASS;
import static nl.tudelft.cse.sem.client.utils.Constants.BALL_RADIUS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.GdxNativesLoader;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.balls.Ball;
import nl.tudelft.cse.sem.client.utils.Vector3f;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class HelpLineTest {

    private transient HelpLine helpLine;
    private transient HelpLine mockLine;

    private transient Ball ball;
    private transient Ball mockBall;

    private transient btRigidBody.btRigidBodyConstructionInfo constructionInfo;

    @BeforeAll
    static void setupLib() {
        GdxNativesLoader.load();
    }

    @BeforeEach
    void setUp() {
        helpLine = new HelpLine(new Model());
        mockLine = mock(HelpLine.class);
        mockBall = mock(Ball.class);

        Bullet.init();

        btSphereShape shape = new btSphereShape(BALL_RADIUS);
        Vector3 localInertia = new Vector3();

        shape.calculateLocalInertia(BALL_MASS, localInertia);
        constructionInfo = new btRigidBody.btRigidBodyConstructionInfo(BALL_MASS, null,
                shape, localInertia);

        ball = new Ball(new Model(), constructionInfo, 0);
    }

    @Test
    void getDirection() {
        when(mockBall.getCenter()).thenReturn(new Vector3(1, 0, 0));
        assertThat(helpLine.getDirection(mockBall)).isEqualTo(new Vector3f(1, 0, 0));
    }

    @Test
    void rotateHelpLine() {
        mockLine.transform = new Matrix4();
        when(mockLine.getDirection(any())).thenReturn(new Vector3f(1, 0, 0));
        when(mockLine.getIntersectionWithPlane(any(), anyInt(), anyInt()))
                .thenReturn(new Vector3f(0, 1, 0));
        doCallRealMethod().when(mockLine).rotate(any(), any(), anyInt(), anyInt());

        mockLine.rotate(ball, null, 10, 10);

        float[] values = mockLine.transform.getValues();
        assertEquals(values[0], 0, 0.1);
        assertEquals(values[1], 1, 0.1);
        assertEquals(values[4], -1, 0.1);
        assertEquals(values[5], 0, 0.1);
    }

    @Test
    void rotateHelpLine2() {
        //Fix transform having a value
        mockLine.transform = new Matrix4();
        when(mockLine.getDirection(any())).thenReturn(new Vector3f(-1, 0, 0));
        when(mockLine.getIntersectionWithPlane(any(), anyInt(), anyInt()))
                .thenReturn(new Vector3f(0, 1, 0));
        doCallRealMethod().when(mockLine).rotate(any(), any(), anyInt(), anyInt());

        mockLine.rotate(ball, null, 10, 10);
        float[] values = mockLine.transform.getValues();
        assertEquals(values[0], 0, 0.1);
        assertEquals(values[1], -1, 0.1);
        assertEquals(values[4], 1, 0.1);
        assertEquals(values[5], 0, 0.1);
    }

    @Test
    void noRotate() {
        //Fix transform having a value
        mockLine.transform = new Matrix4();
        when(mockLine.getDirection(any())).thenReturn(new Vector3f(0, 1, 0));
        when(mockLine.getIntersectionWithPlane(any(), anyInt(), anyInt()))
                .thenReturn(new Vector3f(0, 1, 0));
        doCallRealMethod().when(mockLine).rotate(any(), any(), anyInt(), anyInt());

        mockLine.rotate(ball, null, 10, 10);
        float[] values = mockLine.transform.getValues();
        assertEquals(values[0], 1, 0.1);
        assertEquals(values[1], 0, 0.1);
        assertEquals(values[4], 0, 0.1);
        assertEquals(values[5], 1, 0.1);
    }
}