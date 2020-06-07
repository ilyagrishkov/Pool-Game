package nl.tudelft.cse.sem.client.gamelogic;

import static nl.tudelft.cse.sem.client.utils.Constants.BALL_MASS;
import static nl.tudelft.cse.sem.client.utils.Constants.BALL_RADIUS;
import static nl.tudelft.cse.sem.client.utils.Constants.EIGHT_BALL_GAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import java.util.ArrayList;
import java.util.List;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.GameObject;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.balls.Ball;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.balls.BallFactory;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.cue.Cue;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.cue.CueFactory;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.helpline.HelpLine;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.helpline.HelpLineFactory;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.plane.PlaneFactory;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.pockets.PocketFactory;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.walls.Wall;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.walls.WallFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TableTest {

    private transient Table table;

    private transient btRigidBody.btRigidBodyConstructionInfo constructionInfo;

    @BeforeEach
    void setUp() {
        table = mock(Table.class);
        doCallRealMethod().when(table).setBalls(Mockito.anyList());
        doCallRealMethod().when(table).dispose();
        doCallRealMethod().when(table).setWalls(Mockito.anyList());

        List<GameObject> walls = new ArrayList<>();
        walls.add(mock(Wall.class));
        table.setWalls(walls);
        table.setBalls(new ArrayList<>());

        Bullet.init();
        btSphereShape shape = new btSphereShape(BALL_RADIUS);
        Vector3 localInertia = new Vector3();

        shape.calculateLocalInertia(BALL_MASS, localInertia);

        constructionInfo = new btRigidBody.btRigidBodyConstructionInfo(BALL_MASS, null,
                        shape, localInertia);
    }

    @AfterEach
    void tearDown() {
        table.dispose();
    }

    @Test
    void constructTable() {
        BallFactory ballFactory = mock(BallFactory.class);
        List<Ball> ballList = new ArrayList<>();
        when(ballFactory.constructBalls(anyInt())).thenReturn(ballList);

        WallFactory wallFactory = mock(WallFactory.class);
        List<GameObject> wallList = new ArrayList<>();
        when(wallFactory.constructAll()).thenReturn(wallList);

        PlaneFactory planeFactory = mock(PlaneFactory.class);
        GameObject plane = mock(GameObject.class);
        doReturn(plane).when(planeFactory).construct();

        PocketFactory pocketFactory = mock(PocketFactory.class);
        List<GameObject> pocketList = new ArrayList<>();
        when(pocketFactory.constructAll()).thenReturn(pocketList);

        CueFactory cueFactory = mock(CueFactory.class);
        Cue cue = mock(Cue.class);
        when(cueFactory.construct()).thenReturn(cue);

        HelpLineFactory helpLineFactory = mock(HelpLineFactory.class);
        HelpLine helpLine = mock(HelpLine.class);
        when(helpLineFactory.construct()).thenReturn(helpLine);

        Table tableTest = new Table.Builder()
                .ofType(EIGHT_BALL_GAME)
                .withBallFactory(ballFactory)
                .withWallFactory(wallFactory)
                .withPlaneFactory(planeFactory)
                .withPocketFactory(pocketFactory)
                .withCueFactory(cueFactory)
                .withHelpLineFactory(helpLineFactory)
                .build();

        assertEquals(tableTest.getBalls(), ballList);
        assertEquals(tableTest.getWalls(), wallList);
        assertEquals(tableTest.getPockets(), pocketList);
        assertEquals(tableTest.getPlane(), plane);
        assertEquals(tableTest.getCue(), cue);
    }




    @Test
    void getCueBall() {
        doCallRealMethod().when(table).getCueBall();

        List<Ball> balls = new ArrayList<>();
        balls.add(new Ball(new Model(), constructionInfo, 0));
        balls.add(new Ball(new Model(), constructionInfo, 2));
        balls.add(new Ball(new Model(), constructionInfo, 1));

        table.setBalls(balls);

        assertThat(table.getCueBall()).isEqualTo(balls.get(0));
    }

    @Test
    void getBallExisting() {
        doCallRealMethod().when(table).getBall(Mockito.anyInt());

        List<Ball> balls = new ArrayList<>();
        balls.add(new Ball(new Model(), constructionInfo, 0));
        balls.add(new Ball(new Model(), constructionInfo, 2));
        balls.add(new Ball(new Model(), constructionInfo, 1));

        table.setBalls(balls);

        assertThat(table.getBall(2)).isEqualTo(balls.get(1));
    }

    @Test
    void getBallNonExisting() {
        doCallRealMethod().when(table).getBall(Mockito.anyInt());
        List<Ball> balls = new ArrayList<>();
        balls.add(new Ball(new Model(), constructionInfo, 0));
        balls.add(new Ball(new Model(), constructionInfo, 2));
        balls.add(new Ball(new Model(), constructionInfo, 1));

        table.setBalls(balls);

        assertThat(table.getBall(5)).isNull();
    }

    @Test
    void getRenderables() {
        doCallRealMethod().when(table).getRenderables();
        doCallRealMethod().when(table).setPlane(Mockito.any());

        List<Ball> balls = new ArrayList<>();
        balls.add(new Ball(new Model(), constructionInfo, 0));

        table.setBalls(balls);
        table.setPlane(mock(GameObject.class));

        assertThat(table.getRenderables().size()).isEqualTo(2);
    }
}