package core.entities;

import core.misc.HighscoreEntry;
import core.misc.InputHandler;
import core.misc.Map;
import core.utils.Config;
import core.utils.Ray;
import core.utils.Vector2D;

import javax.swing.*;

/**
 * The Player class represents the player entity in the game.
 */
public class Player {
    private Vector2D position;   // Player position.
    private Vector2D direction;  // Player look direction.
    private Ray horizontal;  // Ray for horizontal grid line check.
    private Ray vertical;    // Ray for vertical grid line check.
    private Ray[] rays; // Array of Rays.


    private InputHandler inputHandler; // InputHandler to react to user input.
    private Timer timer; // Timer for shooting.

    private Map map;

    private double rotation; // Rotation value of the player.

    private final int fov = Config.FOV; // FOV value.

    private EntityAttributes attributes;

    private boolean isShooting;
    private final int shootDelay = 200;

    private enum State {
        ALIVE, DEAD
    }
    private State state;


    /**
     * Constructs a new player object with a position.
     *
     * @param position The position of the player.
     */
    public Player(Vector2D position) {
        initPlayer(position);
    }

    /**
     * Initializes the player object with a position.
     *
     * @param position The position of the player.
     */
    private void initPlayer(Vector2D position) {
        this.state = State.ALIVE;

        this.position = position;
        this.direction = new Vector2D(1, 0);
        this.rotation = 0;
        this.horizontal = new Ray();
        this.vertical = new Ray();

        this.rays = new Ray[Config.rayResolution * fov];
        for (int i = 0; i < rays.length; i++) rays[i] = new Ray();

        this.attributes = new EntityAttributes(
                100, Config.MOVEMENT_SPEED, Config.RUN_SPEED, Config.ROTATION_SPEED,
                15, 100, 30,
                30, 0, 0, 0
        );

        isShooting = false;

        timer = new Timer(shootDelay, e -> shoot());
    }

    /**
     * Casts rays in the game map based on the player's position and viewing angle.
     * The rays are used to detect walls in the game map.
     */
    public void castRays() {
        // Temporary values to store the length of the horizontal/vertical rays for comparison.
        double vLength, hLength;


        /*
        Increase or decrease the amount of rays within in a defined FOV divided by the ray resolution.
        (The higher the ray resumption, the more rays are calculated within the FOV).
         */
        double lookRadiant = rotation - (Math.toRadians(fov) / 2.0);

        // Reset angle to keep it in the limits [0, 2 * PI]
        if (lookRadiant < 0) lookRadiant += 2 * Math.PI;
        if (lookRadiant > 2 * Math.PI) lookRadiant -= 2 * Math.PI;

        // For each ray the horizontal and vertical rays are calculated.
        for (int i = 0; i < rays.length; i++) {
            double tempAngle = lookRadiant + Math.toRadians((i / (double) Config.rayResolution));
            if (tempAngle < 0) tempAngle += 2 * Math.PI;
            if (tempAngle > 2 * Math.PI) tempAngle -= 2 * Math.PI;

            // Calculate and get the horizontal/vertical ray that hit a grid line.
            this.horizontal = getHorizontalRay(tempAngle);
            this.vertical = getVerticalRay(tempAngle);

            // Calculate the difference between the player position and the rays.
            this.horizontal.calculateDifference(position);
            this.vertical.calculateDifference(position);

            double newX, newY, length;
            boolean horizontal;
            int wallID;
            /*
            If the vertical ray is shorter than the horizontal, the x and y values are stored for a new ray with the values of
            the vertical Ray and the length of it.
             */
            if ((vertical.getLength() < this.horizontal.getLength())) {
                newX = vertical.getX();
                newY = vertical.getY();
                length = vertical.getLength();
                wallID = vertical.getWallID();
                horizontal = false;
            }
            // Else the horizontal ray is used to create a new ray.
            else {
                newX = this.horizontal.getX();
                newY = this.horizontal.getY();
                length = this.horizontal.getLength();
                wallID = this.horizontal.getWallID();
                horizontal = true;
            }

            // This calculation is done to prevent the 'fisheye' effect when standing to close to a wall.
            //double temp = rotation - tempAngle;
            // if(temp < 0) temp += 2 * Math.PI;
            // if(temp > 2 * Math.PI) temp -= 2 * Math.PI;
            // length *= Math.cos(temp);

            // A new ray is stored in the array with the values of the shortest ray, the length, color and the indicator if it was a horizontal wall or not.
            rays[i] = new Ray(new Vector2D(newX, newY), tempAngle, length, horizontal, wallID);
        }
    }

