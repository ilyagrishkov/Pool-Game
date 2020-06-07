package nl.tudelft.cse.sem.client.gamelogic.gameobjects.balls;

import static nl.tudelft.cse.sem.client.utils.Constants.BALL_FLAG;
import static nl.tudelft.cse.sem.client.utils.Constants.BALL_FRICTION;
import static nl.tudelft.cse.sem.client.utils.Constants.BALL_MASS;
import static nl.tudelft.cse.sem.client.utils.Constants.BALL_RADIUS;
import static nl.tudelft.cse.sem.client.utils.Constants.BALL_RESTITUTION;
import static nl.tudelft.cse.sem.client.utils.Constants.BALL_SPINNING_FRICTION;
import static nl.tudelft.cse.sem.client.utils.Constants.BALL_USER_VALUE;
import static nl.tudelft.cse.sem.client.utils.Constants.CUE_BALL_FLAG;
import static nl.tudelft.cse.sem.client.utils.Constants.CUE_BALL_MASS;
import static nl.tudelft.cse.sem.client.utils.Constants.EIGHT_BALL_GAME;
import static nl.tudelft.cse.sem.client.utils.Constants.NINE_BALL_GAME;
import static nl.tudelft.cse.sem.client.utils.Constants.POCKET_FLAG;
import static nl.tudelft.cse.sem.client.utils.Constants.ROLLING_FRICTION;
import static nl.tudelft.cse.sem.client.utils.Constants.TABLE_WIDTH;
import static nl.tudelft.cse.sem.client.utils.Constants.WALL_FLAG;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.tudelft.cse.sem.client.gamelogic.gameobjects.util.MaterialLoader;
import nl.tudelft.cse.sem.client.utils.Vector3f;


public class BallFactory {


    private transient btDynamicsWorld dynamicsWorld;
    private transient MaterialLoader materialLoader;

    /**
     * Constructor for Ball Factory.
     *
     * @param dynamicsWorld - dynamic world
     */
    public BallFactory(btDynamicsWorld dynamicsWorld, MaterialLoader materialLoader) {
        this.dynamicsWorld = dynamicsWorld;
        this.materialLoader = materialLoader;
    }

    /**
     * Constructs balls for the given type.
     * @param type the game type
     * @return a list of balls
     */
    public List<Ball> constructBalls(int type) {
        if (type == EIGHT_BALL_GAME) {
            return constructEightBallBalls();
        } else if (type == NINE_BALL_GAME) {
            return constructNineBallBalls();
        }
        return null;
    }

    /**
     * Constructs all Ball objects for standard pool game.
     *
     * @return - List of Balls
     */
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public List<Ball> constructEightBallBalls() {
        List<Ball> result = new ArrayList<>();
        Map<Integer, Vector3f> centers = createCentersEightBall();

        for (int i = 0; i <= 15; i++) {
            result.add(construct(i, centers));
        }
        return result;
    }

    /**
     * Constructs all Ball objects for a 9-ball pool game.
     *
     * @return - List of Balls
     */
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public List<Ball> constructNineBallBalls() {
        List<Ball> result = new ArrayList<>();
        Map<Integer, Vector3f> centers = createCentersNineBall();

        for (int i = 0; i <= 9; i++) {
            result.add(construct(i, centers));
        }
        return result;
    }

    /**
     * Constructs a Ball instance.
     *
     * @param number - Ball number
     * @return - Ball instance
     */
    Ball construct(int number, Map<Integer, Vector3f> centers) {
        Model model = createModel(number);

        Ball obj = new Ball(model, createConstructionInfo(number), number);

        Vector3 center = centers.get(number).toVector3();
        center.z = 0.01f;

        obj.transform.trn(center);
        obj.getBody().proceedToTransform(obj.transform);

        obj.getBody().setUserValue(BALL_USER_VALUE + number);
        obj.getBody().setRollingFriction(ROLLING_FRICTION);
        obj.getBody().setSpinningFriction(BALL_SPINNING_FRICTION);
        obj.getBody().setRestitution(BALL_RESTITUTION);
        obj.getBody().setFriction(BALL_FRICTION);
        obj.getBody().setCollisionFlags(obj.getBody().getCollisionFlags()
                | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);

        this.dynamicsWorld.addRigidBody(obj.getBody());

        if (number == 0) {

            obj.getBody().setContactCallbackFlag(CUE_BALL_FLAG);
            obj.getBody().setContactCallbackFilter(WALL_FLAG | BALL_FLAG | POCKET_FLAG);
        } else {

            obj.getBody().setContactCallbackFlag(BALL_FLAG);
            obj.getBody().setContactCallbackFilter(WALL_FLAG | POCKET_FLAG);
        }


        return obj;
    }

    /**
     * Creates rigid body info for Ball.
     *
     * @param number - Ball number
     * @return - rigid body info
     */
    private btRigidBody.btRigidBodyConstructionInfo createConstructionInfo(int number) {
        float objMass = number == 0 ? CUE_BALL_MASS : BALL_MASS;

        btSphereShape shape = new btSphereShape(BALL_RADIUS);
        Vector3 localInertia = new Vector3();

        shape.calculateLocalInertia(objMass, localInertia);

        return new btRigidBody.btRigidBodyConstructionInfo(objMass, null,
                shape, localInertia);

    }

