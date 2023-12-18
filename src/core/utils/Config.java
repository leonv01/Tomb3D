package core.utils;

import java.awt.Color;

public class Config {
    public final static int WIDTH = 1600;
    public final static int HEIGHT = 1600;
    public final static int MAP_WIDTH = 1600;
    public final static int MAP_HEIGHT = 1600;
    public final static float MOVEMENT_SPEED = .05f;
    public final static float RUN_SPEED = .1f;
    public final static float ROTATION_SPEED = .03f;
    public final static Color colorSky = Color.GRAY;
    public final static Color colorGround = Color.lightGray;
    
    public static int CELL_SIZE_X = 1;
    public static int CELL_SIZE_Y = 1;

    public static int CELL_COUNT_X = 1;
    public static int CELL_COUNT_Y = 1;
    public static int CELL_SCREEN_WIDTH = 0;
    public static int CELL_SCREEN_HEIGHT = 0;

    public static double HIT_ACCURACY_THRESHOLD = 0.5;

    public final static int LIGHT_ENEMY_DAMAGE = 10;
    public final static int LIGHT_ENEMY_HEALTH = 100;
    public final static int LIGHT_ENEMY_SCORE = 100;
    public final static double LIGHT_ENEMY_SPEED = 0.025;

    public final static int MEDIUM_ENEMY_DAMAGE = 20;
    public final static int MEDIUM_ENEMY_HEALTH = 200;
    public final static int MEDIUM_ENEMY_SCORE = 200;
    public final static double MEDIUM_ENEMY_SPEED = 0.015;

    public final static int HEAVY_ENEMY_DAMAGE = 30;
    public final static int HEAVY_ENEMY_HEALTH = 300;
    public final static int HEAVY_ENEMY_SCORE = 300;
    public final static double HEAVY_ENEMY_SPEED = 0.01;

    public final static int BOSS_ENEMY_DAMAGE = 40;
    public final static int BOSS_ENEMY_HEALTH = 400;
    public final static int BOSS_ENEMY_SCORE = 400;
    public final static double BOSS_ENEMY_SPEED = 0.005;


    public final static int FOV = 40;
    public final static int rayResolution = 3;


}
