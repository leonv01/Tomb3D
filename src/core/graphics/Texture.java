package core.graphics;

import java.io.*;
import java.io.IOException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;


/**
 * Represents the texture used for the wall in the game world.
 */
public class Texture {
    final String path;
    int[] rgbArray;
    int size;
    private int textureCount;
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

        loadTexture();
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
}
