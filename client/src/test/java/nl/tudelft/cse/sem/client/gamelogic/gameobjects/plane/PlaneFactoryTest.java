package nl.tudelft.cse.sem.client.gamelogic.gameobjects.plane;

import static nl.tudelft.cse.sem.client.utils.Constants.GROUND_FLAG;
import static nl.tudelft.cse.sem.client.utils.Constants.PLANE_FRICTION;
import static nl.tudelft.cse.sem.client.utils.Constants.PLANE_USER_VALUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.badlogic.gdx.graphics.Texture;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PlaneFactoryTest {

    private transient btDynamicsWorld dynamicsWorld;
    private transient MaterialLoader materialLoader;
    private transient PlaneFactory planeFactory;

    @BeforeAll
    static void setupLib() {
        GdxNativesLoader.load();
        Bullet.init();
    }

    @BeforeEach
    void before() {
        dynamicsWorld = mock(btDynamicsWorld.class);
        materialLoader = mock(MaterialLoader.class);
        planeFactory = new PlaneFactory(dynamicsWorld, materialLoader);
    }

    @Test
    void constructionInfo() {
        btRigidBody.btRigidBodyConstructionInfo info = planeFactory.createConstructionInfo();
        assertEquals(info.getMass(), 0);
        assertNull(info.getMotionState());
    }

    @Test
    void createModel() {
        Texture texture = mock(Texture.class);
        when(materialLoader.load(anyString())).thenReturn(texture);
        Material material = mock(Material.class);
        when(materialLoader.createMaterial(anyBoolean(), anyBoolean(), any())).thenReturn(material);
        Model model = mock(Model.class);
        ModelBuilder modelBuilder = mock(ModelBuilder.class);
        when(modelBuilder.createBox(anyFloat(), anyFloat(), anyFloat(), any(), anyLong()))
                .thenReturn(model);

        planeFactory.createModel(modelBuilder);

        verify(materialLoader, times(1)).load("tableTexture.png");
        verify(materialLoader, times(1)).createMaterial(false, true, texture);
    }

    @Test
    void flagTest() {
        GameObject planeObj = mock(GameObject.class);
        btRigidBody body = mock(btRigidBody.class);
        when(planeObj.getBody()).thenReturn(body);

        planeFactory.setFlags(planeObj);

        verify(body).setUserValue(PLANE_USER_VALUE);
        verify(body).setFriction(PLANE_FRICTION);
        verify(body).setContactCallbackFlag(GROUND_FLAG);
        verify(body).setContactCallbackFilter(0);
        verify(body).setActivationState(Collision.DISABLE_DEACTIVATION);
    }
}
