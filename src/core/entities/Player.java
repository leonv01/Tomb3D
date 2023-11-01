package core.entities;

import core.misc.InputHandler;
import core.misc.Map;
import core.utils.Config;
import core.utils.Ray;
import core.utils.Vector2D;

import java.awt.*;

public class Player {
    public Vector2D position;   // player position
    public Vector2D direction;  // player direction vector
    public Ray horizontal;
    public Ray vertical;
    public Ray[] rays;

    public InputHandler inputHandler = new InputHandler();

    double rotation;
    
    int fov = Config.FOV;

    public Player(){
        this.position = new Vector2D();
        this.direction = new Vector2D(1,0);
        this.rotation = 0;
        this.horizontal = new Ray();
        this.vertical = new Ray();
        this.rays = new Ray[Config.rayResolution * fov];
        for (int i = 0; i < rays.length; i++) {
            rays[i] = new Ray();
        }
    }

    public Player(Vector2D position){
        this.position = position;
        this.direction = new Vector2D(1,0);
        this.horizontal = new Ray();
        this.vertical = new Ray();
        this.rays = new Ray[Config.rayResolution * fov];
        for (int i = 0; i < rays.length; i++) {
            rays[i] = new Ray();
        }
        this.rotation = 0;
    }

    public void castRays(Map map){
        double vLength, hLength;

        double lookRadiant = rotation - (Math.toRadians(fov) / Config.rayResolution);
        if(lookRadiant < 0) lookRadiant += 2 * Math.PI;
        if(lookRadiant > 2 * Math.PI) lookRadiant -= 2 * Math.PI;

        for (int i = 0; i < rays.length; i++) {

            double tempAngle = lookRadiant + Math.toRadians((i / (double)Config.rayResolution));
            if(tempAngle < 0) tempAngle += 2 * Math.PI;
            if(tempAngle > 2 * Math.PI) tempAngle -= 2 * Math.PI;

            this.horizontal = getHorizontalVector(map, tempAngle);
            vertical = getVerticalVector(map, tempAngle);

            this.horizontal.calculateDifference(position);
            vertical.calculateDifference(position);

            double newX, newY, length;
            Color wallColor;
            boolean horizontal;
            if((vertical.getLength() < this.horizontal.getLength())){
                newX = vertical.getX();
                newY = vertical.getY();
                length = vertical.getLength();
                wallColor = vertical.getColor();
                horizontal = false;
            }
            else{
                newX = this.horizontal.getX();
                newY = this.horizontal.getY();
                length = this.horizontal.getLength();
                wallColor = this.horizontal.getColor().darker();
                horizontal = true;
            }
            double temp = rotation - tempAngle;
            if(temp < 0) temp += 2 * Math.PI;
            if(temp > 2 * Math.PI) temp -= 2 * Math.PI;
            length *= Math.cos(temp);
            rays[i] = new Ray(new Vector2D(newX, newY), length, wallColor, horizontal);
        }
    }

