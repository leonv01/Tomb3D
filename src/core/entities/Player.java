package core.entities;

import core.misc.InputHandler;
import core.misc.Map;
import core.utils.Config;
import core.utils.Vector2D;

public class Player {
    public Vector2D position;   // player position
    public Vector2D direction;  // player direction vector
    public Vector2D plane;      // camera plane for fov
    public Vector2D ray;
    public Vector2D rayDirection;

    private float MOVEMENT_SPEED = Config.MOVEMENT_SPEED;
    private final float ROTATION_SPEED = Config.ROTATION_SPEED;
    private final int WIDTH = Config.WIDTH;
    private final int HEIGHT = Config.HEIGHT;

    private final int CELL_SIZE_X = 1;
    private final int CELL_SIZE_Y = 1;
    private final float FOV =  0.66f;// Config.FOV;

    public InputHandler inputHandler = new InputHandler();

    double rotation;

    public Player(){
        position = new Vector2D();
        direction = new Vector2D(1,0);
        plane = new Vector2D(0,-FOV);
        rotation = 0;
        ray = new Vector2D();
    }

    public Player(Vector2D position){
        this.position = position;
        this.direction = new Vector2D(1,0);
        this.plane = new Vector2D(0,-FOV);
        this.ray = new Vector2D();
        rotation = 0;
    }

    private Vector2D getVerticalVector(double rad){
        int mapX = (int) position.x;
        
        double deltaX = position.x - mapX;

        Vector2D vertical;
        /*
         * check if player looks right
         */
        if(direction.x >= 0){
            double tempRayX = 1 - deltaX;
            double tempRayY =  tempRayX * Math.tan(rotation);
            vertical = new Vector2D(tempRayX, tempRayY);
        }
        else{
            double tempRayX = -deltaX;
            double tempRayY = deltaX* Math.tan(-rotation);
            vertical = new Vector2D(tempRayX, tempRayY);
        }
        return vertical;
    }


    private Vector2D getHorziontalVector(double rad){
        int mapY = (int) position.y;
        
        double deltaY = position.y - mapY;

        Vector2D horizontal;
        /*
         * check if player looks down
         */
        if(direction.y >= 0){
            double tmp = -deltaY + 1.0;
            double tempRayX = tmp / Math.tan(-rotation);

            horizontal = new Vector2D(-tempRayX, tmp);
        }
        else{
            double tempRayX = -deltaY / Math.tan(-rotation);
            horizontal = new Vector2D(-tempRayX, -deltaY);
        }
        return horizontal;
    }
    /*
     * Wall detection algorithm:
     * DDA
     */
    public void castRays(Map map){
        Vector2D horizontal = getHorziontalVector(rotation);
        Vector2D vertical = getVerticalVector(rotation);

        double distanceH = horizontal.length();
        double distanceV = vertical.length();

        if(distanceH > distanceV)
            ray = new Vector2D(vertical);
        else
            ray = new Vector2D(horizontal);
        ray.add(position);
    }

    public void update(Map map){
        double tempX;
        double tempY;
        int mapX;
        int mapY;

        if(inputHandler.run){
            MOVEMENT_SPEED = Config.RUN_SPEED;
        }
        else{
            MOVEMENT_SPEED = Config.MOVEMENT_SPEED;
        }
        if(inputHandler.forward){
            tempX = position.x + direction.x * MOVEMENT_SPEED;
            tempY = position.y + direction.y * MOVEMENT_SPEED;

            mapX = (int) (tempX);
            mapY = (int) (tempY);
            /*
            * Check if tile is free if player would move there
            * Horizontal
            */  
            if(map.map[(int) position.y][mapX] == 0){
                position.x = tempX;
            }
            /*
            * Check if tile is free if player would move there
            * Vertical
            */  
            if(map.map[mapY][(int) position.x] == 0){
                position.y = tempY;
            }
        }
        if(inputHandler.back){
            tempX = position.x - direction.x * MOVEMENT_SPEED;
            tempY = position.y - direction.y * MOVEMENT_SPEED;

            mapX = (int) tempX;
            mapY = (int) tempY;

            /*
            * Check if tile is free if player would move there
            * Horizontal
            */  
            if(map.map[(int) position.y][mapX] == 0){
                position.x = tempX;
            }
            /*
            * Check if tile is free if player would move there
            * Vertical
            */  
            if(map.map[mapY][(int) position.x] == 0){
                position.y = tempY;
            }
        }
        if(inputHandler.left){
            rotation -= ROTATION_SPEED;
            if(rotation <= 0)
                rotation = 2 * Math.PI;
            updateDirection(direction);
            updateDirection(plane);
        }
        if(inputHandler.right){
            rotation += ROTATION_SPEED;
            if(rotation >= 2 * Math.PI)
                rotation = 0;

            updateDirection(direction);
            updateDirection(plane);
        }
        castRays(map);
    }

    public void updateDirection(Vector2D vec) {
        vec.x = Math.cos(rotation);
        vec.y = Math.sin(rotation);
        vec.normalize();
    }
    
}
