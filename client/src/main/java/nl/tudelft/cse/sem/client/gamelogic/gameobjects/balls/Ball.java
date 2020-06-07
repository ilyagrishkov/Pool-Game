package nl.tudelft.cse.sem.client.gamelogic.gameobjects.balls;

import static nl.tudelft.cse.sem.client.utils.Constants.BALL_RADIUS;
import static nl.tudelft.cse.sem.client.utils.Constants.BLACK_BALL;
import static nl.tudelft.cse.sem.client.utils.Constants.SOLIDS_TYPE;
import static nl.tudelft.cse.sem.client.utils.Constants.STRIPES_TYPE;
import static nl.tudelft.cse.sem.client.utils.Constants.TABLE_WIDTH;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Disposable;
import lombok.Getter;
import lombok.Setter;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.GameObject;
import nl.tudelft.cse.sem.client.utils.Vector3f;

/**
 * Pool ball class.
 */
@Getter
@Setter
public class Ball extends GameObject implements Disposable {

    private int number;
    private int type;

    private static final float SPEED_OF_NO_MOVEMENT = 0.001f;

    /**
     * Constructor for Ball class.
     *
     * @param model            - ball Model
     * @param constructionInfo - rigid body info
     * @param number           - ball number
     */
    public Ball(Model model, btRigidBody.btRigidBodyConstructionInfo constructionInfo, int number) {
        super(model, constructionInfo);
        this.number = number;

        if (number < BLACK_BALL) {
            type = SOLIDS_TYPE;

        } else if (number == BLACK_BALL) {
            type = BLACK_BALL;

        } else {
            type = STRIPES_TYPE;
        }
    }

    /**
     * Get a type of a ball that is opposite the current ball's type.
     *
     * @return - the opposite type
     */
    public int getOppositeType() {
        return type == SOLIDS_TYPE ? STRIPES_TYPE : SOLIDS_TYPE;
    }

    /**
     * Applies central impulse to the Ball.
     *
     * @param dir   - impulse direction
     * @param speed - impulse strength
     */
    public void shoot(Vector3f dir, float speed) {
        getBody().applyCentralImpulse(dir.normalize().multiply(speed).toVector3());
    }

    /**
     * Method for checking if the ball is moving or not.
     *
     * @return - True if the ball is moving, False otherwise
     */
    public boolean isMoving() {
        return getSpeed() > SPEED_OF_NO_MOVEMENT;
    }

    /**
     * Return speed of the ball.
     *
     * @return - speed of the ball
     */
    public float getSpeed() {
        return this.getBody().getLinearVelocity().len();
    }

    /**
     * Get current movement direction.
     *
     * @return - movement direction
     */
    public Vector3f getDirection() {
        return new Vector3f(this.getBody().getLinearVelocity()).normalize();
    }

    /**
     * Resets the cue ball.
     */
    public void resetCueBall() {
        this.transform.setTranslation(new Vector3(-TABLE_WIDTH / 4, 0, BALL_RADIUS));
        this.getBody().proceedToTransform(this.transform);
        this.getBody().setLinearVelocity(new Vector3());
    }
}
