package core.entities;

import core.utils.Vector2D;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Obstacle {
    private Vector2D position;
    public double z;
    private BufferedImage image;
    private int width, height;
    private Color color;

    public Obstacle(String path, Vector2D position){
        try {
            image = ImageIO.read(new File(path));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.position = position;
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.color = Color.BLUE;
    }
    public Obstacle(String path, Vector2D position, Color color){
        try {
            image = ImageIO.read(new File(path));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.position = position;
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.color = color;
    }
    public BufferedImage getImage(){
        return image;
    }
    public void setPosition(Vector2D position){
        this.position = position;
    }
    public Color getColor(){return color;}
    public Vector2D getPosition() {
        return position;
    }
}
