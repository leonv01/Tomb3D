package core.entities;

import core.misc.InputHandler;
import core.misc.Map;
import core.utils.Config;
import core.utils.Ray;
import core.utils.Vector2D;

import java.awt.*;

/**
 * The Player class represents the player entity in the game.
 */
public class Player {
    // Player position.
    public Vector2D position;
    // Player look direction.
    public Vector2D direction;
    // Ray for horizontal grid line check.
    public Ray horizontal;
    // Ray for vertical grid line check.
    public Ray vertical;
    // Array of Rays.
    public Ray[] rays;

    // InputHandler to react to user input.
    private InputHandler inputHandler;

    // Rotation value of the player.
    double rotation;

    // FOV value.
    int fov = Config.FOV;

    /**
     * Default constructor for the Player class. Initializes player properties.
     */
    public Player(){
        this.position = new Vector2D();
        this.direction = new Vector2D(1,0);
        this.rotation = 0;
        this.horizontal = new Ray();
        this.vertical = new Ray();

        // Creates array of rays based on the FOV and the ray resolution.
        this.rays = new Ray[Config.rayResolution * fov];
        for (int i = 0; i < rays.length; i++) rays[i] = new Ray();
    }

    /**
     * Constructor for the Player class with an initial position.
     *
     * @param position The initial position of the player.
     */
    public Player(Vector2D position){
        // Sets position of the player to the parameter.
        this.position = position;
        this.direction = new Vector2D(1,0);
        this.rotation = 0;
        this.horizontal = new Ray();
        this.vertical = new Ray();

        // Creates array of rays based on the FOV and the ray resolution.
        this.rays = new Ray[Config.rayResolution * fov];
        for (int i = 0; i < rays.length; i++) rays[i] = new Ray();
    }

    /**
     * Cast rays to detect walls in the game map.
     *
     * @param map The game map used for ray casting.
     */
    public void castRays(Map map){
        // Temporary values to store the length of the horizontal/vertical rays for comparison.
        double vLength, hLength;


        /*
        Increase or decrease the amount of rays within in a defined FOV divided by the ray resolution.
        (The higher the ray resumption, the more rays are calculated within the FOV).
         */
        double lookRadiant = rotation - (Math.toRadians(fov) / 2.0);

        // Reset angle to keep it in the limits [0, 2 * PI]
        if(lookRadiant < 0) lookRadiant += 2 * Math.PI;
        if(lookRadiant > 2 * Math.PI) lookRadiant -= 2 * Math.PI;

        // For each ray the horizontal and vertical rays are calculated.
        for (int i = 0; i < rays.length; i++) {
            double tempAngle = lookRadiant + Math.toRadians((i / (double)Config.rayResolution));
            if(tempAngle < 0) tempAngle += 2 * Math.PI;
            if(tempAngle > 2 * Math.PI) tempAngle -= 2 * Math.PI;

            // Calculate and get the horizontal/vertical ray that hit a grid line.
            this.horizontal = getHorizontalRay(map, tempAngle);
            this.vertical = getVerticalRay(map, tempAngle);

            // Calculate the difference between the player position and the rays.
            this.horizontal.calculateDifference(position);
            this.vertical.calculateDifference(position);

            double newX, newY, length;
            Color wallColor;
            boolean horizontal;

            /*
            If the vertical ray is shorter than the horizontal, the x and y values are stored for a new ray with the values of
            the vertical Ray and the length of it.
             */
            if((vertical.getLength() < this.horizontal.getLength())){
                newX = vertical.getX();
                newY = vertical.getY();
                length = vertical.getLength();
                wallColor = vertical.getColor();
                horizontal = false;
            }
            // Else the horizontal ray is used to create a new ray.
            else{
                newX = this.horizontal.getX();
                newY = this.horizontal.getY();
                length = this.horizontal.getLength();
                wallColor = this.horizontal.getColor().darker();
                horizontal = true;
            }

            // This calculation is done to prevent the 'fisheye' effect when standing to close to a wall.
            double temp = rotation - tempAngle;
            if(temp < 0) temp += 2 * Math.PI;
            if(temp > 2 * Math.PI) temp -= 2 * Math.PI;
            length *= Math.cos(temp);

            // A new ray is stored in the array with the values of the shortest ray, the length, color and the indicator if it was a horizontal wall or not.
            rays[i] = new Ray(new Vector2D(newX, newY), length, wallColor, horizontal);
        }
    }

