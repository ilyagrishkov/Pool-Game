package nl.tudelft.cse.sem.client.gamelogic.gameobjects.walls;

import static nl.tudelft.cse.sem.client.utils.Constants.BALL_RADIUS;
import static nl.tudelft.cse.sem.client.utils.Constants.POCKET_SIZE;
import static nl.tudelft.cse.sem.client.utils.Constants.TABLE_HEIGHT;
import static nl.tudelft.cse.sem.client.utils.Constants.TABLE_WIDTH;
import static nl.tudelft.cse.sem.client.utils.Constants.WALL_FLAG;
import static nl.tudelft.cse.sem.client.utils.Constants.WALL_USER_VALUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
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
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.GameObject;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.util.MaterialLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


class WallFactoryTest {

    private transient btDynamicsWorld dynamicsWorld;

    private transient WallFactory wallFactory;

    @BeforeEach
    void setUp() {
        Bullet.init();
        btCollisionConfiguration collisionConfig = new btDefaultCollisionConfiguration();
        btDispatcher dispatcher = new btCollisionDispatcher(collisionConfig);
        btBroadphaseInterface broadPhase = new btDbvtBroadphase();
        btConstraintSolver constraintSolver = new btSequentialImpulseConstraintSolver();
        dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, broadPhase,
                constraintSolver, collisionConfig);

        wallFactory = new WallFactory(dynamicsWorld, new MaterialLoader());
    }

    @Test
    void getWidthRightLeftWall() {
        assertThat(wallFactory.getWidth(1)).isEqualTo(4 * BALL_RADIUS);
    }

    @Test
    void getWidthTopBottomWall() {
        assertThat(wallFactory.getWidth(4)).isEqualTo(TABLE_WIDTH / 2 - POCKET_SIZE);
    }

    @Test
    void getHeightRightLeftWall() {
        assertThat(wallFactory.getHeight(1)).isEqualTo(TABLE_HEIGHT - POCKET_SIZE);
    }

    @Test
    void getHeightTopBottomWall() {
        assertThat(wallFactory.getHeight(4)).isEqualTo(4 * BALL_RADIUS);
    }

    @Test
    void getDepthRightLeftWall() {
        assertThat(wallFactory.getDepth(1)).isEqualTo(8 * BALL_RADIUS);
    }

    @Test
    void getDepthTopBottomWall() {
        assertThat(wallFactory.getDepth(4)).isEqualTo(8 * BALL_RADIUS);
    }

    @Test
    void constructAll() {
        wallFactory = mock(WallFactory.class, withSettings()
                .useConstructor(dynamicsWorld, new MaterialLoader()));

        when(wallFactory.construct(Mockito.anyInt())).thenReturn(mock(Wall.class));
        doCallRealMethod().when(wallFactory).constructAll();
        wallFactory.constructAll();

        verify(wallFactory, times(6)).construct(Mockito.anyInt());
    }

    @Test
    void constructInfo() {
        MaterialLoader materialLoader = mock(MaterialLoader.class);
        WallFactory wallFactory = new WallFactory(dynamicsWorld, materialLoader);

        btRigidBody.btRigidBodyConstructionInfo info = wallFactory.createConstructionInfo(1);
        assertEquals(info.getMass(), 0);
        assertNull(info.getMotionState());
        assertEquals(info.getCollisionShape().getName(), "Box");
    }

    @Test
    void constructModel() {
        MaterialLoader materialLoader = mock(MaterialLoader.class);
        WallFactory wallFactory = new WallFactory(dynamicsWorld, materialLoader);

        Material material = mock(Material.class);
        when(materialLoader.createMaterial(anyBoolean(), anyBoolean(), any())).thenReturn(material);
        Model model = mock(Model.class);
        ModelBuilder modelBuilder = mock(ModelBuilder.class);
        when(modelBuilder.createCylinder(anyFloat(), anyFloat(), anyFloat(),
                anyInt(), any(), anyLong())).thenReturn(model);

        wallFactory.createModel(modelBuilder, 1);

        verify(materialLoader, times(1)).createMaterial(true, true, null);
    }

    @Test
    void setFlags() {
        btDynamicsWorld dynamicsWorld = mock(btDynamicsWorld.class);
        MaterialLoader materialLoader = mock(MaterialLoader.class);
        WallFactory wallFactory = new WallFactory(dynamicsWorld, materialLoader);

        GameObject obj = mock(GameObject.class);
        btRigidBody body = mock(btRigidBody.class);
        when(obj.getBody()).thenReturn(body);

        wallFactory.setFlags(obj, 1);
        verify(body).setUserValue(WALL_USER_VALUE + 1);
        verify(body).setRestitution(0.7f);
        verify(body).setContactCallbackFlag(WALL_FLAG);
        verify(body).setContactCallbackFilter(0);
    }
}