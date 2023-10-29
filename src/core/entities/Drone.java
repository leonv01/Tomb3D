package core.entities;

import core.misc.Map;
import core.utils.Config;
import core.utils.Vector2D;

public class Drone {
    public Vector2D position;
    public Vector2D direction;
    public double rotation;

    public Drone() {
        position = new Vector2D();
        direction = new Vector2D();
        rotation = 0;
    }

    public Drone(Vector2D position, Vector2D direction, double rotation) {
        this.direction = direction;
        this.rotation = rotation;
        this.position = position;
    }

    public Drone(Vector2D position) {
        this.position = position;
        this.rotation = 0;
        this.direction = new Vector2D(1, 0);
    }

    public void caluclateAngle(Player player) {
        rotation = Math.acos(
                position.mul(player.position) /
                        (position.length() * player.position.length()));
        System.out.println(Math.toDegrees(rotation));
    }

    public void update(Map map, Player player) {
        // caluclateAngle(player);
        // rotateEnemy();

        double diffX = player.position.x - position.x;
        double diffY = player.position.y - position.y;

        rotation = Math.atan2(diffY, diffX);

        direction.x = Math.cos(rotation);
        direction.y = Math.sin(rotation);

        double tempX = position.x + direction.x * Config.DRONE_SPEED;
        double tempY = position.y + direction.y * Config.DRONE_SPEED;

        int mapX = (int) tempX;
        int mapY = (int) tempY;

        if(map.map[(int) position.y][mapX] == 0){
            position.x = tempX;
        }
        if(map.map[mapY][(int) position.x] == 0){
            position.y = tempY;
        }

/*
        position.x += direction.x * Config.MOVEMENT_SPEED;
        position.y += direction.y * Config.MOVEMENT_SPEED;
 */
    }
}
