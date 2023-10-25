package core.entities;

import java.util.Vector;

import core.misc.InputHandler;
import core.misc.Map;
import core.utils.Config;
import core.utils.Vector2D;

public class Player {
    public Vector2D position;   // player position
    public Vector2D direction;  // player direction vector
    public Vector2D horizontalVector;
    public Vector2D verticalVector;
    public Vector2D ray;

    private float MOVEMENT_SPEED = Config.MOVEMENT_SPEED;
    private final float ROTATION_SPEED = Config.ROTATION_SPEED;

    public InputHandler inputHandler = new InputHandler();

    double rotation;

    public Player(){
        this.position = new Vector2D();
        this.direction = new Vector2D(1,0);
        this.rotation = 0;
        this.horizontalVector = new Vector2D();
        this.verticalVector = new Vector2D();
        this.ray = new Vector2D();
    }

    public Player(Vector2D position){
        this.position = position;
        this.direction = new Vector2D(1,0);
        this.horizontalVector = new Vector2D();
        this.verticalVector = new Vector2D();
        this.ray = new Vector2D();
        this.rotation = 0;
    }

    public void castRays(Map map){
        double vLength, hLength;

        for (int i = 0; i < 1; i++) {
            horizontalVector = getHorizontalVector(map, rotation);
            verticalVector = getVerticalVector(map, rotation);

            vLength = verticalVector.length();
            hLength = horizontalVector.length();

            if(vLength < hLength)
                ray = verticalVector;
            else 
                ray = horizontalVector;
           
        }
    }

    private Vector2D getHorizontalVector(Map map, double angle){
        int dof = 0;
        int dofEnd = Config.CELL_COUNT_X;
        
        double yRayDelta = position.y - (int)(position.y);

        double rayX = 0, rayY = 0;
        double yRayOff = 0, xRayOff = 0;

        int lookingUp = 1;
        
        // looking down
        if(Math.sin(angle) > 0){
            rayX = -yRayDelta / Math.tan(angle);
            rayX = position.x - rayX;
            rayY = position.y - yRayDelta;

            yRayOff = 1.0;
            xRayOff = yRayOff / -Math.tan(angle);

            lookingUp = -1;
        } else if(Math.sin(angle) < 0){
            double tmp = -yRayDelta + 1.0;
            
            rayX = tmp / Math.tan(angle);
            rayX = position.x - rayX;
            rayY = position.y + tmp;

            yRayOff = -1;
            xRayOff = yRayOff / -Math.tan(angle);

            lookingUp = 0;
        }
        if(Math.sin(angle) == 0 /* angle == Math.PI || angle == 0 || angle == 2 * Math.PI */){
            rayX = position.x;
            rayY = position.y;
            dof = dofEnd;
        }

        while(dof < dofEnd){
            int indexX = (int)(rayX);
            int indexY = (int)(rayY) + lookingUp;

            if(map.inBounds(indexX, indexY) && map.getValue(indexX, indexY) != 0)
                dof = dofEnd;
            else{
                rayX -= xRayOff;
                rayY -= yRayOff;
                dof++;
            }
        }
        return new Vector2D(rayX, rayY);
    }

    private Vector2D getVerticalVector(Map map, double angle){
        int dof = 0;
        int dofEnd = Config.CELL_COUNT_Y;
        
        double xRayDelta = position.x - (int)(position.x);

        double rayX = 0, rayY = 0;
        double yRayOff = 0, xRayOff = 0;

        int lookingLeft = 1;

        if(angle < (3 * Math.PI / 2) && angle > Math.PI / 2)   {
            rayX = xRayDelta;
            rayX = position.x - rayX;
            rayY = xRayDelta * Math.tan(angle);
            rayY = position.y + rayY;

            xRayOff = 1.0;
            yRayOff = xRayOff * -Math.tan(angle);
            
            lookingLeft = -1;
        }
        if (angle > (3 * Math.PI / 2) || angle < Math.PI / 2) {
            xRayDelta = 1.0 - xRayDelta;

            rayX = xRayDelta;
            rayX = position.x + rayX;
            rayY = xRayDelta * Math.tan(angle);
            rayY = position.y - rayY;

            xRayOff = -1.0;
            yRayOff = -xRayOff * Math.tan(angle);

            lookingLeft = 0;
        }
        if(angle == Math.PI / 2 || angle == (3 * Math.PI / 2)){
            rayX = position.x;
            rayY = position.y;

            dof = dofEnd;
        }

        while(dof < dofEnd){
            int indexY = (int)(rayY);
            int indexX = (int)(rayX) + lookingLeft;

            if(map.inBounds(indexX, indexY)	&& map.getValue(indexX, indexY) != 0)
                dof = dofEnd;
            else{
                rayX -= xRayOff;
                rayY -= yRayOff;
                dof++;
            }
        }

        return new Vector2D(rayX, rayY);
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
        }
        if(inputHandler.left){
            rotation += ROTATION_SPEED;
            if(rotation >= 2 * Math.PI)
                rotation = 0;

            updateDirection(direction);
        }
        castRays(map);
    }

    public void updateDirection(Vector2D vec) {
        vec.x = Math.cos(-rotation);
        vec.y = Math.sin(-rotation);
        vec.normalize();
    }
    
}