    /**
     * Calculates a horizontal ray for ray casting based on the player's position and viewing angle.
     * The ray is used to detect horizontal walls in the game map.
     *
     * @param map   The game map used for ray casting and collision detection.
     * @param angle The viewing angle in radians at which the ray is cast.
     * @return A Ray object representing the detected horizontal ray.
     */
    private Ray getHorizontalRay(Map map, double angle){
        // Maximum iterations for the ray.
        int dof = 0;

        // Defines the limit of the depth of field.
        int dofEnd = Config.CELL_COUNT_X;

        // Determines the relative position of the player within the current cell.
        double yRayDelta = position.y - (int)(position.y);

        double rayX = 0, rayY = 0;  
        double yRayOff = 0, xRayOff = 0;

        // Grid correction for the direction the player is looking at.
        int lookingUp = 1;
        
        // If the player is looking down.
        if(Math.sin(angle) > 0){
            // Ray for the current cell ground is calculated based on the relative cell position.
            rayX = -yRayDelta / Math.tan(angle);
            rayX = position.x - rayX;
            rayY = position.y - yRayDelta;

            // The y offset for the iterations = 1.0, because the player is looking down, so y needs to be positive.
            yRayOff = 1.0;

            // Calculate the x offset for the iterations.
            xRayOff = yRayOff / -Math.tan(angle);

            // Grid correction is set to -1.
            lookingUp = -1;
        } 
        // Else if the player is looking up.
        else if(Math.sin(angle) < 0){
            // The relative cell position.
            yRayDelta = -yRayDelta + 1.0;

            // Ray for the current cell ground is calculated based on the relative cell position.
            rayX = yRayDelta / Math.tan(angle);
            rayX = position.x - rayX;
            rayY = position.y + yRayDelta;

            // The y offset for the iterations = -1.0, because the player is looking up, so y needs to be negative.
            yRayOff = -1.0;

            // Calculate the x offset for the iterations.
            xRayOff = yRayOff / -Math.tan(angle);

            // Grid correction is set to 0.
            lookingUp = 0;
        }
        // If player is looking directly horizontal, no horizontal lines are visible.
        if(Math.sin(angle) == 0){
            rayX = position.x;
            rayY = position.y;

            // No iteration will be done.
            dof = dofEnd;
        }

        Color color = Color.GRAY;

        // The ray will iterate as long as no wall has been hit or the ray goes out of bounds.
        while(dof < dofEnd){

            // The x and y indexes are calculated by flooring the position of the ray.
            int indexX = (int)(rayX);
            int indexY = (int)(rayY) + lookingUp;

            // If the indexes are within the boundaries and the value at the index is a wall, the loop will be exited.
            if(map.inBounds(indexX, indexY) && map.getValue(indexX, indexY) != 0) {
                dof = dofEnd;
                color = map.getColor(indexX, indexY);
            }
            // Else the ray will iterate with the x and y offsets.
            else{
                rayX -= xRayOff;
                rayY -= yRayOff;
                dof++;
            }
        }
        return new Ray(new Vector2D(rayX, rayY), 0, color, false);
    }

    /**
     * Calculates a vertical ray for ray casting based on the player's position and viewing angle.
     * The ray is used to detect vertical walls in the game map.
     *
     * @param map   The game map used for ray casting and collision detection.
     * @param angle The viewing angle in radians at which the ray is cast.
     * @return A Ray object representing the detected vertical ray.
     */
    private Ray getVerticalRay(Map map, double angle){
        // Maximum iterations for the ray.
        int dof = 0;

        // Defines the limit of the depth of field.
        int dofEnd = Config.CELL_COUNT_Y;

        // Determines the relative position of the player within the current cell.
        double xRayDelta = position.x - (int)(position.x);

        double rayX = 0, rayY = 0;
        double yRayOff = 0, xRayOff = 0;

        // Grid correction for the direction the player is looking at.
        int lookingLeft = 1;

        // If the player is looking left.
        if(Math.cos(angle) < 0)   {
            // Ray for the current cell ground is calculated based on the relative cell position.
            rayY = xRayDelta * Math.tan(angle);
            rayY = position.y + rayY;
            rayX = position.x - xRayDelta;

            // The x offset for the iterations = 1.0, because the player is looking left, so y needs to be positive.
            xRayOff = 1.0;

            // Calculate the y offset for the iterations.
            yRayOff = xRayOff * -Math.tan(angle);

            // Grid correction is set to -1.
            lookingLeft = -1;
        }
        // If the player is looking right.
        if (Math.cos(angle) > 0) {
            // The relative cell position.
            xRayDelta = 1.0 - xRayDelta;

            // Ray for the current cell ground is calculated based on the relative cell position.
            rayY = xRayDelta * Math.tan(angle);
            rayY = position.y - rayY;
            rayX = position.x + xRayDelta;

            // The y offset for the iterations = -1.0, because the player is looking right, so x needs to be negative.
            xRayOff = -1.0;

            // Calculate the y offset for the iterations.
            yRayOff = -xRayOff * Math.tan(angle);

            // Grid correction is set to 0.
            lookingLeft = 0;
        }
        // If player is looking directly vertical, no vertical lines are visible.
        if(Math.cos(angle) == 0){
            rayX = position.x;
            rayY = position.y;

            // No iteration will be done.
            dof = dofEnd;
        }

        Color color = Color.GRAY;

        // The ray will iterate as long as no wall has been hit or the ray goes out of bounds.
        while(dof < dofEnd){

            // The x and y indexes are calculated by flooring the position of the ray.
            int indexY = (int)(rayY);
            int indexX = (int)(rayX) + lookingLeft;

            // If the indexes are within the boundaries and the value at the index is a wall, the loop will be exited.
            if(map.inBounds(indexX, indexY)	&& map.getValue(indexX, indexY) != 0) {
                dof = dofEnd;
                color = map.getColor(indexX, indexY);
            }
            // Else the ray will iterate with the x and y offsets.
            else{
                rayX -= xRayOff;
                rayY -= yRayOff;
                dof++;
            }
        }

        return new Ray(new Vector2D(rayX, rayY), 0, color, true);
    }

