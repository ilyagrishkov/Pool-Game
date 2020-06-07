package nl.tudelft.cse.sem.client.gamelogic.gameobjects.cue;

import static nl.tudelft.cse.sem.client.utils.Constants.BALL_RADIUS;
import static nl.tudelft.cse.sem.client.utils.Constants.CUE_LENGTH;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import lombok.Getter;
import lombok.Setter;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.GameObject;
import nl.tudelft.cse.sem.client.utils.Vector3f;

@Getter
@Setter
public class Cue extends ModelInstance {

    private Vector3f position;
    private Vector3f direction;

    private static final float angleThreshold = 0.5f;


    /**
     * Constructor for a Cue.
     *
     * @param model - a model form a cue
     */
    public Cue(Model model) {
        super(model);
        position = new Vector3f(0, 0, 0);
        direction = new Vector3f(0, 0, 0);
    }


    /**
     * Get strength of the shot.
     *
     * @param cueBall - cueBall in order to calculate distance from the cue to
     * @return - shot strength
     */
    public float getStrength(GameObject cueBall) {
        return this.getCenter().sub(cueBall.getCenter()).len() - (CUE_LENGTH / 2 + BALL_RADIUS);
    }

    /**
     * Get the center of cue.
     *
     * @return - center of cue
     */
    public Vector3 getCenter() {
        Vector3 pos = new Vector3();
        this.transform.getTranslation(pos);

        return pos;
    }

    /**
     * Save position of the cue.
     *
     * @param cam     - scene camera
     * @param screenX - X coordinate on the screen
     * @param screenY - Y coordinate on the screen
     */
    public void setPosition(PerspectiveCamera cam, int screenX, int screenY) {
        position = getIntersectionWithPlane(cam, screenX, screenY);
        position.setZ(3);
    }

    /**
     * Get a new position of a cue ball on a table.
     *
     * @param cam     - scene camera
     * @param screenX - X coordinate on the screen
     * @param screenY - Y coordinate on the screen
     * @return - a new position of a cue ball on a table
     */
    public Vector3 getCueBallPosition(PerspectiveCamera cam, int screenX, int screenY) {
        Vector3f newPos = getIntersectionWithPlane(cam, screenX, screenY);
        newPos.setZ(3);
        return newPos.toVector3();
    }

    /**
     * Get intersection of a ray from the screen view with the plane.
     *
     * @param cam     - scene camera
     * @param screenX - X coordinate on the screen
     * @param screenY - Y coordinate on the screen
     * @return - intersection point
     */
    public Vector3f getIntersectionWithPlane(PerspectiveCamera cam, int screenX, int screenY) {
        Ray ray = cam.getPickRay(screenX, screenY);

        final float distance = -ray.origin.z / ray.direction.z;

        Vector3f rayDir = new Vector3f(ray.direction);
        Vector3f rayOrigin = new Vector3f(ray.origin);

        return rayDir.multiply(distance).add(rayOrigin);
    }

    /**
     * Get direction from the center of the cue ball to the center of the cue.
     *
     * @param cueBall - cue ball instance
     * @return - inverse of cue direction
     */
    public Vector3f getDirection(GameObject cueBall) {
        Vector3f dir = new Vector3f(getCenter().sub(cueBall.getCenter()));

        dir.setZ(0);

        return dir.normalize();
    }

    /**
     * Get the change in strength of the shot.
     *
     * @param cam     - scene camera
     * @param screenX - X coordinate on the screen
     * @param screenY - Y coordinate on the screen
     * @param cueBall - cue ball instance
     */
    private float getDeltaStrength(PerspectiveCamera cam, int screenX, int screenY,
                                   GameObject cueBall) {
        Vector3f temp = getIntersectionWithPlane(cam, screenX, screenY);
        temp.setZ(3);

        float result = (float) this.position.subtract(temp).getLength();

        result = (position.subtract(new Vector3f(cueBall.getCenter())).getLength()
                < temp.subtract(new Vector3f(cueBall.getCenter())).getLength()) ? result : -result;

        position = temp;

        return result;
    }

    /**
     * Move cue back to prepare for the shot.
     *
     * @param cam     - scene camera
     * @param screenX - X coordinate on the screen
     * @param screenY - Y coordinate on the screen
     * @param cueBall - cue ball instance
     */
    public void strike(PerspectiveCamera cam, int screenX, int screenY, GameObject cueBall) {
        float deltaStrength = getDeltaStrength(cam, screenX, screenY, cueBall);

        if (getStrength(cueBall) + deltaStrength > 0) {
            Vector3f translation = direction.inverse().normalize().multiply(deltaStrength);

            Vector3 currTranslation = new Vector3();

            double dot = this.getDirection(cueBall).normalize().dot(new Vector3f(0, 1.0f, 0));
            double currentAngle = Math.toDegrees(Math.acos(dot));

            this.transform.getTranslation(currTranslation);
            this.transform = new Matrix4();

            this.transform.translate(translation.toVector3());

            this.transform.translate(currTranslation);

            if (this.getDirection(cueBall).normalize().cross(new Vector3f(0, 1.0f, 0)).getZ() < 0) {
                this.transform.rotate(Vector3.Z, (float) currentAngle);

            } else {
                this.transform.rotate(Vector3.Z, (float) -currentAngle);
            }

            this.calculateTransforms();
        }

    }

    /**
     * Get change of the direction of the cue.
     *
     * @param cam     - scene camera
     * @param screenX - X coordinate on the screen
     * @param screenY - Y coordinate on the screen
     * @param cueBall - cue ball instance
     * @return - delta of direction
     */
    private Vector3f getNewDirection(PerspectiveCamera cam, int screenX, int screenY,
                                     GameObject cueBall) {
        Vector3f temp = getIntersectionWithPlane(cam, screenX, screenY)
                .add(new Vector3f(0, 0, 3));

        Vector3f cueBallCenter = new Vector3f(cueBall.getCenter());

        Vector3f newDir = temp.subtract(cueBallCenter);
        newDir = newDir.subtract(new Vector3f(0, 0, newDir.getZ()));

        return newDir;
    }

    /**
     * Calculate angle between the old direction and a new direction.
     *
     * @param oldDir - old direction
     * @param newDir - new direction
     * @return - angle between vectors (in degrees)
     */
    private float calculateAngle(Vector3f oldDir, Vector3f newDir) {
        double dot = oldDir.normalize().dot(newDir.normalize());

        return (float) Math.toDegrees(Math.acos(dot));
    }

    /**
     * Allows cue to rotate around a cue ball.
     *
     * @param cueBall - cue ball instance
     * @param cam     - scene camera
     * @param screenX - X coordinate on the screen
     * @param screenY - Y coordinate on the screen
     */
    public void rotate(GameObject cueBall, PerspectiveCamera cam, int screenX, int screenY) {
        Vector3f oldDir = getDirection(cueBall);
        Vector3f newDir = getNewDirection(cam, screenX, screenY, cueBall);
        float angleDeg = calculateAngle(oldDir, newDir);

        this.transform.translate(0, -CUE_LENGTH / 2 - BALL_RADIUS, 0);

        if (angleDeg > angleThreshold) {
            if (oldDir.cross(newDir).getZ() > 0) {
                this.transform.rotate(Vector3.Z, angleDeg);

            } else {
                this.transform.rotate(Vector3.Z, -angleDeg);
            }
        }

        this.transform.translate(0, CUE_LENGTH / 2 + BALL_RADIUS, 0);
        this.calculateTransforms();
    }
}
