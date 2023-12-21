package core.entities;

import core.graphics.Texture;
import core.misc.Map;
import core.utils.Config;
import core.utils.Vector2D;

import javax.swing.*;

public class Drone {

    public enum State{
        IDLE, CHASE, DEAD
    }

    public enum Type{
        HEAVY, LIGHT, MEDIUM, BOSS
    }
    private Type type;
    private State state;
    private Map map;
    private Timer timer, idleTimer, renderAttack, renderReset;
    private Player player;
    public Vector2D position, direction;
    public double rotation, radius;
    public Texture idleSprite;
    public Texture attackStart;
    public Texture attackEnd;
    public Obstacle renderSprite;


    private EntityAttributes attributes;

    public Drone(Vector2D position, Map map, Type type, Player player) {
        initDrone(position, map, type, player);
    }


    public void takeDamage(int damage){
        state = State.CHASE;
        attributes.takeDamage(damage);
        System.out.println(attributes.getHealth());
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
            case DEAD -> dead();
        }
        renderSprite.setPosition(position);
    }

    /**
     * The drone will die.
     */
    private void dead(){
        renderSprite.setVisible(false);
        renderSprite.setActive(false);
        renderSprite.setShootable(false);
    }

    /**
     * The drone will attack the player.
     * @param player The player that the drone will attack.
     */
    private void attack(Player player){
        if(renderSprite.isActive()) {
            if (Math.random() < Config.HIT_ACCURACY_THRESHOLD) {
                player.takeDamage(attributes.getDamage());
                renderSprite.setTexture(attackEnd);
                renderAttack.start();
            }
        }
        timer.stop();
    }

    private void setDirection(double angle){
        direction.x = Math.cos(angle);
        direction.y = Math.sin(angle);
    }

    /**
     * The drone will chase the player.
     * @param playerPosition The position of the player.
     */
    private void chase(Vector2D playerPosition){
        double diffX = playerPosition.x - position.x;
        double diffY = playerPosition.y - position.y;

        rotation = Math.atan2(diffY, diffX);

        setDirection(rotation);

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

        setDirection(rotation);

        double tempX = position.x + direction.x * attributes.getSpeed() * 0.5;
        double tempY = position.y + direction.y * attributes.getSpeed() * 0.5;

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

        idleTimer.start();
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
        String idlePath = "";
        String attackStartPath = "";
        String attackEndPath = "";

        this.type = type;
        this.state = State.IDLE;
        this.timer = new Timer(1000, e -> attack(player));
        this.idleTimer = new Timer(1000, e -> {
            if(state == State.IDLE){
                rotation += Math.random() * Math.PI / 2 * (Math.random() < 0.5 ? -1 : 1);
                if(rotation > Math.PI * 2) rotation -= Math.PI * 2;
            }
        });

        this.renderAttack = new Timer(200, e ->{
            renderSprite.setTexture(attackStart);
            renderAttack.stop();
            renderReset.start();
        });
        this.renderReset = new Timer(200, e -> {
            renderSprite.setTexture(idleSprite);
            renderReset.stop();
        });

        switch (type){
            case HEAVY -> {
                health = Config.HEAVY_ENEMY_HEALTH;
                speed = Config.HEAVY_ENEMY_SPEED;
                damage = Config.HEAVY_ENEMY_DAMAGE;
                score = Config.HEAVY_ENEMY_SCORE;
                idlePath = texturePath.concat("heavy/heavyIdle.png");
                attackStartPath = texturePath.concat("heavy/heavyAttack.png");
                attackEndPath = texturePath.concat("heavy/heavyAttacking.png");
            }
            case LIGHT -> {
                health = Config.LIGHT_ENEMY_HEALTH;
                speed = Config.LIGHT_ENEMY_SPEED;
                damage = Config.LIGHT_ENEMY_DAMAGE;
                score = Config.LIGHT_ENEMY_SCORE;
                idlePath = texturePath.concat("light/lightIdle.png");
                attackStartPath = texturePath.concat("light/lightAttack.png");
                attackEndPath = texturePath.concat("light/lightAttacking.png");
            }
            case MEDIUM -> {
                health = Config.MEDIUM_ENEMY_HEALTH;
                speed = Config.MEDIUM_ENEMY_SPEED;
                damage = Config.MEDIUM_ENEMY_DAMAGE;
                idlePath = texturePath.concat("medium/mediumIdle.png");
                attackStartPath = texturePath.concat("medium/mediumAttack.png");
                attackEndPath = texturePath.concat("medium/mediumAttacking.png");
                score = Config.MEDIUM_ENEMY_SCORE;
            }
            case BOSS -> {
                health = Config.BOSS_ENEMY_HEALTH;
                speed = Config.BOSS_ENEMY_SPEED;
                damage = Config.BOSS_ENEMY_DAMAGE;
                score = Config.BOSS_ENEMY_SCORE;
                idlePath = texturePath.concat("medium/mediumIdle.png");
                attackStartPath = texturePath.concat("medium/mediumAttack.png");
                attackEndPath = texturePath.concat("medium/mediumAttack.png");
            }
        }
        this.attributes = new EntityAttributes(health, speed, 0, 0, damage, health, 0,0,0,0,score);
        //this.idleSprite = new Obstacle(idlePath, position, Obstacle.Type.ENEMY, 100);
        //this.attackSprite = new Obstacle(attackPath, position, Obstacle.Type.ENEMY, 100);
        //this.attackingSprite = new Obstacle(attackingPath, position, Obstacle.Type.ENEMY, 100);

        //renderSprite = idleSprite;

        this.idleSprite = new Texture(idlePath);
        this.attackEnd = new Texture(attackEndPath);
        this.attackStart = new Texture(attackStartPath);

        this.renderSprite = new Obstacle(idlePath, position, Obstacle.Type.ENEMY, 200);
    }

    public void printAttributes(){
        System.out.println(attributes);
    }
}