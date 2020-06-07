package nl.tudelft.cse.sem.client.gamelogic.gameobjects.pockets;

import static nl.tudelft.cse.sem.client.utils.Constants.POCKET_FLAG;
import static nl.tudelft.cse.sem.client.utils.Constants.POCKET_VALUE;
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

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.GdxNativesLoader;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.GameObject;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.util.MaterialLoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


class PocketFactoryTest {

    private transient btDynamicsWorld dynamicsWorld;
    private transient MaterialLoader materialLoader;
    private transient PocketFactory pocketFactory;

    @BeforeAll
    static void setupLib() {
        GdxNativesLoader.load();
        Bullet.init();
    }

    @BeforeEach
    void setUp() {
        dynamicsWorld = mock(btDynamicsWorld.class);
        materialLoader = mock(MaterialLoader.class);
        pocketFactory = new PocketFactory(dynamicsWorld, materialLoader);
    }

    @Test
    void constructor() {
        assertEquals(pocketFactory.getCenters().size(), 6);
        assertEquals(pocketFactory.getDynamicsWorld(), dynamicsWorld);
        assertEquals(pocketFactory.getMaterialLoader(), materialLoader);
    }

    @Test
    void constructAll() {
        PocketFactory pocketFactory = mock(PocketFactory.class);
        doCallRealMethod().when(pocketFactory).constructAll();
        when(pocketFactory.construct(Mockito.anyInt())).thenReturn(mock(Pocket.class));

        pocketFactory.constructAll();

        verify(pocketFactory, times(6)).construct(Mockito.anyInt());
    }

    @Test
    void setFlags() {
        GameObject obj = mock(GameObject.class);
        btRigidBody body = mock(btRigidBody.class);
        when(obj.getBody()).thenReturn(body);

        pocketFactory.updateBody(obj, 1);

        verify(body).setUserValue(POCKET_VALUE + 1);
        verify(body).setContactCallbackFlag(POCKET_FLAG);
        verify(body).setContactCallbackFilter(0);
        verify(body).setActivationState(Collision.DISABLE_DEACTIVATION);
    }

    @Test
    void transform() {
        GameObject obj = mock(GameObject.class);
        btRigidBody body = mock(btRigidBody.class);
        when(obj.getBody()).thenReturn(body);
    }

    @Test
    void constructInfo() {
        btRigidBody.btRigidBodyConstructionInfo info = pocketFactory.createConstructionInfo();
        assertEquals(info.getMass(), 0);
        assertEquals(info.getCollisionShape().getName(), "CylinderY");
        assertNull(info.getMotionState());
    }

    @Test
    void constructModel() {
        Material material = mock(Material.class);
        when(materialLoader.createMaterial(anyBoolean(), anyBoolean(), any())).thenReturn(material);
        Model model = mock(Model.class);
        ModelBuilder modelBuilder = mock(ModelBuilder.class);
        when(modelBuilder.createCylinder(anyFloat(), anyFloat(), anyFloat(),
                anyInt(), any(), anyLong())).thenReturn(model);

        pocketFactory.createModel(modelBuilder);

        verify(materialLoader, times(1)).createMaterial(true, true, null);

    }
}