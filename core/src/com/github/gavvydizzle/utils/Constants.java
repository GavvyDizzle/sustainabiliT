package com.github.gavvydizzle.utils;

public class Constants {

    public static final float PPM = 32.0f;
    public static final double ROOT_TWO = Math.sqrt(2);

    public static final float DEGTORAD = 0.0174532925199432957f;
    public static final float RADTODEG = 57.295779513082320876f;

    public static final float CAMERA_WIDTH_PX = 400f;
    public static final float CAMERA_HEIGHT_PX = 400f;
    public static final float CAMERA_WIDTH_METERS = CAMERA_WIDTH_PX/16.0f;
    public static final float CAMERA_HEIGHT_METERS = CAMERA_HEIGHT_PX/16.0f;

    public static final float GRAVITY = -300f;

    public static final float TIME_STEP = 1/60f;
    public static final int VELOCITY_ITERATIONS = 6;
    public static final int POSITION_ITERATIONS = 2;

}
