package nl.tudelft.cse.sem.client.utils;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btDispatcher;
import com.badlogic.gdx.physics.bullet.dynamics.btConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.utils.Disposable;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import nl.tudelft.cse.sem.client.gamelogic.Table;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.GameObject;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.balls.Ball;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.balls.BallFactory;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.cue.Cue;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.cue.CueFactory;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.helpline.HelpLine;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.helpline.HelpLineFactory;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.plane.PlaneFactory;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.pockets.PocketFactory;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.util.MaterialLoader;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.walls.WallFactory;

@Getter
@Setter
public class PhysicsEngine implements Disposable {

    private transient int gameMode;

    private transient btCollisionConfiguration collisionConfig;
    private transient btDispatcher dispatcher;
    private transient btBroadphaseInterface broadPhase;
    private transient btDynamicsWorld dynamicsWorld;
    private transient btConstraintSolver constraintSolver;

    private transient Table table;

    /**
     * Constructs an engine.
     * @param gravity the gravity.
     * @param gameMode the game mode
     */
    public PhysicsEngine(Vector3 gravity, int gameMode) {
        collisionConfig = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfig);
        broadPhase = new btDbvtBroadphase();
        constraintSolver = new btSequentialImpulseConstraintSolver();
        dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, broadPhase,
                constraintSolver, collisionConfig);
        dynamicsWorld.setGravity(gravity);

        this.gameMode = gameMode;
        this.table = createTable();
    }

    /**
     * Creates a table.
     * @return a table
     */
    private Table createTable() {
        MaterialLoader materialLoader = MaterialLoader.getInstance();
        table = new Table.Builder()
                .ofType(gameMode)
                .withBallFactory(new BallFactory(dynamicsWorld, materialLoader))
                .withWallFactory(new WallFactory(dynamicsWorld, materialLoader))
                .withPlaneFactory(new PlaneFactory(dynamicsWorld, materialLoader))
                .withPocketFactory(new PocketFactory(dynamicsWorld, materialLoader))
                .withCueFactory(new CueFactory(materialLoader))
                .withHelpLineFactory(new HelpLineFactory())
                .build();
        return table;
    }

    /**
     * Gets the renderables.
     * @return the renderables
     */
    public List<GameObject> getRenderables() {
        return table.getRenderables();
    }

    /**
     * Gets the cue ball.
     * @return the cue ball
     */
    public Ball getCueBall() {
        return table.getCueBall();
    }

    /**
     * Gets the Cue.
     * @return the Cue
     */
    public Cue getCue() {
        return table.getCue();
    }

    /**
     * Gets the helpline.
     * @return the helpline
     */
    public HelpLine getHelpLine() {
        return table.getHelpLine();
    }

    @Override
    public void dispose() {
        dynamicsWorld.dispose();
        constraintSolver.dispose();
        broadPhase.dispose();
        dispatcher.dispose();
        collisionConfig.dispose();
        table.dispose();
    }
}
