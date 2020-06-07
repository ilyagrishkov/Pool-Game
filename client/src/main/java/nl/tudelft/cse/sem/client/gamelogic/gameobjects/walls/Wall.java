package nl.tudelft.cse.sem.client.gamelogic.gameobjects.walls;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import lombok.Getter;
import lombok.Setter;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.GameObject;
import nl.tudelft.cse.sem.client.utils.Vector3f;

@Getter
@Setter
public class Wall extends GameObject {

    private Vector3f normal;

    Wall(Model model, btRigidBody.btRigidBodyConstructionInfo constructionInfo, Vector3f normal) {
        super(model, constructionInfo);
        this.normal = normal;
    }

}
