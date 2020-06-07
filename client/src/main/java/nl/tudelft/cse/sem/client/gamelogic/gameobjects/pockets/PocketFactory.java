package nl.tudelft.cse.sem.client.gamelogic.gameobjects.pockets;

import static nl.tudelft.cse.sem.client.utils.Constants.POCKET_FLAG;
import static nl.tudelft.cse.sem.client.utils.Constants.POCKET_SIZE;
import static nl.tudelft.cse.sem.client.utils.Constants.POCKET_VALUE;
import static nl.tudelft.cse.sem.client.utils.Constants.TABLE_HEIGHT;
import static nl.tudelft.cse.sem.client.utils.Constants.TABLE_WIDTH;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCylinderShape;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.GameObject;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.util.MaterialLoader;
import nl.tudelft.cse.sem.client.utils.Vector3f;

@Getter
@Setter
public class PocketFactory {
    private transient Map<Integer, Vector3f> centers;
    private transient btDynamicsWorld dynamicsWorld;
    private transient MaterialLoader materialLoader;

    /**
     * Constructor for Pocket Factory.
     *
     * @param dynamicsWorld - dynamic world
     */
    public PocketFactory(btDynamicsWorld dynamicsWorld, MaterialLoader materialLoader) {
        this.dynamicsWorld = dynamicsWorld;
        this.centers = createCenters();
        this.materialLoader = materialLoader;
    }

    /**
     * Constructs all Pocket objects for standard pool game.
     *
     * @return - List of Pockets
     */
    public List<GameObject> constructAll() {
        List<GameObject> result = new ArrayList<>();
        int numOfPockets = 6;
        for (int i = 0; i < numOfPockets; i++) {
            result.add(construct(i));
        }
        return result;
    }

    /**
     * Constructs a Pocket instance.
     *
     * @param number - pocket number
     * @return - Pocket instance
     */
    GameObject construct(int number) {
        GameObject obj = new Pocket(createModel(new ModelBuilder()), createConstructionInfo());

        obj.getBody().setCollisionFlags(obj.getBody().getCollisionFlags()
                | btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT);

        Vector3 center = centers.get(number).toVector3();
        center.z = 2f;
        obj.transform.trn(center);
        obj.transform.rotate(Vector3.X, 90);
        obj.getBody().proceedToTransform(obj.transform);

        return updateBody(obj, number);
    }

    /**
     * Sets correct flags and adds the body to the environment.
     * @param obj The object.
     * @param number The pocket number
     * @return The object.
     */
    GameObject updateBody(GameObject obj, int number) {
        obj.getBody().setUserValue(POCKET_VALUE + number);
        obj.getBody().setContactCallbackFlag(POCKET_FLAG);
        obj.getBody().setContactCallbackFilter(0);
        obj.getBody().setActivationState(Collision.DISABLE_DEACTIVATION);
        dynamicsWorld.addRigidBody(obj.getBody());
        return obj;
    }

    /**
     * Creates rigid body info for Pocket.
     *
     * @return - rigid body info
     */
    btRigidBody.btRigidBodyConstructionInfo createConstructionInfo() {
        btCylinderShape shape = new btCylinderShape(new Vector3(POCKET_SIZE / 2,
                POCKET_SIZE / 2, POCKET_SIZE / 2));

        Vector3 localInertia = new Vector3();

        localInertia.set(0, 0, 0);

        return new btRigidBody.btRigidBodyConstructionInfo(0, null,
                shape, localInertia);

    }

    /**
     * Creates model of the Pocket.
     *
     * @return - Model of the Pocket
     */
    Model createModel(ModelBuilder modelBuilder) {
        final Material material = materialLoader.createMaterial(true, true, null);

        return modelBuilder.createCylinder(POCKET_SIZE, POCKET_SIZE, POCKET_SIZE,
                20, material, VertexAttributes.Usage.Position
                        | VertexAttributes.Usage.Normal);
    }

    /**
     * Creates centers of Pockets for 8 pool game.
     *
     * @return - map of pockets' centers
     */
    private Map<Integer, Vector3f> createCenters() {
        Map<Integer, Vector3f> result = new HashMap<>();

        result.put(0, new Vector3f(-TABLE_WIDTH / 2 - POCKET_SIZE / 4,
                TABLE_HEIGHT / 2 + POCKET_SIZE / 4, 0));

        result.put(1, new Vector3f(-TABLE_WIDTH / 2 - POCKET_SIZE / 4,
                -TABLE_HEIGHT / 2 - POCKET_SIZE / 4, 0));

        result.put(2, new Vector3f(TABLE_WIDTH / 2 + POCKET_SIZE / 4,
                -TABLE_HEIGHT / 2 - POCKET_SIZE / 4, 0));

        result.put(3, new Vector3f(TABLE_WIDTH / 2 + POCKET_SIZE / 4,
                TABLE_HEIGHT / 2 + POCKET_SIZE / 4, 0));

        result.put(4, new Vector3f(0, TABLE_HEIGHT / 2 + POCKET_SIZE / 2, 0));
        result.put(5, new Vector3f(0, -TABLE_HEIGHT / 2 - POCKET_SIZE / 2, 0));

        return result;
    }
}
