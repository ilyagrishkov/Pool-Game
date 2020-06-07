package nl.tudelft.cse.sem.client.gamelogic.gameobjects;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MotionState extends btMotionState {

    private transient Matrix4 transform;

    //Yes these names dont make any sense but this is just broken libgdx names.
    //It works don't touch it
    @Override
    public void getWorldTransform(Matrix4 worldTrans) {
        worldTrans.set(transform);
    }

    @Override
    public void setWorldTransform(Matrix4 worldTrans) {
        transform.set(worldTrans);
    }
}
