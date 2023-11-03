package core.utils;

import java.awt.*;

/**
 * Represents a ray used for ray casting in the game world.
 */
public class Ray {
    private final Vector2D position;
    private double length;
    private final Color color;
    private boolean horitontal;
    private int wallID;

    /**
     * Constructs a new Ray with default values.
     */
    public Ray(){
        this.position = new Vector2D();
        this.length = 0;
        this.color = Color.WHITE;
        this.horitontal = false;
        this.wallID = 0;
    }

    /**
     * Constructs a new Ray with the specified parameters.
     *
     * @param position The position of the ray.
     * @param length The length of the ray.
     * @param color The color of the ray.
     * @param horizontal Indicates whether the ray is horizontal (true) or vertical (false).
     */
    public Ray(Vector2D position, double length, Color color, boolean horizontal, int wallID) {
        this.position = position;
        this.length = length;
        this.color = color;
        this.horitontal = horizontal;
        this.wallID = wallID;
    }

    /**
     * Calculates the length of the ray based on the position difference.
     *
     * @param position The position to calculate the length from.
     */
    public void calculateDifference(Vector2D position){
        this.length = this.position.sub(position).length();
    }

    /**
     * Checks if the ray is horizontal.
     *
     * @return True if the ray is horizontal, false if it is vertical.
     */
    public boolean getHorizontal(){
        return horitontal;
    }

    /**
     * Get the X-coordinate of the ray's position.
     *
     * @return The X-coordinate of the ray's position.
     */
    public double getX(){ return position.x; }

    /**
     * Get the Y-coordinate of the ray's position.
     *
     * @return The Y-coordinate of the ray's position.
     */
    public double getY(){ return position.y; }

    public int getWallID(){ return wallID; }

    /**
     * Get the length of the ray.
     *
     * @return The length of the ray.
     */
    public double getLength(){ return length; }

    /**
     * Get the color of the ray.
     *
     * @return The color of the ray.
     */
    public Color getColor(){ return color; }
}
