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

    public void castRays(Map map){
        int dof = 0;
        int dofEnd = Config.CELL_COUNT_X;
        
        double yRayDelta = position.y - (int)(position.y);

        double rayX = 0, rayY = 0;
        double yRayOff = 0, xRayOff = 0;

        double lookingUp = 1.0;
        
        // looking down
        if(rotation < Math.PI){
            rayX = -yRayDelta / Math.tan(rotation);
            rayX = position.x - rayX;
            rayY = position.y - yRayDelta;

            yRayOff = 1.0;
            xRayOff = yRayOff / -Math.tan(rotation);

            lookingUp = -1.0;
        } else if(rotation > Math.PI){
            double tmp = -yRayDelta + 1.0;
            
            rayX = tmp / Math.tan(rotation);
            rayX = position.x - rayX;
            rayY = position.y + tmp;

            yRayOff = -1.0;
            xRayOff = yRayOff / -Math.tan(rotation);

            lookingUp = 0;
        }
        if(rotation == Math.PI || rotation == 0 || rotation == 2 * Math.PI){
            rayX = position.x;
            rayY = position.y;
            dof = dofEnd;
        }

        while(dof < dofEnd){
            int indexX = (int)(rayX);
            int indexY = (int)(rayY + lookingUp);

            System.out.println(indexX + " " + indexY);
            if(map.inBounds(indexX, indexY) && map.getValue(indexX, indexY) != 0)
                dof = dofEnd;
            else{
                rayX -= xRayOff;
                rayY -= yRayOff;
                dof++;
            }
        }
        ray = new Vector2D(rayX, rayY);
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
        if(inputHandler.right){
            rotation -= ROTATION_SPEED;
            if(rotation <= 0)
                rotation = 2 * Math.PI;
            updateDirection(direction);
            updateDirection(plane);
        }
        if(inputHandler.left){
            rotation += ROTATION_SPEED;
            if(rotation >= 2 * Math.PI)
                rotation = 0;

            updateDirection(direction);
            updateDirection(plane);
        }
        castRays(map);
    }

    public void updateDirection(Vector2D vec) {
        vec.x = Math.cos(-rotation);
        vec.y = Math.sin(-rotation);
        vec.normalize();
    }
    
}
