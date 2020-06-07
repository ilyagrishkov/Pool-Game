package nl.tudelft.cse.sem.client.utils;

import static nl.tudelft.cse.sem.client.utils.Constants.BALL_USER_VALUE;
import static nl.tudelft.cse.sem.client.utils.Constants.PLANE_USER_VALUE;
import static nl.tudelft.cse.sem.client.utils.Constants.POCKET_VALUE;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.balls.Ball;
import nl.tudelft.cse.sem.client.screens.GameScreen;

public class CollisionHandler extends ContactListener {

    transient GameScreen gameScreen;
    transient PhysicsEngine physicsEngine;

    public CollisionHandler(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        this.physicsEngine = gameScreen.getPhysicsEngine();
    }

    @Override
    public boolean onContactAdded(int userValue0, int partId0, int index0, boolean match0,
                                  int userValue1, int partId1, int index1, boolean match1) {
        if (userValue0 >= POCKET_VALUE) {

            handlePocketCollisions(userValue1);
            return true;
        }

        if (userValue1 >= POCKET_VALUE) {

            handlePocketCollisions(userValue0);
            return true;
        }


        if (match0) {

            handleTouch(userValue1, userValue1);
            return true;
        }

        if (match1) {

            handleTouch(userValue0, userValue1);
            return true;
        }

        return true;
    }

    private void handleTouch(int userValue0, int userValue1) {
        if (userValue0 >= BALL_USER_VALUE && userValue1 >= BALL_USER_VALUE
                && userValue0 < PLANE_USER_VALUE && userValue1 < PLANE_USER_VALUE) {
            Ball ball = (Ball) physicsEngine.getTable().getBall(userValue0 - BALL_USER_VALUE);
            gameScreen.getGame().addTouchedBall(ball);
        }
    }

    private void handlePocketCollisions(int userValue1) {
        if (userValue1 != BALL_USER_VALUE) {
            Ball ball = (Ball) physicsEngine.getTable().getBall(userValue1 - BALL_USER_VALUE);
            movePottedBall(ball);
            gameScreen.getGame().pocketBall(ball);
        } else {
            gameScreen.getGame().setFoul(true);
            physicsEngine.getCueBall().resetCueBall();
        }
    }

    private void movePottedBall(Ball ball) {
        ball.transform.setTranslation(new Vector3(0, 0, -10));
        ball.getBody().setLinearVelocity(new Vector3());
        gameScreen.getPhysicsEngine().getDynamicsWorld().removeRigidBody(ball.getBody());
    }

}