    /**
     * Update the player's position, rotation, and cast rays in the game map based on user input.
     *
     * @param map The game map used for collision detection and ray casting.
     */
    public void update(Map map){
        double tempX;
        double tempY;
        int mapX;
        int mapY;

        // Get movement speed from the config.
        float MOVEMENT_SPEED = Config.MOVEMENT_SPEED;

        // If player runs, the movement speed is increased.
        if(inputHandler.run){
            MOVEMENT_SPEED = Config.RUN_SPEED;
        }
        // Else the player movement speed is set to default.
        else{
            MOVEMENT_SPEED = Config.MOVEMENT_SPEED;
        }
        // If the player moves forward.
        if(inputHandler.forward){
            // Calculate the temporary x and y values, based on the current position added with the direction where the player looks at multiplied by the movement speed.
            tempX = position.x + direction.x * MOVEMENT_SPEED;
            tempY = position.y + direction.y * MOVEMENT_SPEED;

            // Flooring the position to get the x and y index in the map.
            mapX = (int) (tempX);
            mapY = (int) (tempY);

            // If the temporary x position doesn't hit a wall, the player's x position is set to the temporary x position.
            if(map.map[(int) position.y][mapX] == 0){
                position.x = tempX;
            }

            // If the temporary y position doesn't hit a wall, the player's y position is set to the temporary y position.
            if(map.map[mapY][(int) position.x] == 0){
                position.y = tempY;
            }
        }
        if(inputHandler.back){
            // Calculate the temporary x and y values, based on the current position added with the direction where the player looks at multiplied by the movement speed.
            tempX = position.x - direction.x * MOVEMENT_SPEED;
            tempY = position.y - direction.y * MOVEMENT_SPEED;

            // Flooring the position to get the x and y index in the map.
            mapX = (int) tempX;
            mapY = (int) tempY;

            // If the temporary x position doesn't hit a wall, the player's x position is set to the temporary x position.
            if(map.map[(int) position.y][mapX] == 0){
                position.x = tempX;
            }

            // If the temporary y position doesn't hit a wall, the player's y position is set to the temporary y position.
            if(map.map[mapY][(int) position.x] == 0){
                position.y = tempY;
            }
        }

        // Get the rotation speed from the config.
        float ROTATION_SPEED = Config.ROTATION_SPEED;

        // If the player rotates right.
        if(inputHandler.right){
            // Rotation speed is subtracted from the player rotation.
            rotation -= ROTATION_SPEED;

            // Keeping the player rotation in bounds [0, 2 * PI]
            if(rotation <= 0)
                rotation = 2 * Math.PI;

            // Updating the player direction based on the rotation.
            updateDirection(direction);
        }

        // If the player rotates left.
        if(inputHandler.left){
            // Rotation speed is added onto the player rotation.
            rotation += ROTATION_SPEED;

            // Keeping the player rotation in bounds [0, 2 * PI]
            if(rotation >= 2 * Math.PI)
                rotation = 0;

            // Updating the player direction based on the rotation.
            updateDirection(direction);
        }

        // Start the ray casting.
        castRays(map);
    }

    /**
     * Get the player's x-coordinate in the game world.
     *
     * @return The x-coordinate of the player's position.
     */
    public double getX(){
        return position.x;
    }

    /**
     * Get the player's y-coordinate in the game world.
     *
     * @return The y-coordinate of the player's position.
     */
    public double getY(){
        return position.y;
    }

    /**
     * Update the direction vector of the player based on the current rotation angle.
     *
     * @param vec The 2D vector to be updated with the new direction.
     */
    public void updateDirection(Vector2D vec) {
        vec.x = Math.cos(-rotation);
        vec.y = Math.sin(-rotation);
        vec.normalize();
    }

    /**
     * Set the input handler for the player to handle user input.
     *
     * @param i The input to be set for the player.
     */
    public void setKeyListener(InputHandler i){
        this.inputHandler = i;
    }
}