    /**
     * Calculates a horizontal ray for ray casting based on the player's position and viewing angle.
     * The ray is used to detect horizontal walls in the game map.
     * @param angle The viewing angle in radians at which the ray is cast.
     * @return A Ray object representing the detected horizontal ray.
     */
    private Ray getHorizontalRay(double angle) {
        // Maximum iterations for the ray.
        int dof = 0;

        // Defines the limit of the depth of field.
        int dofEnd = Config.CELL_COUNT_X;

        // Determines the relative position of the player within the current cell.
        double yRayDelta = position.y - (int) (position.y);

        double rayX = 0, rayY = 0;
        double yRayOff = 0, xRayOff = 0;

        // Grid correction for the direction the player is looking at.
        int lookingUp = 1;

        // If the player is looking down.
        if (Math.sin(angle) > 0) {
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
        else if (Math.sin(angle) < 0) {
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
        if (Math.sin(angle) == 0) {
            rayX = position.x;
            rayY = position.y;

            // No iteration will be done.
            dof = dofEnd;
        }

        int wallID = 0;

        // The ray will iterate as long as no wall has been hit or the ray goes out of bounds.
        while (dof < dofEnd) {

            // The x and y indexes are calculated by flooring the position of the ray.
            int indexX = (int) (rayX);
            int indexY = (int) (rayY) + lookingUp;

            // If the indexes are within the boundaries and the value at the index is a wall, the loop will be exited.
            if (map.inBounds(indexX, indexY) && map.getValue(indexX, indexY) != 0) {
                wallID = map.getValue(indexX, indexY);
                dof = dofEnd;
            }
            // Else the ray will iterate with the x and y offsets.
            else {
                rayX -= xRayOff;
                rayY -= yRayOff;
                dof++;
            }
        }
        return new Ray(new Vector2D(rayX, rayY), angle, 0, false, wallID);
    }

    /**
     * Calculates a horizontal ray for ray casting based on the player's position and viewing angle.
     * The ray is used to detect horizontal walls in the game map.
     * @param angle The viewing angle in radians at which the ray is cast.
     * @return A Ray object representing the detected horizontal ray.
     */
    private Ray getVerticalRay(double angle) {
        // Maximum iterations for the ray.
        int dof = 0;

        // Defines the limit of the depth of field.
        int dofEnd = Config.CELL_COUNT_Y;

        // Determines the relative position of the player within the current cell.
        double xRayDelta = position.x - (int) (position.x);

        double rayX = 0, rayY = 0;
        double yRayOff = 0, xRayOff = 0;

        // Grid correction for the direction the player is looking at.
        int lookingLeft = 1;

        // If the player is looking left.
        if (Math.cos(angle) < 0) {
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
        if (Math.cos(angle) == 0) {
            rayX = position.x;
            rayY = position.y;

            // No iteration will be done.
            dof = dofEnd;
        }

        int wallID = 0;
        // The ray will iterate as long as no wall has been hit or the ray goes out of bounds.
        while (dof < dofEnd) {

            // The x and y indexes are calculated by flooring the position of the ray.
            int indexY = (int) (rayY);
            int indexX = (int) (rayX) + lookingLeft;

            // If the indexes are within the boundaries and the value at the index is a wall, the loop will be exited.
            if (map.inBounds(indexX, indexY) && map.getValue(indexX, indexY) != 0) {
                wallID = map.getValue(indexX, indexY);
                dof = dofEnd;
            }
            // Else the ray will iterate with the x and y offsets.
            else {
                rayX -= xRayOff;
                rayY -= yRayOff;
                dof++;
            }
        }

        return new Ray(new Vector2D(rayX, rayY), angle, 0, true, wallID);
    }

    /**
     * Updates the player position and direction based on user input.
     */
    public void update() {
        double tempX;
        double tempY;
        int mapX;
        int mapY;

        double playerSpeed = attributes.getSpeed();

        if (state.equals(State.DEAD)) {
            System.out.println("Dead");
        }

        if (inputHandler.isRun()) {
            playerSpeed = attributes.getRunSpeed();
        }

        // If the player moves forward.
        if (inputHandler.isForward()) {
            // Calculate the temporary x and y values, based on the current position added with the direction where the player looks at multiplied by the movement speed.
            tempX = position.x + direction.x * playerSpeed;
            tempY = position.y + direction.y * playerSpeed;

            // Flooring the position to get the x and y index in the map.
            mapX = (int) (tempX);
            mapY = (int) (tempY);

            // If the temporary x position doesn't hit a wall, the player's x position is set to the temporary x position.
            if (map.map[(int) position.y][mapX] == 0) {
                position.x = tempX;
            }

            // If the temporary y position doesn't hit a wall, the player's y position is set to the temporary y position.
            if (map.map[mapY][(int) position.x] == 0) {
                position.y = tempY;
            }
        }
        if (inputHandler.isBack()) {
            // Calculate the temporary x and y values, based on the current position added with the direction where the player looks at multiplied by the movement speed.
            tempX = position.x - direction.x * playerSpeed;
            tempY = position.y - direction.y * playerSpeed;

            // Flooring the position to get the x and y index in the map.
            mapX = (int) tempX;
            mapY = (int) tempY;

            // If the temporary x position doesn't hit a wall, the player's x position is set to the temporary x position.
            if (map.map[(int) position.y][mapX] == 0) {
                position.x = tempX;
            }

            // If the temporary y position doesn't hit a wall, the player's y position is set to the temporary y position.
            if (map.map[mapY][(int) position.x] == 0) {
                position.y = tempY;
            }
        }

        // If the player rotates right.
        if (inputHandler.isRight()) {
            // Rotation speed is subtracted from the player rotation.
            rotation -= attributes.getRotationSpeed();

            // Keeping the player rotation in bounds [0, 2 * PI]
            if (rotation <= 0)
                rotation = 2 * Math.PI;

            // Updating the player direction based on the rotation.
            updateDirection(direction);
        }

        // If the player rotates left.
        if (inputHandler.isLeft()) {
            // Rotation speed is added onto the player rotation.
            rotation += attributes.getRotationSpeed();

            // Keeping the player rotation in bounds [0, 2 * PI]
            if (rotation >= 2 * Math.PI)
                rotation = 0;

            // Updating the player direction based on the rotation.
            updateDirection(direction);
        }

        if (inputHandler.isUse()) {
            int directionX = (int) (direction.x + position.x);
            int directionY = (int) (direction.y + position.y);


            if (map.getWall(directionX, directionY).equals(Map.WALLS.DOOR) && attributes.getKey()) {
                map.setValue(directionX, directionY, Map.WALLS.EMPTY);
            }

            //TODO: Implement wall building mechanic + remove designated walls
            /*
            else if (
                    map.getWall(directionX, directionY).equals(Map.WALLS.EMPTY)
                            && (directionX != (int) position.x || directionY != (int) position.y) && false
            ) {
                map.setValue(directionX, directionY, Map.WALLS.WOOD);
            }

             */
        }

        if (inputHandler.isShoot()) {
            if (!isShooting)
                for (Drone drone : map.getEnemies()) {
                    if (drone.getRenderSprite().getShootable() && canShoot()) {
                        drone.takeDamage(40);
                    }
                }
            isShooting = true;
            timer.start();
        }

        // Start the ray casting.
        castRays();
    }

    /**
     * Shoots a bullet.
     */
    private void shoot() {
        isShooting = false;
        attributes.shoot();
        timer.stop();
    }

    /**
     * Updates the player direction based on the rotation.
     * @param vec The vector to be updated.
     */
    public void updateDirection(Vector2D vec) {
        vec.x = Math.cos(-rotation);
        vec.y = Math.sin(-rotation);
        vec.normalize();
    }

    /**
     * Adds a key to the player.
     */
    public void addKey() {
        attributes.setKey(true);
    }

    /**
     * Adds ammo to the player.
     */
    public void addAmmo() {
        attributes.addAmmo();
    }

    /**
     * Adds health to the player.
     * @param health The amount of health to be added.
     */
    public void addHealth(int health) {
        attributes.addHealth(health);
    }

    /**
     * Adds score to the player.
     * @param score The amount of score to be added.
     */
    public void addScore(int score) {
        attributes.addScore(score);
        System.out.println(attributes.getScore());
    }

    /**
     * Takes damage from entity.
     * @param damage The damage to be taken.
     */
    public void takeDamage(int damage) {
        attributes.takeDamage(damage);
        if (attributes.getHealth() <= 0)
            state = State.DEAD;
    }

    /**
     * Returns the player's position.
     * @return The player's position.
     */
    public double getY() {
        return position.y;
    }

    public double getX() {
        return position.x;
    }

    /**
     * Returns the player's direction.
     * @return The player's direction.
     */
    public Vector2D getPosition() {
        return position;
    }

    public HighscoreEntry getHighscore() {
        return new HighscoreEntry(Config.PLAYER_NAME, attributes.getScore());
    }

    /**
     * Returns the player's direction.
     * @return The player's direction.
     */
    public Vector2D getDirection() {
        return direction;
    }

    /**
     * Returns the player's rays.
     * @return The player's rays.
     */
    public Ray[] getRays() {
        return rays;
    }

    /**
     * Returns the player's rotation.
     * @return The player's rotation.
     */
    public double getRotation() {
        return rotation;
    }

    /**
     * Returns the player's attributes.
     * @return The player's attributes.
     */
    public int getHealth() {
        return attributes.getHealth();
    }

    /**
     * Returns the player's attributes.
     * @return The player's attributes.
     */
    public int getAmmo() {
        return attributes.getCurrentAmmo();
    }

    /**
     * Returns the player's attributes.
     * @return The player's attributes.
     */
    public int getScore() {
        return attributes.getScore();
    }

    /**
     * Returns the player's attributes.
     * @return The player's attributes.
     */
    public int getAmmoPack() {
        return attributes.getAmmoPack();
    }

    /**
     * Returns the player's attributes.
     * @return The player's attributes.
     */
    public boolean getKey() {
        return attributes.getKey();
    }

    /**
     * Returns the player's attributes.
     * @return The player's attributes.
     */
    public boolean isAlive() {
        return attributes.isAlive();
    }

    /**
     * Returns the player's attributes.
     * @return The player's attributes.
     */
    public boolean canShoot() {
        return attributes.getCurrentAmmo() > 0;
    }

    /**
     * Sets the InputHandler for the player.
     * @param i The InputHandler object.
     */
    public void setKeyListener(InputHandler i) {
        this.inputHandler = i;
    }

    /**
     * Sets the map for the player.
     * @param map The map object.
     */
    public void setMap(Map map) {
        this.map = map;
    }

    /**
     * Prints the player's attributes.
     */
    public void printAttributes() {
        System.out.println(attributes.toString());
    }
}