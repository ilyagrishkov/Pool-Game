package nl.tudelft.cse.sem.client.utils;

public final class Constants {

    public static final int SCREEN_WIDTH = 1200;
    public static final int SMALL_SCREEN_WIDTH = 600;
    public static final int SCREEN_HEIGHT = 700;

    public static final short GROUND_FLAG = 1 << 8;
    public static final short BALL_FLAG = 1 << 9;
    public static final short WALL_FLAG = 1 << 10;
    public static final short POCKET_FLAG = 1 << 5;
    public static final short CUE_BALL_FLAG = 1 << 7;

    public static final int PLANE_USER_VALUE = 1000;
    public static final int BALL_USER_VALUE = 100;
    public static final int WALL_USER_VALUE = 10;
    public static final int POCKET_VALUE = 5000;

    public static final float TABLE_TEXTURE_OFFSET = 29f;
    public static final float TABLE_WIDTH = 224f;
    public static final float TABLE_HEIGHT = 112f;
    public static final float TABLE_DEPTH = 2f;
    public static final float PLANE_FRICTION = 0.8f;

    public static final float ROLLING_FRICTION = 0.6f;
    public static final float BALL_RESTITUTION = 0.7f;
    public static final float BALL_SPINNING_FRICTION = 0.3f;
    public static final float BALL_FRICTION = 0.7f;
    public static final float BALL_RADIUS = 2.8f;
    public static final float BALL_MASS = 0.078f;
    public static final float CUE_BALL_MASS = 0.085f;
    public static final float SPEED_LIMIT = 23;
    public static final float CUE_LENGTH = 100;
    public static final float INITIAL_HELP_LINE_LENGTH = 100;

    public static final float POCKET_SIZE = 4 * BALL_RADIUS;

    public static final int BLACK_BALL = 8;
    public static final int SOLIDS_TYPE = 1;
    public static final int STRIPES_TYPE = 2;

    //Game modes
    public static final int NINE_BALL_GAME = 9;
    public static final int EIGHT_BALL_GAME = 8;

}
