package nl.tudelft.cse.sem.client.gamelogic.gameobjects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

import com.badlogic.gdx.math.Matrix4;
import org.junit.jupiter.api.Test;

public class MotionStateTest {
    @Test
    void testWorldUpdate() {
        MotionState motionState = mock(MotionState.class, withSettings()
                .defaultAnswer(CALLS_REAL_METHODS));
        motionState.setTransform(new Matrix4());
        Matrix4 matrix4 = new Matrix4();
        motionState.setWorldTransform(matrix4);
        assertEquals(matrix4.toString(), motionState.getTransform().toString());

        float[] values = {3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,};
        Matrix4 scaledMatrix = new Matrix4(values);
        motionState.getWorldTransform(scaledMatrix);

        assertEquals(matrix4.toString(), scaledMatrix.toString());

    }
}
