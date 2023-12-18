package core.entities;

import core.graphics.Texture;
import core.utils.Vector2D;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Obstacle {
    public enum Type{
        COLLECTIBLE, OBSTACLE, HEAL_ITEM, AMMO_PACK, ENEMY, KEY
    }

    private Vector2D position;
    public double z;
    private Texture texture;
    private final int width, height;
    private boolean visible, active, shootable;
    private final double radius = 2;
    private final Type type;
    private final int value;
    public Obstacle(String path, Vector2D position, Type type, int value){

           // image = ImageIO.read(new File(path));
        texture = new Texture(path);



        this.position = position;
        this.width = texture.getImage().getWidth();
        this.height = texture.getImage().getHeight();
        this.visible = true;
        this.active = true;
        this.shootable = false;
        this.type = type;
        this.value = value;
    }


    public void initObstacle(){

    }

    /**
     * Checks if the player is colliding with the obstacle.
     * @param player The player object.
     */
    public void checkCollision(Player player){
        int x = (int) position.getX();
        int y = (int) position.getY();

        int posX = (int) player.getX();
        int posY = (int) player.getY();

        if(
                x == posX && posY == y &&
                visible
        ){
            switch (type) {
                case COLLECTIBLE -> {
                    player.addScore(value);
                }
                case OBSTACLE -> {
                    return;
                }
                case HEAL_ITEM -> {
                    player.addHealth(value);
                }
                case AMMO_PACK -> {
                    player.addAmmo(value);
                }
                case ENEMY -> {
                    player.takeDamage(value);
                }
                case KEY -> {
                    player.addKey();
                    System.out.println("KEY");
                }
            }
            player.printAttributes();
            visible = false;
            System.out.println("No longer visible");
        }
    }

    public Texture getTexture(){
        return texture;
    }

    public void setPosition(Vector2D position){
        this.position = position;
    }

    public int getSize(){
        return texture.getImage().getWidth();
    }

    public Vector2D getPosition() {
        return position;
    }

    public boolean isCollectible() {
        return type.equals(Type.COLLECTIBLE);
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setShootable(boolean shootable){
        this.shootable = shootable;
    }

    public boolean getShootable(){
        return shootable;
    }

    public boolean isActive(){
        return active;
    }

    public void setActive(boolean active){
        this.active = active;
    }

    public Type getType(){ return type; }

    public void setTexture(Texture texture) { this.texture = texture; }
}
