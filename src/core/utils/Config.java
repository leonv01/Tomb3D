package core.utils;

import java.awt.Color;

public class Config {
    public final static int WIDTH = 1500;
    public final static int HEIGHT = 1500;
    public final static float DRONE_SPEED = 0.04f;
    public final static float MOVEMENT_SPEED = .05f;
    public final static float RUN_SPEED = .1f;
    public final static float ROTATION_SPEED = .05f;
    public final static int TEXTURE_HEIGHT = 64;
    public final static int TEXTURE_WIDTH = 64;
    public final static Color colorSky = Color.GRAY;
    public final static Color colorGround = Color.lightGray;
    
    public static int CELL_SIZE_X = 1;
    public static int CELL_SIZE_Y = 1;

    public static int CELL_COUNT_X = 1;
    public static int CELL_COUNT_Y = 1;

    public final static int FOV = 40;
    public final static int rayResolution = 3;
}
