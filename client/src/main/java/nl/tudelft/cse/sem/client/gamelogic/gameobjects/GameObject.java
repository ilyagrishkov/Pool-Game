package nl.tudelft.cse.sem.client.gamelogic.gameobjects;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Disposable;
import lombok.Getter;

@Getter
public abstract class GameObject extends ModelInstance implements Disposable {

    private final btRigidBody body;
    private final MotionState motionState;
    private final Model model;

    /**
     * Constructor for GameObject class.
     *
     * @param model            - model of the object
     * @param constructionInfo - rigid body info
     */
    public GameObject(Model model, btRigidBody.btRigidBodyConstructionInfo constructionInfo) {
        super(model);

        this.model = model;
        this.motionState = new MotionState();
        this.motionState.setTransform(transform);
        this.body = new btRigidBody(constructionInfo);
        this.body.setMotionState(motionState);
    }

    public Vector3 getCenter() {
        return body.getCenterOfMassPosition();
    }

    @Override
    public void dispose() {
        body.dispose();
        motionState.dispose();
        model.dispose();
    }

}
