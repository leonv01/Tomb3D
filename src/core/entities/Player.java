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

    private final float MOVEMENT_SPEED = Config.MOVEMENT_SPEED;
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
        plane = new Vector2D(0,FOV);
        rotation = 0;
        ray = new Vector2D();
    }

    public Player(Vector2D position){
        this.position = position;
        this.direction = new Vector2D(1,0);
        this.plane = new Vector2D(0,FOV);
        this.ray = new Vector2D();
        rotation = 0;
    }

    /*
     * Wall detection algorithm:
     * DDA
     */
    public void castRays(Map map){
        int mapX = (int) position.x;
        int mapY = (int) position.y;
        
        double rayX = position.x; // Der Startpunkt des Strahls ist gleich der Spielerposition
        double rayY = position.y;
        
        // Berechne die Richtung des Strahls (normalisierte Richtungsvektoren)
        double rayDirectionX = direction.x;
        double rayDirectionY = direction.y;
        
        // Berechne die Entfernung zur nächsten vertikalen Kachelkante
        double deltaX;
        if (rayDirectionX >= 0) {
            deltaX = (mapX + 1 - rayX) / rayDirectionX;
        } else {
            deltaX = (rayX - mapX) / (-rayDirectionX);
        }
        double xIntersection = rayX + deltaX * rayDirectionX;
        
        // Berechne die Entfernung zur nächsten horizontalen Kachelkante
        double deltaY;
        if (rayDirectionY >= 0) {
            deltaY = (mapY + 1 - rayY) / rayDirectionY;
        } else {
            deltaY = (rayY - mapY) / (-rayDirectionY);
        }
        double yIntersection = rayY + deltaY * rayDirectionY;

        // TODO fix pos

        ray.x = xIntersection;
        ray.y = yIntersection;

        System.out.println(ray);
    }

    public void update(Map map){
        double tempX;
        double tempY;
        int mapX;
        int mapY;

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
        //System.out.println(position);
        //System.out.println(rotation);
        castRays(map);
    }

    public void updateDirection(Vector2D vec) {
        vec.x = Math.cos(rotation);
        vec.y = Math.sin(rotation);
        vec.normalize();
    }
    
}
