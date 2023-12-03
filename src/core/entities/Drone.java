package core.entities;

import core.misc.Map;
import core.utils.Config;
import core.utils.Vector2D;

public class Drone {


    public enum Type{
        HEAVY, LIGHT, MEDIUM, BOSS
    }
    private Type type;
    public Vector2D position;
    public Vector2D direction;
    public double rotation;
    public Obstacle obstacle;

    private EntityAttributes attributes;

    public Drone(Vector2D position, Type type) {
        initDrone(position, type);
    }

    public void takeDamage(int damage){
        attributes.takeDamage(damage);
    }

    public void update(Map map, Player player) {
        double diffX = player.position.x - position.x;
        double diffY = player.position.y - position.y;

        rotation = Math.atan2(diffY, diffX);

        direction.x = Math.cos(rotation);
        direction.y = Math.sin(rotation);

        double tempX = position.x + direction.x * attributes.getSpeed();
        double tempY = position.y + direction.y * attributes.getSpeed();

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
            //player.takeDamage(20);
            printAttributes();
        }


        obstacle.setPosition(position);
    }

    private void initDrone(Vector2D position, Type type){
        this.position = position;
        this.rotation = 0;
        this.direction = new Vector2D(1, 0);

        int health = 0;
        double speed = 0;
        int damage = 0;
        int score = 0;

        String texturePath = "src/textures/enemy/";

        this.type = type;

        switch (type){
            case HEAVY -> {
                health = 200;
                speed = 0.01;
                damage = 50;
                score = 500;
                texturePath = texturePath.concat("heavy.png");
            }
            case LIGHT -> {
                health = 100;
                speed = 0.025;
                damage = 20;
                score = 100;
                texturePath = texturePath.concat("light.png");
            }
            case MEDIUM -> {
                health = 150;
                speed = 0.015;
                damage = 30;
                score = 250;
                texturePath = texturePath.concat("medium.png");
            }
            case BOSS -> {
                health = 500;
                speed = 0.005;
                damage = 100;
                score = 1000;
                texturePath = texturePath.concat("boss.png");
            }
        }
        this.attributes = new EntityAttributes(health, speed, 0, 0, damage, health, 0,0,0,score);
        this.obstacle = new Obstacle(texturePath, position, Obstacle.Type.ENEMY, 100);
    }

    public void printAttributes(){
        System.out.println(attributes);
    }
}