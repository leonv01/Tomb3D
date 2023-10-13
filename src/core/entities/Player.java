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

    private void checkHorizontal(){
        int xIdx = (int)rayPosition.x;
        int yIdx = (int)rayPosition.y;

        deltaRayPosition.y = yIdx - rayPosition.y;
        if(rotation <= Math.PI){
            rayPosition.x = -deltaRayPosition.y * 1 / Math.tan(rotation);
            rayPosition.x = position.x - rayPosition.x;
            rayPosition.y = position.y - deltaPosition.y; 
        }
        
        System.out.println("Delta: " + deltaRayPosition);
    }

    public void castRays(){
        checkHorizontal();
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
        System.out.println(position);
        castRays();
    }
}
