package core.entities;

import core.misc.Map;
import core.utils.Config;
import core.utils.Ray;
import core.utils.Vector2D;

import javax.swing.*;
import java.util.Random;

public class Drone {

    public enum State{
        IDLE, CHASE, ATTACK, DEAD
    }

    public enum Type{
        HEAVY, LIGHT, MEDIUM, BOSS
    }
    private Type type;
    private State state;
    private Map map;
    private Timer timer;
    private Player player;
    public Vector2D position, direction;
    public double rotation, radius;
    public Obstacle obstacle;

    private EntityAttributes attributes;

    public Drone(Vector2D position, Map map, Type type, Player player) {
        initDrone(position, map, type, player);
    }

    /**
     * Returns the position of the drone.
     * @return The position of the drone.
     */
    public void takeDamage(int damage){
        attributes.takeDamage(damage);
    }

    /**
     * Updates the drone.
     */
    public void update() {
        Vector2D playerPosition = new Vector2D(player.position);

        double distance = playerPosition.sub(position).length();
        if(!attributes.isAlive()) state = State.DEAD;
        else if(distance < radius)
            state = State.CHASE;

        switch(state){
            case IDLE -> idle(playerPosition);
            case CHASE -> chase(playerPosition);
            case ATTACK -> attack(player);
            case DEAD -> dead();
        }


        obstacle.setPosition(position);
    }

    /**
     * The drone will die.
     */
    private void dead(){

    }

    /**
     * The drone will attack the player.
     * @param player The player that the drone will attack.
     */
    private void attack(Player player){
        if(obstacle.isActive()) {
            if (Math.random() < Config.HIT_ACCURACY_THRESHOLD)
                player.takeDamage(attributes.getDamage());
        }
    }

    /**
     * The drone will chase the player.
     * @param playerPosition The position of the player.
     */
    private void chase(Vector2D playerPosition){
        double diffX = playerPosition.x - position.x;
        double diffY = playerPosition.y - position.y;

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

        timer.start();
    }

    /**
     * The drone will idle around the map.
     */
    private void idle(Vector2D playerPosition){

    }

    /**
     * Initializes the drone with a position, map, type and player.
     *
     * @param position The position of the drone.
     * @param map The map that the drone will be placed on.
     * @param type The type of the drone.
     * @param player The player that the drone will attack.
     */
    private void initDrone(Vector2D position, Map map, Type type, Player player){
        this.position = position;
        this.rotation = 0;
        this.direction = new Vector2D(1, 0);
        this.map = map;
        this.radius = 3;
        this.player = player;

        int health = 0;
        double speed = 0;
        int damage = 0;
        int score = 0;

        String texturePath = "src/textures/enemy/";

        this.type = type;
        this.state = State.IDLE;
        this.timer = new Timer(1000, e -> attack(player));
        switch (type){
            case HEAVY -> {
                health = Config.HEAVY_ENEMY_HEALTH;
                speed = Config.HEAVY_ENEMY_SPEED;
                damage = Config.HEAVY_ENEMY_DAMAGE;
                score = Config.HEAVY_ENEMY_SCORE;
                texturePath = texturePath.concat("heavy.png");
            }
            case LIGHT -> {
                health = Config.LIGHT_ENEMY_HEALTH;
                speed = Config.LIGHT_ENEMY_SPEED;
                damage = Config.LIGHT_ENEMY_DAMAGE;
                score = Config.LIGHT_ENEMY_SCORE;
                texturePath = texturePath.concat("light.png");
            }
            case MEDIUM -> {
                health = Config.MEDIUM_ENEMY_HEALTH;
                speed = Config.MEDIUM_ENEMY_SPEED;
                damage = Config.MEDIUM_ENEMY_DAMAGE;
                texturePath = texturePath.concat("medium.png");
                score = Config.MEDIUM_ENEMY_SCORE;
            }
            case BOSS -> {
                health = Config.BOSS_ENEMY_HEALTH;
                speed = Config.BOSS_ENEMY_SPEED;
                damage = Config.BOSS_ENEMY_DAMAGE;
                score = Config.BOSS_ENEMY_SCORE;
                texturePath = texturePath.concat("boss.png");
            }
        }
        this.attributes = new EntityAttributes(health, speed, 0, 0, damage, health, 0,0,0,0,score);
        this.obstacle = new Obstacle(texturePath, position, Obstacle.Type.ENEMY, 100);
    }

    public void printAttributes(){
        System.out.println(attributes);
    }
}