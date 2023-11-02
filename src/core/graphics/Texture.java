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

    /**
     * Constructs a new texture.
     *
     * @param path The path to the texture.
     * @param size The size of the texture.
     */
    public Texture(String path, int size){
        this.path = path;
        this.size = size;
        this.rgbArray = new int[size * size];

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
}
