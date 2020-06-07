package nl.tudelft.cse.sem.client.gamelogic.gameobjects.pockets;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.GameObject;

class Pocket extends GameObject {

    Pocket(Model model, btRigidBody.btRigidBodyConstructionInfo constructionInfo) {
        super(model, constructionInfo);
    }
}
