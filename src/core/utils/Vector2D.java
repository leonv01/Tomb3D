package core.utils;

/**
 * The Vector2D class represents a two-dimensional vector with x and y components.
 */
public class Vector2D {
    public double x, y;

    /**
     * Default constructor. Initializes the vector with (0, 0).
     */
    public Vector2D(){
        this.x = 0;
        this.y = 0;
    }

    /**
     * Constructor to initialize the vector with specified x and y values.
     *
     * @param x The x component of the vector.
     * @param y The y component of the vector.
     */
    public Vector2D(double x, double y){
        this.x = x;
        this.y = y;
    }

    /**
     * Copy constructor to create a new vector with the same component as the provided vector.
     *
     * @param vec The vector to copy.
     */
    public Vector2D(Vector2D vec){
        this.x = vec.x;
        this.y = vec.y;
    }

    /**
     * Adds another vector to this vector.
     *
     * @param vec The vector to add to this vector.
     */
    public void add(Vector2D vec){
        this.x += vec.x;
        this.y += vec.y;
    }

    /**
     * Subtracts another vector from this vector and returns the result as a new vector.
     *
     * @param vec The vector to subtract from this vector.
     * @return The resulting vector after subtraction.
     */
    public Vector2D sub(Vector2D vec){
        return new Vector2D(
        this.x - vec.x,
        this.y - vec.y);
    }

    /**
     * Calculates the dot product of this vector and another vector.
     *
     * @param vec The other vector to calculate the dot product with.
     * @return The dot product of the two vectors.
     */
    public double mul(Vector2D vec){
        return (
            this.x * vec.x +
            this.y * vec.y
        );
    }

    /**
     * Calculates the Euclidean distance between this vector and another vector.
     *
     * @param vec The other vector to calculate the distance to.
     * @return The Euclidean distance between the two vectors.
     */
    public double distance(Vector2D vec){
        Vector2D vector2d = new Vector2D(this);
        vector2d.sub(vec);
        return vector2d.length();
    }

    /**
     * Calculates the length of the vector.
     *
     * @return The length of the vector.
     */
    public double length(){
        return Math.sqrt(
            x * x + y * y
        );
    }

    /**
     * Normalizes the vector, making it a unit vector while preserving direction.
     */
    public void normalize(){
        double length = length();
        if(length != 0){
            x /= length;
            y /= length;
        }
    }

    /**
     * Returns a string representation of the vector with formatted x and y values.
     *
     * @return The string representation of the vector.
     */
    @Override
    public String toString(){
        return String.format("x: %.3f y: %.3f", x,y);
    }
}
