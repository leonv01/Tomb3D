package core.entities;

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
    private final BufferedImage image;
    private final int width, height;
    private boolean visible;
    private final double radius = 2;
    private final Type type;
    private final int value;
    public Obstacle(String path, Vector2D position, Type type, int value){
        try {
            image = ImageIO.read(new File(path));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.position = position;
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.visible = true;
        this.type = type;
        this.value = value;
    }


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
                    System.out.println("Collected");
                }
                case OBSTACLE -> System.out.println("Hit obstacle");
                case HEAL_ITEM -> {
                    player.addHealth(value);
                    System.out.println("Healed");
                }
                case AMMO_PACK -> {
                    System.out.println("Ammo");
                    player.addAmmo(value);
                }
                case ENEMY -> {
                    player.takeDamage(value);
                    System.out.println("Hit enemy");
                }
                case KEY -> {
                    System.out.println("Key");
                }
            }
            player.printAttributes();
            visible = false;
            System.out.println("No longer visible");
        }
    }

    public BufferedImage getImage(){
        return image;
    }
    public void setPosition(Vector2D position){
        this.position = position;
    }

    public int getSize(){
        return image.getWidth();
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
}
