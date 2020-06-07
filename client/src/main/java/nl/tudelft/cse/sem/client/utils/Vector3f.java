package nl.tudelft.cse.sem.client.utils;

import com.badlogic.gdx.math.Vector3;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


/**
 * 3D vector class.
 */
@Getter
@Setter
@Data
public class Vector3f {

    private double[] vector;
    private double length;

    /**
     * Constructor for 3D vector.
     *
     * @param x - x component of the vector
     * @param y - y component of the vector
     * @param z - x component of the vector
     */
    public Vector3f(double x, double y, double z) {
        this.vector = new double[3];
        this.vector[0] = x;
        this.vector[1] = y;
        this.vector[2] = z;

        this.length = Math.sqrt(
                Math.pow(vector[0], 2) + Math.pow(vector[1], 2) + Math.pow(vector[2], 2));
    }


    /**
     * Constructor for 3D vector from Vector3 of libGDX.
     *
     * @param vec - Vector3
     */
    public Vector3f(Vector3 vec) {
        this.vector = new double[]{vec.x, vec.y, vec.z};

        this.length = Math.sqrt(
                Math.pow(vector[0], 2) + Math.pow(vector[1], 2) + Math.pow(vector[2], 2));
    }

    /**
     * Getter for x component of this vector.
     *
     * @return - x component of this vector
     */
    public double getX() {
        return vector[0];
    }

    /**
     * Getter for y component of this vector.
     *
     * @return - y component of this vector
     */
    public double getY() {
        return vector[1];
    }

    /**
     * Getter for z component of this vector.
     *
     * @return - z component of this vector
     */
    public double getZ() {
        return vector[2];
    }

    /**
     * Setter or x component of this vector.
     * @param x - new x
     */
    public void setX(double x) {
        this.vector[0] = x;
        this.length = Math.sqrt(
                Math.pow(vector[0], 2) + Math.pow(vector[1], 2) + Math.pow(vector[2], 2));
    }

    /**
     * Setter or y component of this vector.
     * @param y - new y
     */
    public void setY(double y) {
        this.vector[1] = y;
        this.length = Math.sqrt(
                Math.pow(vector[0], 2) + Math.pow(vector[1], 2) + Math.pow(vector[2], 2));
    }

    /**
     * Setter or z component of this vector.
     * @param z - new z
     */
    public void setZ(double z) {
        this.vector[2] = z;
        this.length = Math.sqrt(
                Math.pow(vector[0], 2) + Math.pow(vector[1], 2) + Math.pow(vector[2], 2));
    }

    /**
     * Dot product between this and other vectors.
     *
     * @param other - second vector for dot product
     * @return - dot product of two vectors
     */
    public double dot(Vector3f other) {
        return this.vector[0] * other.vector[0]
                + this.vector[1] * other.vector[1]
                + this.vector[2] * other.vector[2];
    }

    /**
     * Cross product between this and other vectors.
     *
     * @param other - second vector form cross product
     * @return - cross product of vectors
     */
    public Vector3f cross(Vector3f other) {
        double newX = this.vector[1] * other.vector[2] - this.vector[2] * other.vector[1];
        double newY = -this.vector[0] * other.vector[2] + this.vector[2] * other.vector[0];
        double newZ = this.vector[0] * other.vector[1] - this.vector[1] * other.vector[0];

        return new Vector3f(newX, newY, newZ);
    }

    /**
     * Normalize this vector.
     *
     * @return - normalized vector
     */
    public Vector3f normalize() {
        double x = this.vector[0] / length;
        double y = this.vector[1] / length;
        double z = this.vector[2] / length;

        return new Vector3f(x, y, z);
    }

    /**
     * Inverse this vector.
     *
     * @return - a vector of the same magnitude and opposite direction
     */
    public Vector3f inverse() {
        return new Vector3f(-this.vector[0], -this.vector[1], -this.vector[2]);
    }

    /**
     * Adds other vector to this vector.
     *
     * @param other - addend
     * @return - sum
     */
    public Vector3f add(Vector3f other) {
        double x = this.vector[0] + other.vector[0];
        double y = this.vector[1] + other.vector[1];
        double z = this.vector[2] + other.vector[2];

        return new Vector3f(x, y, z);
    }

    /**
     * Subtract other vector from this vector.
     *
     * @param other - subtrahend
     * @return - difference
     */
    public Vector3f subtract(Vector3f other) {
        double x = this.vector[0] - other.vector[0];
        double y = this.vector[1] - other.vector[1];
        double z = this.vector[2] - other.vector[2];

        return new Vector3f(x, y, z);
    }

    /**
     * Multiply other vector with a scalar.
     *
     * @param scalar - scalar
     * @return - product
     */
    public Vector3f multiply(double scalar) {
        double x = scalar * this.vector[0];
        double y = scalar * this.vector[1];
        double z = scalar * this.vector[2];

        return new Vector3f(x, y, z);
    }

    /**
     * Reflects this vector across other vector.
     *
     * @param other - vector to reflect across
     * @return - reflected vector
     */
    public Vector3f reflect(Vector3f other) {
        Vector3f thisNormal = this.normalize();
        Vector3f otherNormal = other.normalize();
        double dot = thisNormal.dot(otherNormal);
        dot *= 2;
        Vector3f otherDot = otherNormal.multiply(dot);
        return thisNormal.subtract(otherDot);

    }

    /**
     * Converts Vector3f to Vector3 from libGDX.
     *
     * @return - converted Vector3
     */
    public Vector3 toVector3() {
        return new Vector3((float) this.getX(), (float) this.getY(), (float) this.getZ());
    }
}
