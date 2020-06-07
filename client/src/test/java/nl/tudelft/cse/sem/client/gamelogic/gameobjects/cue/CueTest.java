package nl.tudelft.cse.sem.client.gamelogic.gameobjects.cue;

import static nl.tudelft.cse.sem.client.utils.Constants.BALL_MASS;
import static nl.tudelft.cse.sem.client.utils.Constants.BALL_RADIUS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.GdxNativesLoader;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.balls.Ball;
import nl.tudelft.cse.sem.client.utils.Vector3f;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

//Mocking is used to avoid a lot of the math tested by other tests.
//This is to make the tests less flaky.
@ExtendWith(MockitoExtension.class)
class CueTest {

    private transient Cue cue;
    private transient Cue mockCue;

    private transient Ball ball;

    private transient btRigidBody.btRigidBodyConstructionInfo constructionInfo;

    @BeforeAll
    static void setupLib() {
        GdxNativesLoader.load();
    }

    @BeforeEach
    void setUp() {
        mockCue = mock(Cue.class);
        cue = new Cue(new Model());

        Bullet.init();

        btSphereShape shape = new btSphereShape(BALL_RADIUS);
        Vector3 localInertia = new Vector3();

        shape.calculateLocalInertia(BALL_MASS, localInertia);
        constructionInfo = new btRigidBody.btRigidBodyConstructionInfo(BALL_MASS, null,
                shape, localInertia);

        ball = new Ball(new Model(), constructionInfo, 0);
    }

    @Test
    void getStrength() {
        cue.transform.setTranslation(-50, -50, 0);
        assertThat((int)cue.getStrength(ball)).isEqualTo(17);
    }

    @Test
    void getIntersectionTest() {
        PerspectiveCamera camera = mock(PerspectiveCamera.class);
        Ray ray = new Ray(new Vector3(1, 1, 1), new Vector3(1, 1, 1));
        when(camera.getPickRay(10, 10)).thenReturn(ray);

        Vector3f vector = cue.getIntersectionWithPlane(camera, 10, 10);

        assertEquals(vector.getX(), 0, 0.1);
        assertEquals(vector.getY(), 0, 0.1);
        assertEquals(vector.getZ(), 0, 0.1);
    }

    @Test
    void setPosition() {
        when(mockCue.getIntersectionWithPlane(any(), anyInt(), anyInt()))
                .thenReturn(new Vector3f(0, 0, 0));
        when(mockCue.getPosition()).thenCallRealMethod();
        doCallRealMethod().when(mockCue).setPosition(any(), anyInt(), anyInt());

        mockCue.setPosition(null, 10, 10);

        assertEquals(mockCue.getPosition().getX(), 0);
        assertEquals(mockCue.getPosition().getY(), 0);
        assertEquals(mockCue.getPosition().getZ(), 3);
    }

    @Test
    void getCueBallPosition() {
        when(mockCue.getIntersectionWithPlane(any(), anyInt(), anyInt()))
                .thenReturn(new Vector3f(0, 0, 0));
        doCallRealMethod().when(mockCue).getCueBallPosition(any(), anyInt(), anyInt());

        Vector3 res = mockCue.getCueBallPosition(null, 10, 10);

        assertEquals(res.x, 0, 0.1);
        assertEquals(res.y, 0, 0.1);
        assertEquals(res.z, 3, 0.1);
    }

    @Test
    void getDirection() {
        assertThat(cue.getDirection()).isEqualTo(new Vector3f(0, 0, 0));
    }

    @Test
    void ballDirection() {
        when(mockCue.getCenter()).thenReturn(new Vector3(1,0,1));
        when(mockCue.getDirection(any())).thenCallRealMethod();

        assertEquals(mockCue.getDirection(ball), new Vector3f(1,0,0));
    }

    @Test
    void strikeNegativeDelta() {
        Vector3 oldPos = new Vector3();
        cue.transform.getTranslation(oldPos);

        Ray ray = mock(Ray.class, withSettings()
                .useConstructor(new Vector3(1, 2, 3),
                        new Vector3(5, 5, 5)));
        PerspectiveCamera cam = mock(PerspectiveCamera.class);
        when(cam.getPickRay(Mockito.anyFloat(), Mockito.anyFloat()))
                .thenReturn(ray);

        cue.strike(cam, 1, 1, ball);

        Vector3 newPos = new Vector3();
        cue.transform.getTranslation(newPos);

        assertThat(oldPos).isEqualTo(newPos);
    }

    @Test
    void strikePositive() {
        when(mockCue.getIntersectionWithPlane(any(), anyInt(), anyInt()))
                .thenReturn(new Vector3f(5,5,5));
        when(mockCue.getStrength(any())).thenReturn(1f);
        when(mockCue.getDirection(any())).thenReturn(new Vector3f(1,1,1));
        doCallRealMethod().when(mockCue).setPosition(any());
        doCallRealMethod().when(mockCue).strike(any(), anyInt(), anyInt(), any());
        doCallRealMethod().when(mockCue).setDirection(any());
        mockCue.setPosition(new Vector3f(3,3,3));
        mockCue.setDirection(new Vector3f(1, 1, 1));
        mockCue.transform = new Matrix4();

        mockCue.strike(null, 10, 10, ball);

        verify(mockCue, times(1)).calculateTransforms();
    }


    @Test
    void rotateCue() {
        //Fix transform having a value
        mockCue.transform = new Matrix4();
        when(mockCue.getDirection(any())).thenReturn(new Vector3f(1,0,0));
        when(mockCue.getIntersectionWithPlane(any(), anyInt(), anyInt()))
                .thenReturn(new Vector3f(0,1,0));
        doCallRealMethod().when(mockCue).rotate(any(), any(), anyInt(), anyInt());

        mockCue.rotate(ball, null, 10, 10);
        float[] values = mockCue.transform.getValues();
        assertEquals(values[0], 0, 0.1);
        assertEquals(values[1], 1, 0.1);
        assertEquals(values[4], -1, 0.1);
        assertEquals(values[5], 0, 0.1);
    }

    @Test
    void rotateCue2() {
        //Fix transform having a value
        mockCue.transform = new Matrix4();
        when(mockCue.getDirection(any())).thenReturn(new Vector3f(-1,0,0));
        when(mockCue.getIntersectionWithPlane(any(), anyInt(), anyInt()))
                .thenReturn(new Vector3f(0,1,0));
        doCallRealMethod().when(mockCue).rotate(any(), any(), anyInt(), anyInt());

        mockCue.rotate(ball, null, 10, 10);
        float[] values = mockCue.transform.getValues();
        assertEquals(values[0], 0, 0.1);
        assertEquals(values[1], -1, 0.1);
        assertEquals(values[4], 1, 0.1);
        assertEquals(values[5], 0, 0.1);
    }

    @Test
    void noRotate() {
        //Fix transform having a value
        mockCue.transform = new Matrix4();
        when(mockCue.getDirection(any())).thenReturn(new Vector3f(0,1,0));
        when(mockCue.getIntersectionWithPlane(any(), anyInt(), anyInt()))
                .thenReturn(new Vector3f(0,1,0));
        doCallRealMethod().when(mockCue).rotate(any(), any(), anyInt(), anyInt());

        mockCue.rotate(ball, null, 10, 10);
        float[] values = mockCue.transform.getValues();
        assertEquals(values[0], 1, 0.1);
        assertEquals(values[1], 0, 0.1);
        assertEquals(values[4], 0, 0.1);
        assertEquals(values[5], 1, 0.1);
    }
}