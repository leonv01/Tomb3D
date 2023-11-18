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

    public Obstacle(String path, Vector2D position){
        try {
            image = ImageIO.read(new File(path));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.position = position;
        this.width = image.getWidth();
        this.height = image.getHeight();
    }

    public BufferedImage getImage(){
        return image;
    }
    public void setPosition(Vector2D position){
        this.position = position;
    }

    public Vector2D getPosition() {
        return position;
    }
}
