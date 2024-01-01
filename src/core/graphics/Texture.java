package core.graphics;

import java.awt.*;
import java.io.*;
import java.io.IOException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;


/**
 * Represents the texture used for the wall in the game world.
 */
public class Texture {
    private final String path;
    private int[] rgbArray;
    private Color[] colorArray;
    private int size;
    private int textureCount;

    private BufferedImage image;
    /**
     * Constructs a new texture.
     *
     * @param path The path to the texture.
     * @param size The size of the texture.
     * @param textureCount The amount of textures in the texture atlas.
     */
    public Texture(String path, int size, int textureCount){
        this.path = path;
        this.size = size;

        this.rgbArray = new int[size * size * textureCount * 2];
        this.colorArray = new Color[rgbArray.length];
        loadTexture();
        for (int i = 0; i < colorArray.length; i++) {
            colorArray[i] = new Color(rgbArray[i]);
        }
    }

    public Texture(String path){
        this.path = path;
        try {
            image = ImageIO.read(new File(path));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BufferedImage getImage(){
        return image;
    }

    /**
     * Loads in a texture via its path and creating a rgb-Array out of it.
     */
    private void loadTexture(){
        try{
            File textureFile = new File(path);
            BufferedImage image = ImageIO.read(textureFile);
            image.getRGB(
                0, 0, 
                image.getWidth(), image.getHeight(), 
                rgbArray, 0, image.getWidth()
                );
        }catch(IOException e){
            System.out.println("Texture not found!");
        }
    }

    /**
     * Calculates the x and y index in the 2D RGB array and returns its content.
     *
     * @param y Y index.
     * @param x X index.
     * @return The content at the specified indexes.
     */
    public int getRGB(int y, int x){
         y = Math.abs(y);
        return rgbArray[y * (size * 2) + x];
    }

    public Color getColor(int y, int x){

        y = Math.abs(y);
        x = Math.abs(x);
        return colorArray[y * (size * 2) + x];
    }

    public String getPath(){
        return path;
    }

    public int getSize() {
        return size;
    }
}