    /**
     * Creates model of the Ball.
     *
     * @param number - Ball number
     * @return - Model of the Ball
     */
    Model createModel(int number) {
        ModelBuilder modelBuilder = new ModelBuilder();

        Texture texture = materialLoader.load(number + ".jpg");

        final Material material = materialLoader.createMaterial(false, false, texture);

        return modelBuilder.createSphere(2 * BALL_RADIUS, 2 * BALL_RADIUS,
                2 * BALL_RADIUS, 30, 30, material,
                VertexAttributes.Usage.Position
                        | VertexAttributes.Usage.Normal
                        | VertexAttributes.Usage.TextureCoordinates);
    }

    /**
     * Creates centers of Balls for 8 pool game.
     *
     * @return - map of Balls' centers
     */
    Map<Integer, Vector3f> createCentersEightBall() {
        Map<Integer, Vector3f> result = new HashMap<>();

        result.put(0, new Vector3f(-TABLE_WIDTH / 4, 0, BALL_RADIUS));

        float currentPosX = TABLE_WIDTH / 4;
        result.put(1, new Vector3f(currentPosX, 0, BALL_RADIUS));

        currentPosX += (2 * BALL_RADIUS + 0.001);
        result.put(2, new Vector3f(currentPosX, BALL_RADIUS + 0.001, BALL_RADIUS));
        result.put(9, new Vector3f(currentPosX, -BALL_RADIUS - 0.001, BALL_RADIUS));

        currentPosX += (2 * BALL_RADIUS + 0.001);
        result.put(3, new Vector3f(currentPosX, 2 * BALL_RADIUS + 0.0005, BALL_RADIUS));
        result.put(8, new Vector3f(currentPosX, 0, BALL_RADIUS));
        result.put(10, new Vector3f(currentPosX, -2 * BALL_RADIUS - 0.0005, BALL_RADIUS));

        currentPosX += (2 * BALL_RADIUS + 0.001);
        result.put(4, new Vector3f(currentPosX, 3 * BALL_RADIUS + 0.0015, BALL_RADIUS));
        result.put(14, new Vector3f(currentPosX, BALL_RADIUS + 0.0005, BALL_RADIUS));
        result.put(7, new Vector3f(currentPosX, -BALL_RADIUS - 0.0005, BALL_RADIUS));
        result.put(11, new Vector3f(currentPosX, -3 * BALL_RADIUS - 0.0015, BALL_RADIUS));

        currentPosX += (2 * BALL_RADIUS + 0.001);
        result.put(12, new Vector3f(currentPosX, 4 * BALL_RADIUS + 0.001, BALL_RADIUS));
        result.put(6, new Vector3f(currentPosX, 2 * BALL_RADIUS + 0.0005, BALL_RADIUS));
        result.put(15, new Vector3f(currentPosX, 0, BALL_RADIUS / 2));
        result.put(13, new Vector3f(currentPosX, -2 * BALL_RADIUS - 0.0005, BALL_RADIUS));
        result.put(5, new Vector3f(currentPosX, -4 * BALL_RADIUS - 0.001, BALL_RADIUS));

        return result;
    }

    /**
     * Creates centers of Balls for 9 pool game.
     *
     * @return - map of Balls' centers
     */
    Map<Integer, Vector3f> createCentersNineBall() {
        Map<Integer, Vector3f> result = new HashMap<>();

        result.put(0, new Vector3f(-TABLE_WIDTH / 4, 0, BALL_RADIUS));

        float currentPosX = TABLE_WIDTH / 4;
        result.put(1, new Vector3f(currentPosX, 0, BALL_RADIUS));

        currentPosX += (2 * BALL_RADIUS + 0.001);
        result.put(2, new Vector3f(currentPosX, BALL_RADIUS + 0.001, BALL_RADIUS));
        result.put(3, new Vector3f(currentPosX, -BALL_RADIUS - 0.001, BALL_RADIUS));

        currentPosX += (2 * BALL_RADIUS + 0.001);
        result.put(4, new Vector3f(currentPosX, 2 * BALL_RADIUS + 0.0005, BALL_RADIUS));
        result.put(9, new Vector3f(currentPosX, 0, BALL_RADIUS));
        result.put(5, new Vector3f(currentPosX, -2 * BALL_RADIUS - 0.0005, BALL_RADIUS));

        currentPosX += (2 * BALL_RADIUS + 0.001);
        result.put(6, new Vector3f(currentPosX, BALL_RADIUS + 0.001, BALL_RADIUS));
        result.put(7, new Vector3f(currentPosX, -BALL_RADIUS - 0.001, BALL_RADIUS));

        currentPosX += (2 * BALL_RADIUS + 0.001);
        result.put(8, new Vector3f(currentPosX, 0, BALL_RADIUS));

        return result;
    }
}
