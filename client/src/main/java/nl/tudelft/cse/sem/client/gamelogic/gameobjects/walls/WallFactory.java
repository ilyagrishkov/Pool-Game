package nl.tudelft.cse.sem.client.gamelogic.gameobjects.walls;

import static nl.tudelft.cse.sem.client.utils.Constants.BALL_RADIUS;
import static nl.tudelft.cse.sem.client.utils.Constants.POCKET_SIZE;
import static nl.tudelft.cse.sem.client.utils.Constants.TABLE_HEIGHT;
import static nl.tudelft.cse.sem.client.utils.Constants.TABLE_WIDTH;
import static nl.tudelft.cse.sem.client.utils.Constants.WALL_FLAG;
import static nl.tudelft.cse.sem.client.utils.Constants.WALL_USER_VALUE;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.tudelft.cse.sem.client.gamelogic.gameobjects.GameObject;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.util.MaterialLoader;
import nl.tudelft.cse.sem.client.utils.Vector3f;


public class WallFactory {

    private transient Map<Integer, Vector3f> centers;
    private transient Map<Integer, Vector3f> normals;
    private transient btDynamicsWorld dynamicsWorld;
    private transient MaterialLoader materialLoader;

    private transient Vector3f rightLeftDimensions;
    private transient Vector3f topBottomDimensions;

    /**
     * Constructor for Wall Factory.
     *
     * @param dynamicsWorld - dynamic world
     */
    public WallFactory(btDynamicsWorld dynamicsWorld, MaterialLoader materialLoader) {
        this.centers = createCenters();
        this.normals = createNormals();
        this.dynamicsWorld = dynamicsWorld;
        this.materialLoader = materialLoader;

        this.rightLeftDimensions = new Vector3f(4 * BALL_RADIUS,
                TABLE_HEIGHT - POCKET_SIZE, 8 * BALL_RADIUS);

        this.topBottomDimensions = new Vector3f(TABLE_WIDTH / 2 - POCKET_SIZE,
                4 * BALL_RADIUS, 8 * BALL_RADIUS);
    }

    /**
     * Constructs all Wall objects for standard pool game.
     *
     * @return - List of Walls
     */
    public List<GameObject> constructAll() {
        List<GameObject> result = new ArrayList<>();

        for (int i = 0; i <= 5; i++) {

            result.add(construct(i));
        }

        return result;
    }

    /**
     * Constructs a Wall instance.
     *
     * @param number - wall number
     * @return - Wall instance
     */
    Wall construct(int number) {
        Model model = createModel(new ModelBuilder(), number);
        Wall obj = new Wall(model, createConstructionInfo(number), normals.get(number));

        Vector3 center = centers.get(number).toVector3();
        center.z = 2f;
        obj.transform.trn(center);
        obj.getBody().proceedToTransform(obj.transform);

        setFlags(obj, number);

        return obj;
    }

    /**
     * Sets the correct flags for the Wall object.
     * @param obj The wall object
     * @param number The wall number
     */
    void setFlags(GameObject obj, int number) {
        obj.getBody().setUserValue(WALL_USER_VALUE + number);
        obj.getBody().setRestitution(0.7f);
        obj.getBody().setCollisionFlags(obj.getBody().getCollisionFlags()
                | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
        dynamicsWorld.addRigidBody(obj.getBody());
        obj.getBody().setContactCallbackFlag(WALL_FLAG);
        obj.getBody().setContactCallbackFilter(0);
    }

    /**
     * Creates rigid body info for Wall.
     *
     * @param number - Wall number
     * @return - rigid body info
     */
    btRigidBody.btRigidBodyConstructionInfo createConstructionInfo(int number) {
        btBoxShape shape = new btBoxShape(new Vector3(getWidth(number) / 2,
                getHeight(number) / 2, getDepth(number) / 2));

        Vector3 localInertia = new Vector3();

        localInertia.set(0, 0, 0);

        return new btRigidBody.btRigidBodyConstructionInfo(0, null,
                shape, localInertia);

    }

    /**
     * Creates model of the Wall.
     *
     * @param number - Wall number
     * @return - Model of the Wall
     */
    Model createModel(ModelBuilder modelBuilder, int number) {
        final Material material = materialLoader.createMaterial(true, true, null);

        return modelBuilder.createBox(getWidth(number), getHeight(number), getDepth(number),
                material, VertexAttributes.Usage.Position
                        | VertexAttributes.Usage.Normal);
    }

    /**
     * Creates centers of Walls for 8 pool game.
     *
     * @return - map of walls' centers
     */
    private Map<Integer, Vector3f> createCenters() {
        Map<Integer, Vector3f> result = new HashMap<>();

        result.put(0, new Vector3f(-TABLE_WIDTH / 2 - 2 * BALL_RADIUS, 0, 0));
        result.put(1, new Vector3f(TABLE_WIDTH / 2 + 2 * BALL_RADIUS, 0, 0));
        result.put(2, new Vector3f(TABLE_WIDTH / 4, TABLE_HEIGHT / 2 + 2 * BALL_RADIUS, 0));
        result.put(3, new Vector3f(-TABLE_WIDTH / 4, TABLE_HEIGHT / 2 + 2 * BALL_RADIUS, 0));
        result.put(4, new Vector3f(TABLE_WIDTH / 4, -TABLE_HEIGHT / 2 - 2 * BALL_RADIUS, 0));
        result.put(5, new Vector3f(-TABLE_WIDTH / 4, -TABLE_HEIGHT / 2 - 2 * BALL_RADIUS, 0));

        return result;
    }

    /**
     * Creates normals of Walls for 8 pool game.
     *
     * @return - map of walls' normals
     */
    private Map<Integer, Vector3f> createNormals() {
        Map<Integer, Vector3f> result = new HashMap<>();

        result.put(0, new Vector3f(1, 0, 0));
        result.put(1, new Vector3f(-1, 0, 0));
        result.put(2, new Vector3f(0, -1, 0));
        result.put(3, new Vector3f(0, -1, 0));
        result.put(4, new Vector3f(0, 1, 0));
        result.put(5, new Vector3f(0, 1, 0));

        return result;
    }

    /**
     * Get width of the Wall.
     *
     * @param number - wall number
     * @return - width of the wall
     */
    public float getWidth(int number) {
        return number < 2 ? (float) rightLeftDimensions.getX() :
                (float) topBottomDimensions.getX();
    }

    /**
     * Get height of the Wall.
     *
     * @param number - wall number
     * @return - height of the wall
     */
    public float getHeight(int number) {
        return number < 2 ? (float) rightLeftDimensions.getY() :
                (float) topBottomDimensions.getY();
    }

    /**
     * Get depth of the Wall.
     *
     * @param number - wall number
     * @return - depth of the wall
     */
    public float getDepth(int number) {
        return number < 2 ? (float) rightLeftDimensions.getZ() :
                (float) topBottomDimensions.getZ();
    }

}
