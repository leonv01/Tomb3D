package core.entities;

import core.utils.Vector2D;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Obstacle {
    private Vector2D position;
    public double z;
    private BufferedImage image;
    private int width, height;
    private boolean collectible, visible;
    private final double radius = 2;
    public Obstacle(String path, Vector2D position, boolean collectible){
        try {
            image = ImageIO.read(new File(path));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.collectible = collectible;
        this.position = position;
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.visible = true;
    }


    public void checkCollision(Vector2D pos){
        int x = (int) position.getX();
        int y = (int) position.getY();

        int posX = (int) pos.getX();
        int posY = (int) pos.getY();

        if(
                x == posX && posY == y
        ){
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
        return collectible;
    }

    public void setCollectible(boolean collectible) {
        this.collectible = collectible;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
