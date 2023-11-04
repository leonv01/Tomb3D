package core.graphics;

import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import core.entities.Player;
import core.utils.Config;
import core.utils.Ray;

/**
 * The Display class represents the graphical display for rendering the game world.
 */
public class Display extends JFrame{

    // Displayed image.
    BufferedImage image;
    private final int DIS_HEIGHT = Config.HEIGHT;
    private final int DIS_WIDTH = Config.WIDTH;
    Texture textureAtlas;

    /**
     * Constructs a Display object and initializes the display window.
     */
    public Display(){
        // Initializing image renderer with Config values and RGB type.
        image = new BufferedImage(DIS_WIDTH, DIS_HEIGHT, BufferedImage.TYPE_INT_RGB);
        // Set window size with in Config defined values.
        setSize(DIS_WIDTH, DIS_HEIGHT);
        // Disable window resize.
        setResizable(false);
        // Set window title
        setTitle("Tomb3D");
        // Set default close operation.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setBackground(Color.BLACK);
        setLocationRelativeTo(null);
        setVisible(true);

        // Load debug texture.
        textureAtlas = new Texture("C:\\Users\\leonv\\Documents\\Tomb3D\\Tomb3D\\src\\textures\\texture_atlas_shadow.png", 64, 3);
    };

    /**
     * Add KeyListener to this window, so user input can be detected when this window is in foreground.
     *
     * @param k KeyListener to detect user input.
     */
    public void setKeyListener(KeyListener k){
        addKeyListener(k);
    }

    /**
     * Render the pseudo 3D walls from the players perspective.
     *
     * @param player Player object to access properties.
     */
    public void render(Player player) {
        BufferStrategy bs = getBufferStrategy();
        if(bs == null){
            createBufferStrategy(3);
            return;
        }

        // Get Graphics2D component for more functionality.
        Graphics2D g = (Graphics2D) bs.getDrawGraphics();

        /*
        Sky color:
        Fill a rectangle from top to half of the window height with the color gray.
         */
        g.setColor(Config.colorSky);
        g.fillRect(0, 0, DIS_WIDTH, DIS_HEIGHT / 2);

        /*
        Floor color:
        Fill a rectangle from the half of the window height to the end of the window height with the color light gray.
         */
        g.setColor(Config.colorGround);
        g.fillRect(0, DIS_HEIGHT / 2, DIS_WIDTH, DIS_HEIGHT);

        // Get the array of calculated rays.
        Ray[] rays = player.rays;

        // Calculate the width for each ray on the image.
        int wallWidth = DIS_WIDTH / rays.length + 1;

        // Rendering for each ray.
        for (int i = 0; i < rays.length; i++) {

            // Determining the wall height by dividing the maximum wall height possible (the window height) with the individual ray length.
            double wallHeight = (double) DIS_HEIGHT / rays[i].getLength();

            // If the wall height for the ray exceeds the maximum wall height, it is reset to the maximum wall height possible (the window height).
            if (wallHeight > DIS_HEIGHT) wallHeight = DIS_HEIGHT;

            // Value to reset the walls to the center of the y-axis. Without this offset the walls would start in the middle of the window and exceed the window height.
            double lineOffset = (DIS_HEIGHT / 2.0);

            // Value to let the rays render from right to left.
            int temp = (rays.length - 1) - i;

            // Calculate the texture coordinates based on the hit point
            double textureXHorizontal = rays[i].getX() % 1.0; // Normalize the hit point's X-coordinate
            double textureXVertical = rays[i].getY() % 1.0;

            // Map the normalized X-coordinate to the texture width
            int texelXHorizontal = (int) (textureXHorizontal * textureAtlas.size);
            int texelXVertical = (int) (textureXVertical * textureAtlas.size);

            for (int j = 0; j < wallHeight; j++) {
                // Calculate the texel Y-coordinate based on the wall height
                int texelYHorizontal = (int) ((j / wallHeight) * textureAtlas.size);
                int texelYVertical = (int) ((j / wallHeight) * textureAtlas.size);

                // Get the color of the texel from the texture
                int texelColor;
                int textureAtlasOffset = rays[i].getWallID() - 1;
                Color color;

                if(rays[i].getHorizontal()) {
                    // Get the RGB value of the texture at the texture atlas offset for horizontal walls.
                    texelColor = textureAtlas.getRGB(((textureAtlas.size - 1) - texelYHorizontal) + textureAtlas.size * textureAtlasOffset, texelXHorizontal);
                    color = new Color(texelColor);
                }
                else{
                    // Get the RGB value of the texture at the texture atlas offset for vertical walls.
                    texelColor = textureAtlas.getRGB((textureAtlas.size - 1 - texelYVertical) + textureAtlas.size * textureAtlasOffset, texelXVertical + textureAtlas.size);
                    color = new Color(texelColor);//.darker().darker();
                }

                // Set the color of the wall segment
                g.setColor(color);


                int y1 = (int) (lineOffset + wallHeight / 2 - j);
                int y2 = (int) (lineOffset + wallHeight / 2 - j + 1);

                int x = ((temp) * wallWidth);
                int width = wallWidth;
                int height = 1; // Adjust this to set the wall segment thickness

                // Use fillRect to draw the wall segment with texture
                g.fillRect(x, y1, width, height);
            }
        }
        bs.show();
    }
}
