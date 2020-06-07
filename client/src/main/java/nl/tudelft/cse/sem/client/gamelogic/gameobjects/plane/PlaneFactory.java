package nl.tudelft.cse.sem.client.gamelogic.gameobjects.plane;

import static nl.tudelft.cse.sem.client.utils.Constants.GROUND_FLAG;
import static nl.tudelft.cse.sem.client.utils.Constants.PLANE_FRICTION;
import static nl.tudelft.cse.sem.client.utils.Constants.PLANE_USER_VALUE;
import static nl.tudelft.cse.sem.client.utils.Constants.TABLE_DEPTH;
import static nl.tudelft.cse.sem.client.utils.Constants.TABLE_HEIGHT;
import static nl.tudelft.cse.sem.client.utils.Constants.TABLE_TEXTURE_OFFSET;
import static nl.tudelft.cse.sem.client.utils.Constants.TABLE_WIDTH;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.GameObject;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.util.MaterialLoader;


public class PlaneFactory {

    private transient btDynamicsWorld dynamicsWorld;
    private transient MaterialLoader materialLoader;

    /**
     * Constructor for Plane Factory.
     *
     * @param dynamicsWorld - dynamic world
     */
    public PlaneFactory(btDynamicsWorld dynamicsWorld, MaterialLoader materialLoader) {
        this.dynamicsWorld = dynamicsWorld;
        this.materialLoader = materialLoader;
    }

    /**
     * Creates instance of a Plane object.
     *
     * @return - Plane instance
     */
    public GameObject construct() {
        GameObject planeObj = new Plane(createModel(new ModelBuilder()), createConstructionInfo());
        setFlags(planeObj);
        return planeObj;
    }

    /**
     * Sets the correct flags to the object.
     * @param planeObj - Plane instance
     */
    void setFlags(GameObject planeObj) {
        planeObj.getBody().setCollisionFlags(planeObj.getBody().getCollisionFlags()
                | btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT);

        dynamicsWorld.addRigidBody(planeObj.getBody());

        planeObj.getBody().setUserValue(PLANE_USER_VALUE);
        planeObj.getBody().setFriction(PLANE_FRICTION);
        planeObj.getBody().setContactCallbackFlag(GROUND_FLAG);
        planeObj.getBody().setContactCallbackFilter(0);
        planeObj.getBody().setActivationState(Collision.DISABLE_DEACTIVATION);
    }

    /**
     * Creates rigid body info for Plane.
     *
     * @return - rigid body info
     */
    btRigidBody.btRigidBodyConstructionInfo createConstructionInfo() {
        btBoxShape shape = new btBoxShape(new Vector3(TABLE_WIDTH / 2,
                TABLE_HEIGHT / 2, TABLE_DEPTH / 2));

        Vector3 localInertia = new Vector3();

        localInertia.set(0, 0, 0);

        return new btRigidBody.btRigidBodyConstructionInfo(0, null,
                shape, localInertia);

    }

    /**
     * Creates model of the Plane.
     *
     * @return - Model of the Plane
     */
    Model createModel(ModelBuilder modelBuilder) {
        Texture texture = materialLoader.load("tableTexture.png");
        final Material planeMaterial = materialLoader.createMaterial(false, true, texture);

        return modelBuilder.createBox(TABLE_WIDTH + TABLE_TEXTURE_OFFSET,
                TABLE_HEIGHT + TABLE_TEXTURE_OFFSET, TABLE_DEPTH, planeMaterial,
                VertexAttributes.Usage.Position
                        | VertexAttributes.Usage.Normal
                        | VertexAttributes.Usage.TextureCoordinates);
    }
}
