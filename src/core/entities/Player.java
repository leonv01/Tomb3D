package core.entities;

import core.misc.InputHandler;
import core.misc.Map;
import core.utils.Config;
import core.utils.Vector2D;

public class Player {
    public Vector2D position;
    public Vector2D deltaPosition;
    public Vector2D rayPosition;
    public Vector2D deltaRayPosition;

    private final float MOVEMENT_SPEED = Config.MOVEMENT_SPEED;
    private final float ROTATION_SPEED = Config.ROTATION_SPEED;
    private final int WIDTH = Config.WIDTH;
    private final int HEIGHT = Config.HEIGHT;

    private final int CELL_SIZE_X = 1;
    private final int CELL_SIZE_Y = 1;
    private final int FOV =  1;// Config.FOV;

    public InputHandler inputHandler = new InputHandler();

    double rotation;

    public Player(){
        position = new Vector2D();
        deltaPosition = new Vector2D(1,0);
        rayPosition = new Vector2D();
        deltaRayPosition = new Vector2D();
        rotation = 0;
    }

    public Player(Vector2D position){
        this.position = position;
        this.deltaPosition = new Vector2D(1,0);
        this.rayPosition = new Vector2D(position);
        this.deltaRayPosition = new Vector2D();
        rotation = 0;
    }

    /*
     * Wall detection algorithm:
     * DDA
     */
    public void castRays(Map map){
        rayPosition.x = Math.cos(rotation);
        rayPosition.y = Math.sin(rotation);

        rayPosition.x = (int) position.x;
        rayPosition.y = (int) position.y;
        System.out.println(rayPosition);
    }

    public void update(Map map){
        double tempX;
        double tempY;
        int mapX;
        int mapY;

        if(inputHandler.forward){
            tempX = position.x + deltaPosition.x * MOVEMENT_SPEED;
            tempY = position.y + deltaPosition.y * MOVEMENT_SPEED;

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
            tempX = position.x - deltaPosition.x * MOVEMENT_SPEED;
            tempY = position.y - deltaPosition.y * MOVEMENT_SPEED;

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
            deltaPosition.x = Math.cos(rotation);
            deltaPosition.y = Math.sin(rotation);
        }
        if(inputHandler.right){
            rotation += ROTATION_SPEED;
            if(rotation >= 2 * Math.PI)
                rotation = 0;

            deltaPosition.x = Math.cos(rotation);
            deltaPosition.y = Math.sin(rotation);
        }
        //System.out.println(position);
        //System.out.println(rotation);
        System.out.println(deltaPosition);
        castRays(map);
    }
}
