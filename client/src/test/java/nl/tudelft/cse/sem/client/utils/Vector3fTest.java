package nl.tudelft.cse.sem.client.utils;

import static org.assertj.core.api.Assertions.assertThat;

import com.badlogic.gdx.math.Vector3;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class Vector3fTest {

    private transient Vector3f vectorOne;
    private transient Vector3f vectorTwo;

    @BeforeEach
    void setUp() {
        vectorOne = new Vector3f(1, 0, 0);
        vectorTwo = new Vector3f(-1, -1, 0);
    }

    @Test
    void vector3f() {
        Vector3f vec = new Vector3f(1, 2, 3);
        assertThat(vec).isEqualTo(new Vector3f(new Vector3(1, 2, 3)));
    }

    @Test
    void getX() {
        assertThat(vectorOne.getX()).isEqualTo(1);
    }

    @Test
    void getY() {
        assertThat(vectorTwo.getY()).isEqualTo(-1);
    }

    @Test
    void getZ() {
        assertThat(vectorOne.getZ()).isEqualTo(0);
    }

    @Test
    void setX() {
        vectorOne.setX(5);
        assertThat(vectorOne.getX()).isEqualTo(5);
    }

    @Test
    void setY() {
        vectorOne.setY(7);
        assertThat(vectorOne.getY()).isEqualTo(7);
    }

    @Test
    void setZ() {
        vectorOne.setZ(-2);
        assertThat(vectorOne.getZ()).isEqualTo(-2);
    }

    @Test
    void inverse() {
        assertThat(new Vector3f(1, 2, 3).inverse()).isEqualTo(new Vector3f(-1, -2, -3));
    }

    @Test
    void dot() {
        assertThat(vectorOne.dot(vectorTwo)).isEqualTo(-1);
    }

    @Test
    void cross() {

        assertThat(new Vector3f(1, 2, 3).cross(new Vector3f(4, 5, 6)))
                .isEqualTo(new Vector3f(-3, 6, -3));
    }

    @Test
    void normalize() {
        assertThat(vectorTwo.normalize()).isEqualTo(new Vector3f(-1 / Math.sqrt(2),
                -1 / Math.sqrt(2), 0));
    }

    @Test
    void add() {
        assertThat(vectorOne.add(vectorTwo)).isEqualTo(new Vector3f(0, -1, 0));
    }

    @Test
    void subtract() {
        assertThat(vectorOne.subtract(vectorTwo)).isEqualTo(new Vector3f(2, 1, 0));
    }

    @Test
    void multiply() {
        assertThat(vectorTwo.multiply(2)).isEqualTo(new Vector3f(-2, -2, 0));
    }

    @Test
    void reflect() {
        Vector3f result = new Vector3f(0, -2, 0).reflect(new Vector3f(1, 1, 0)).normalize();
        int newX = (int) Math.round(result.getX());
        int newY = (int) Math.round(result.getY());
        int newZ = (int) Math.round(result.getZ());
        assertThat(new Vector3f(newX, newY, newZ)).isEqualTo(new Vector3f(1, 0, 0));
    }

    @Test
    void toVector3() {
        assertThat(vectorTwo.toVector3()).isEqualTo(new Vector3(-1, -1, 0));
    }
}