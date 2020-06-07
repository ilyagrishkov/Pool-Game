package nl.tudelft.cse.sem.client.gamelogic.gameobjects.balls;

import static nl.tudelft.cse.sem.client.utils.Constants.BALL_MASS;
import static nl.tudelft.cse.sem.client.utils.Constants.BALL_RADIUS;
import static nl.tudelft.cse.sem.client.utils.Constants.BLACK_BALL;
import static nl.tudelft.cse.sem.client.utils.Constants.SOLIDS_TYPE;
import static nl.tudelft.cse.sem.client.utils.Constants.STRIPES_TYPE;
import static org.assertj.core.api.Assertions.assertThat;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import nl.tudelft.cse.sem.client.utils.Vector3f;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class BallTest {

    private transient Ball ball;

    private transient btRigidBody.btRigidBodyConstructionInfo constructionInfo;

    @BeforeEach
    void setUp() {
        Bullet.init();

        btSphereShape shape = new btSphereShape(BALL_RADIUS);
        Vector3 localInertia = new Vector3();

        shape.calculateLocalInertia(BALL_MASS, localInertia);

        constructionInfo = new btRigidBody.btRigidBodyConstructionInfo(BALL_MASS, null,
                shape, localInertia);

        ball = new Ball(new Model(), constructionInfo, 1);
    }

    @AfterEach
    void tearDown() {
        ball.dispose();
    }

    @Test
    void constructorSolidsType() {
        Ball ballSolidsType = new Ball(new Model(), constructionInfo, 5);
        assertThat(ballSolidsType.getNumber()).isEqualTo(5);
        assertThat(ballSolidsType.getType()).isEqualTo(SOLIDS_TYPE);
        assertThat(ballSolidsType.getOppositeType()).isEqualTo(STRIPES_TYPE);
    }

    @Test
    void constructorStripesType() {
        Ball ballSolidsType = new Ball(new Model(), constructionInfo, 15);
        assertThat(ballSolidsType.getNumber()).isEqualTo(15);
        assertThat(ballSolidsType.getType()).isEqualTo(STRIPES_TYPE);
        assertThat(ballSolidsType.getOppositeType()).isEqualTo(SOLIDS_TYPE);
    }

    @Test
    void constructorBlackBall() {
        Ball ballSolidsType = new Ball(new Model(), constructionInfo, 8);
        assertThat(ballSolidsType.getNumber()).isEqualTo(8);
        assertThat(ballSolidsType.getType()).isEqualTo(BLACK_BALL);
    }

    @Test
    void getOppositeType() {
        assertThat(ball.getOppositeType()).isEqualTo(STRIPES_TYPE);
    }

    @Test
    void shoot() {
        ball.shoot(new Vector3f(1, 0, 0), 10);
        assertThat(ball.isMoving()).isTrue();
    }

    @Test
    void isMovingNoSpeed() {
        assertThat(ball.isMoving()).isFalse();
    }

    @Test
    void isMovingHasSpeed() {
        ball.getBody().setLinearVelocity(new Vector3(1, 1, 1));
        assertThat(ball.isMoving()).isTrue();
    }

    @Test
    void getSpeed() {
        ball.getBody().setLinearVelocity(new Vector3(1, 0, 0));
        assertThat(ball.getSpeed()).isEqualTo(1);
    }

    @Test
    void getSpeedNoMovement() {
        assertThat(ball.getSpeed()).isEqualTo(0);
    }

    @Test
    void getDirection() {
        ball.getBody().setLinearVelocity(new Vector3(0, 1, 0));
        assertThat(ball.getDirection()).isEqualTo(new Vector3f(0, 1, 0));
    }

    @Test
    void setNumber() {
        assertThat(ball.getNumber()).isEqualTo(1);
        ball.setNumber(2);
        assertThat(ball.getNumber()).isEqualTo(2);
    }

    @Test
    void setType() {
        assertThat(ball.getType()).isEqualTo(SOLIDS_TYPE);
        ball.setType(BLACK_BALL);
        assertThat(ball.getType()).isEqualTo(BLACK_BALL);
    }

    @Test
    void getNumber() {
        assertThat(ball.getNumber()).isEqualTo(1);
    }

    @Test
    void getType() {
        assertThat(ball.getType()).isEqualTo(SOLIDS_TYPE);
    }
}