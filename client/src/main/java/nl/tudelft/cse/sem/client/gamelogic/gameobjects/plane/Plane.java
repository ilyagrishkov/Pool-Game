package nl.tudelft.cse.sem.client.gamelogic.gameobjects.plane;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.GameObject;

class Plane extends GameObject {

    Plane(Model model, btRigidBody.btRigidBodyConstructionInfo constructionInfo) {
        super(model, constructionInfo);
    }
}
