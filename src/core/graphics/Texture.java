package core.graphics;

import java.io.*;
import java.io.IOException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;



public class Texture {
    final String path;
    int[] rgbArray;
    int tex_size;

    public Texture(String path, int tex_size){
        this.path = path;
        this.tex_size = tex_size;
        this.rgbArray = new int[tex_size * tex_size];

        loadTexture();
    }

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
