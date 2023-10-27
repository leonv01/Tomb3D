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

            Vector2D vTemp = verticalVector.diff(position);
            Vector2D hTemp = horizontalVector.diff(position);

            vLength = vTemp.length();
            hLength = hTemp.length();

        
            System.out.println(vLength + " " + hLength);

            double newX, newY;
            if((vLength < hLength)){
                newX = verticalVector.x;
                newY = verticalVector.y;
            }
            else{
                newX = horizontalVector.x;
                newY = horizontalVector.y;
            }

            ray = new Vector2D(newX, newY);
           
        }
    }

    /*
     * calculates for the angle of the ray the corresponding vector for the horizontal grid check
     * algorithm used: DDA
     * the function returns a 2D Vector for which the length can be determined
     */
    private Vector2D getHorizontalVector(Map map, double angle){
        int dof = 0;    // defines the maximum iterations/extensions for the ray
        int dofEnd = Config.CELL_COUNT_X;   // defines the limit of the depth of field -> maximum count of horizontal cells
        
        double yRayDelta = position.y - (int)(position.y);  // determines the relative position of the player within the current cell

        double rayX = 0, rayY = 0;  
        double yRayOff = 0, xRayOff = 0;

        int lookingUp = 1;  // grid correction for the direction the player looking at
        
        // player lookin down
        if(Math.sin(angle) > 0){
            rayX = -yRayDelta / Math.tan(angle);    // the ray for the current cell ground is calculated based on the relative cell position
            rayX = position.x - rayX;
            rayY = position.y - yRayDelta;

            yRayOff = 1.0;  // y-offset = 1.0 because the player is looking down, so y needs to be positive
            xRayOff = yRayOff / -Math.tan(angle);   // calculate x offset for the next iterations aslong as no wall has been hit

            lookingUp = -1;
        } 
        // player looking up        
        else if(Math.sin(angle) < 0){
            double tmp = -yRayDelta + 1.0;  // get relative cell position by subtracting the delta by the cell-size
            
            rayX = tmp / Math.tan(angle);    // the ray for the current cell ground is calculated based on the relative cell position
            rayX = position.x - rayX;
            rayY = position.y + tmp;

            yRayOff = -1.0;   // y-offset = -1.0 because the player is looking up, so y need to be negative
            xRayOff = yRayOff / -Math.tan(angle);   //calculate x offset for the next iteraions aslong as no wall has been hit

            lookingUp = 0;
        }
        // if player looks directly horizontal, no horizontal lines are visible -> rays are set to players position
        if(Math.sin(angle) == 0){
            rayX = position.x;
            rayY = position.y;
            dof = dofEnd;   // iteration ends directly, because no horizontal wall will be hit
        }

        // iterations goes aslong as no wall has been detected or the indexes are within the array-bounds
        while(dof < dofEnd){
            int indexX = (int)(rayX);   // get current x-index in the map by flooring the x-position of the ray
            int indexY = (int)(rayY) + lookingUp;   // get current y-index in the map by flooring the y-position and adding the grid-correction, depending on the look-direction

            // if indexes are in bounds and value in the map are not empty -> loop exit
            if(map.inBounds(indexX, indexY) && map.getValue(indexX, indexY) != 0)
                dof = dofEnd;
            // else offsets are added on ray position until dof is >= than dofEnd
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
