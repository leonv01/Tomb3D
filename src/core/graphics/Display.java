package core.graphics;

import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JFrame;

import core.entities.Player;
import core.utils.Config;
import core.utils.Ray;

public class Display extends JFrame{

    BufferedImage image;
    private final int DIS_HEIGHT = Config.HEIGHT;
    private final int DIS_WIDTH = Config.WIDTH;
    Texture texture;

    ArrayList<Texture> textureAtlas;

    public Display(){
        image = new BufferedImage(DIS_WIDTH, DIS_HEIGHT, BufferedImage.TYPE_INT_RGB);
        setSize(DIS_WIDTH, DIS_HEIGHT);
        setResizable(false);
        setTitle("Tomb3D");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(Color.BLACK);
        setLocationRelativeTo(null);
        setVisible(true);

        texture = new Texture("C:\\Users\\leonv\\Documents\\Tomb3D\\Tomb3D\\src\\textures\\wood.png", 64);
    };

    public void setKeyListener(KeyListener k){
        addKeyListener(k);
    }

    public void render(Player player) {
        BufferStrategy bs = getBufferStrategy();
        if(bs == null){
            createBufferStrategy(3);
            return;
        }
        Graphics2D g = (Graphics2D) bs.getDrawGraphics();

        g.setColor(Color.GRAY);
        g.fillRect(0, 0, DIS_WIDTH, DIS_HEIGHT / 2);

        g.setColor(Color.lightGray);
        g.fillRect(0, DIS_HEIGHT / 2, DIS_WIDTH, DIS_HEIGHT);

        Ray[] rays = player.rays;
        int xOffset = DIS_WIDTH / rays.length;

        for (int i = 0; i < rays.length; i++) {
            double wallHeight = (double) DIS_HEIGHT / rays[i].getLength();

            if (wallHeight > DIS_HEIGHT) wallHeight = DIS_HEIGHT;

            double lineOffset = (DIS_HEIGHT / 2.0);
            int temp = (rays.length - 1) - i;

            // Calculate the texture coordinates based on the hit point
            double textureX = rays[i].getX() % 1.0; // Normalize the hit point's X-coordinate

            // Map the normalized X-coordinate to the texture width
            int texelX = (int) (textureX * texture.tex_size);

            for (int j = 0; j < wallHeight; j++) {
                // Calculate the texel Y-coordinate based on the wall height
                int texelY = (int) ((j / wallHeight) * texture.tex_size);

                // Get the color of the texel from the texture
                int texelColor = texture.rgbArray[texelY * texture.tex_size + texelX];

                // Set the color of the wall segment
                Color color = new Color(texelColor);
                if(!rays[i].getHorizontal()){
                    color.darker();
                }
                g.setColor(color);


                int y1 = (int) (lineOffset + wallHeight / 2 - j);
                int y2 = (int) (lineOffset + wallHeight / 2 - j + 1);

                int x = ((temp) * xOffset) + xOffset;
                int width = xOffset;
                int height = 1; // Adjust this to set the wall segment thickness

                // Use fillRect to draw the wall segment with texture
                g.fillRect(x, y1, width, height);
            }
        }
        bs.show();
    }


/*
    public void render(Player player){
        BufferStrategy bs = getBufferStrategy();
        if(bs == null){
            createBufferStrategy(3);
            return;
        }
        Graphics2D g = (Graphics2D) bs.getDrawGraphics();
        
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, DIS_WIDTH, DIS_HEIGHT / 2);

        g.setColor(Color.lightGray);
        g.fillRect(0, DIS_HEIGHT / 2, DIS_WIDTH, DIS_HEIGHT);

        Ray[] rays = player.rays;

        for (int i = 0; i < rays.length; i++) {

            double wallHeight = (double) DIS_HEIGHT / rays[i].getLength();

            if (wallHeight > DIS_HEIGHT) wallHeight = DIS_HEIGHT;

            double lineOffset = (DIS_HEIGHT / 2.0);

            int xOffset = DIS_WIDTH / (rays.length );

            int temp = (rays.length - 1) - i;

            int textYoff = (int) wallHeight / texture.tex_size;

            g.setColor(rays[i].getColor());

            g.setStroke(new BasicStroke(xOffset));

            int x = ((temp) * xOffset) + xOffset;
            int y = (int) (lineOffset - wallHeight / 2);
            int lineHeight = (int) (wallHeight);



           // g.fillRect(x, y, xOffset, lineHeight);
            int s = 0;
            int yOff = (int)(lineOffset - wallHeight / 2);
            for (int j = 0; j < wallHeight; j+= textYoff) {
                g.fillRect(x, yOff, xOffset, textYoff);
                yOff += textYoff;
            }
        }
/*
            int t = 0;
            for (int j = 0; j < wallHeight; j++) {

                int y1 = (int)(lineOffset + wallHeight / 2 - j);
                int y2 = (int)(lineOffset + wallHeight / 2 - j + 1);
                g.drawLine(((temp) * xOffset) +xOffset , y1, ((temp) * xOffset) + xOffset , y2);
            }
            //g.drawLine(((temp) * xOffset) +xOffset , (int)(lineOffset + wallHeight), ((temp) * xOffset) + xOffset , (int)(lineOffset - wallHeight));
        }

        bs.show();
    }
  */
}
