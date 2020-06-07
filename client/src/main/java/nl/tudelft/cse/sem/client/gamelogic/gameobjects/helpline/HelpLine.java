package nl.tudelft.cse.sem.client.gamelogic.gameobjects.helpline;

import static nl.tudelft.cse.sem.client.utils.Constants.BALL_RADIUS;
import static nl.tudelft.cse.sem.client.utils.Constants.INITIAL_HELP_LINE_LENGTH;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;
import lombok.Getter;
import lombok.Setter;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.GameObject;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.cue.Cue;
import nl.tudelft.cse.sem.client.utils.Vector3f;


@Getter
@Setter
public class HelpLine extends Cue {

    private float length;
    private static final float angleThreshold = 0.5f;

    HelpLine(Model model) {
        super(model);
        length = INITIAL_HELP_LINE_LENGTH;
    }

    /**
     * Get direction from cue ball to the center of help line.
     *
     * @param cueBall - cue ball instance
     * @return - direction from cue ball to the center of help line
     */
    public Vector3f getDirection(GameObject cueBall) {
        Vector3f dir = new Vector3f(cueBall.getCenter().sub(getCenter()));

        dir.setZ(0);

        return dir.normalize();
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

        this.transform.translate(0, -length / 2 - BALL_RADIUS, 0);

        if (angleDeg > angleThreshold) {
            if (oldDir.cross(newDir).getZ() > 0) {
                this.transform.rotate(Vector3.Z, angleDeg);

            } else {
                this.transform.rotate(Vector3.Z, -angleDeg);
            }
        }

        this.transform.translate(0, length / 2 + BALL_RADIUS, 0);
        this.calculateTransforms();
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
}