    /*
     * calculates for the angle of the ray the corresponding vector for the horizontal grid check
     * algorithm used: DDA
     * the function returns a 2D Vector for which the length can be determined
     */
    private Ray getHorizontalVector(Map map, double angle){
        int dof = 0;    // defines the maximum iterations/extensions for the ray
        int dofEnd = Config.CELL_COUNT_X;   // defines the limit of the depth of field -> maximum count of horizontal cells
        
        double yRayDelta = position.y - (int)(position.y);  // determines the relative position of the player within the current cell

        double rayX = 0, rayY = 0;  
        double yRayOff = 0, xRayOff = 0;

        int lookingUp = 1;  // grid correction for the direction the player looking at
        
        // player looking down
        if(Math.sin(angle) > 0){
            rayX = -yRayDelta / Math.tan(angle);    // the ray for the current cell ground is calculated based on the relative cell position
            rayX = position.x - rayX;
            rayY = position.y - yRayDelta;

            yRayOff = 1.0;  // y-offset = 1.0 because the player is looking down, so y needs to be positive
            xRayOff = yRayOff / -Math.tan(angle);   // calculate x offset for the next iterations as long as no wall has been hit

            lookingUp = -1;
        } 
        // player looking up        
        else if(Math.sin(angle) < 0){
            double tmp = -yRayDelta + 1.0;  // get relative cell position by subtracting the delta by the cell-size
            
            rayX = tmp / Math.tan(angle);    // the ray for the current cell ground is calculated based on the relative cell position
            rayX = position.x - rayX;
            rayY = position.y + tmp;

            yRayOff = -1.0;   // y-offset = -1.0 because the player is looking up, so y need to be negative
            xRayOff = yRayOff / -Math.tan(angle);   //calculate x offset for the next iterations as long as no wall has been hit

            lookingUp = 0;
        }
        // if player looks directly horizontal, no horizontal lines are visible -> rays are set to players position
        if(Math.sin(angle) == 0){
            rayX = position.x;
            rayY = position.y;
            dof = dofEnd;   // iteration ends directly, because no horizontal wall will be hit
        }

        Color color = Color.GRAY;
        // iterations goes as long as no wall has been detected or the indexes are within the array-bounds
        while(dof < dofEnd){
            int indexX = (int)(rayX);   // get current x-index in the map by flooring the x-position of the ray
            int indexY = (int)(rayY) + lookingUp;   // get current y-index in the map by flooring the y-position and adding the grid-correction, depending on the look-direction

            // if indexes are in bounds and value in the map are not empty -> loop exit
            if(map.inBounds(indexX, indexY) && map.getValue(indexX, indexY) != 0) {
                dof = dofEnd;
                color = map.getColor(indexX, indexY);
            }
            // else offsets are added on ray position until dof is >= than dofEnd
            else{
                rayX -= xRayOff;
                rayY -= yRayOff;
                dof++;
            }
        }
        return new Ray(new Vector2D(rayX, rayY), 0, color, false);
    }

    /*
     * calculates for the angle of the ray the corresponding vector for the vertical grid check
     * algorithm used: DDA
     * the function returns a 2D Vector for which the length can be determined
     */
    private Ray getVerticalVector(Map map, double angle){
        int dof = 0;
        int dofEnd = Config.CELL_COUNT_Y;
        
        double xRayDelta = position.x - (int)(position.x);

        double rayX = 0, rayY = 0;
        double yRayOff = 0, xRayOff = 0;

        int lookingLeft = 1;
        /* angle < (3 * Math.PI / 2) && angle > Math.PI / 2 */
        if(Math.cos(angle) < 0)   {
            rayX = xRayDelta;
            rayX = position.x - rayX;
            rayY = xRayDelta * Math.tan(angle);
            rayY = position.y + rayY;

            xRayOff = 1.0;
            yRayOff = xRayOff * -Math.tan(angle);
            
            lookingLeft = -1;
        }
        // angle > (3 * Math.PI / 2) || angle < Math.PI / 2
        if (Math.cos(angle) > 0) {
            xRayDelta = 1.0 - xRayDelta;

            rayX = xRayDelta;
            rayX = position.x + rayX;
            rayY = xRayDelta * Math.tan(angle);
            rayY = position.y - rayY;

            xRayOff = -1.0;
            yRayOff = -xRayOff * Math.tan(angle);

            lookingLeft = 0;
        }
        //angle == Math.PI / 2 || angle == (3 * Math.PI / 2)
        if(Math.cos(angle) == 0){
            rayX = position.x;
            rayY = position.y;

            dof = dofEnd;
        }

        Color color = Color.GRAY;

        while(dof < dofEnd){
            int indexY = (int)(rayY);
            int indexX = (int)(rayX) + lookingLeft;

            if(map.inBounds(indexX, indexY)	&& map.getValue(indexX, indexY) != 0) {
                dof = dofEnd;
                color = map.getColor(indexX, indexY);
            }
            else{
                rayX -= xRayOff;
                rayY -= yRayOff;
                dof++;
            }
        }

        return new Ray(new Vector2D(rayX, rayY), 0, color, true);
    }

    public void update(Map map){
        double tempX;
        double tempY;
        int mapX;
        int mapY;

        float MOVEMENT_SPEED = Config.MOVEMENT_SPEED;
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
        float ROTATION_SPEED = Config.ROTATION_SPEED;
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

    public double getX(){
        return position.x;
    }
    public double getY(){
        return position.y;
    }

    public void updateDirection(Vector2D vec) {
        vec.x = Math.cos(-rotation);
        vec.y = Math.sin(-rotation);
        vec.normalize();
    }
    
}
