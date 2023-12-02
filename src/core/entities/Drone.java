package core.entities;

import core.misc.Map;
import core.utils.Config;
import core.utils.Vector2D;

public class Drone {
    public Vector2D position;
    public Vector2D direction;
    public double rotation;
    public Obstacle obstacle;

    public Drone() {
        position = new Vector2D();
        direction = new Vector2D();
        rotation = 0;
    }

    public Drone(Vector2D position) {
        this.position = position;
        this.rotation = 0;
        this.direction = new Vector2D(1, 0);
        obstacle = new Obstacle("src/textures/brick.png", position, false);
    }

    public void takeDamage(int i){

    }

    public void update(Map map, Player player) {
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

        Vector2D collision = new Vector2D(position);
        collision.add(direction);

        Vector2D collisionRounded = new Vector2D(
                Math.round(collision.x * 100) / 100.0,
                Math.round(collision.y * 100) / 100.0
        );

        Vector2D positionRounded = new Vector2D(
                Math.round(position.x * 100) / 100.0,
                Math.round(position.y * 100) / 100.0
        );

        Vector2D playerRounded = new Vector2D(
                Math.round(player.position.x * 100) / 100.0,
                Math.round(player.position.y * 100) / 100.0
        );

        if(collisionRounded.equals(playerRounded)){
            player.takeDamage(20);
        }


        obstacle.setPosition(position);
    }
}
