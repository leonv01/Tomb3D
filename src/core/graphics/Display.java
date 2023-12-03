package core.graphics;

import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.JFrame;

import core.entities.Drone;
import core.entities.Obstacle;
import core.entities.Player;
import core.utils.Config;
import core.utils.Ray;
import core.utils.Vector2D;

/**
 * The Display class represents the graphical display for rendering the game world.
 */
public class Display extends JFrame implements Runnable{

    // Displayed image.
    BufferedImage image;
    private final int DIS_HEIGHT = Config.HEIGHT;
    private final int DIS_WIDTH = Config.WIDTH;
    Texture textureAtlas;
    ArrayList<Obstacle> obstacles;
    ArrayList<Drone> drones;
    Player player = null;

    private final Thread thread;
    boolean running;

    double[] zBuffer;
    double[] depth;

    /**
     * Constructs a Display object and initializes the display window.
     */
    public Display(){

        thread = new Thread(this);

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

        obstacles = new ArrayList<>();
        drones = new ArrayList<>();


        zBuffer = new double[Config.rayResolution * Config.FOV];
        depth = new double[zBuffer.length];
        Arrays.fill(zBuffer, Double.MAX_VALUE);
        Arrays.fill(depth, 0);

        // Load debug texture.
        textureAtlas = new Texture("src/textures/texture_atlas_shadow_2.png", 64, 4);
    };

    /**
     * Add KeyListener to this window, so user input can be detected when this window is in foreground.
     *
     * @param k KeyListener to detect user input.
     */
    public void setKeyListener(KeyListener k){
        addKeyListener(k);
    }

    public void addPlayer(Player player){ this.player = player; }
    public void addDrones(ArrayList<Drone> drones){
        this.drones = drones;
    }
    public void addObstacle(ArrayList<Obstacle> obstacles) {this.obstacles = obstacles;}

    public boolean isSpriteBehindPlayer(Player player, Obstacle obstacle){

        Vector2D diff = obstacle.getPosition().sub(player.position);

        double angle = Math.atan2(diff.y, diff.x) - Math.atan2(player.direction.y, player.direction.x);

        angle = Math.atan2(Math.sin(angle), Math.cos(angle));

        double thresholdAngle = Math.PI / 2;
        return Math.abs(angle) > Math.PI - thresholdAngle;
    }
    public void renderSprites(Obstacle obstacle, Graphics2D g){
        double fovRadians = Math.toRadians(Config.FOV);
        double viewPlaneWidth = 2 * Math.tan(fovRadians / 2);

        Vector2D relativePosVector2d = obstacle.getPosition().sub(player.position);
        double distance = relativePosVector2d.length();
        double spriteScreenSize = (((double) (DIS_WIDTH) / distance)); //(zBuffer.length + 1)) / viewPlaneWidth);// * (1 / distance);

        double angle = Math.atan2(relativePosVector2d.y, relativePosVector2d.x) - Math.atan2(player.direction.y, player.direction.x);
        int rectX = (int) (angle * Config.WIDTH / fovRadians + Config.WIDTH / 2 - spriteScreenSize / 2);
        int rectY = (int) (Config.HEIGHT / 2 - spriteScreenSize / 2);



        int rectWidth = (int) spriteScreenSize;
        int rectHeight = (int) spriteScreenSize;

        // Adjust the size of the rendered rectangle
        int smallerRectSize = rectWidth ;/// 2; // Set the size of the smaller rectangle

        // Calculate the new position for the smaller rectangle within the larger one
        int smallerRectX = rectX; //+ (rectWidth - smallerRectSize) / 2;
        int smallerRectY = rectY ;//+ (rectHeight - smallerRectSize) / 2;

        //System.out.println(smallerRectX + " " + smallerRectY);

        int index = (rectX / ((DIS_WIDTH) / (zBuffer.length + 1)));
        if(index >= 0 && index < zBuffer.length - 1){
            if(!(zBuffer[index] < distance))
            {
                if(obstacle.isVisible()) {
                    g.setColor(Color.RED);
                    // Render the smaller rectangle
                    //g.fillRect(smallerRectX, smallerRectY, smallerRectSize, smallerRectSize);
                    g.drawImage(obstacle.getImage(), smallerRectX, smallerRectY, smallerRectSize, smallerRectSize, null);
                }
                //TODO: implement pick up for player
                // player.add(item);

            }
            else{
                //System.out.println(index);
            }
        }
    }

    public void renderWalls(Graphics2D g){
        // Get the array of calculated rays.
        Ray[] rays = player.rays;

        // Calculate the width for each ray on the image.
        int wallWidth = DIS_WIDTH / rays.length + 1;

        // Rendering for each ray.
        for (int i = 0; i < rays.length; i++) {

            double angleDifference = Math.abs(rays[i].getAngle() - player.rotation);
            double correctedDistance = rays[i].getLength() * Math.cos(angleDifference);

            int column = (rays.length - 1) - i;
            depth[i] = correctedDistance;

            if(depth[i] < zBuffer[column]){
                zBuffer[column] = depth[i];
                //System.out.println(zBuffer[column]);
            }

            // Determining the wall height by dividing the maximum wall height possible (the window height) with the individual ray length.
            double wallHeight = (double) DIS_HEIGHT / correctedDistance; //rays[i].getLength();

            // If the wall height for the ray exceeds the maximum wall height, it is reset to the maximum wall height possible (the window height).
            if (wallHeight > DIS_HEIGHT) {
                wallHeight = DIS_HEIGHT;
            }

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

            depth[i] = rays[i].getLength();

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
                    //texelColor = textureAtlas.getRGB(((textureAtlas.size - 1) - texelYHorizontal) + textureAtlas.size * textureAtlasOffset, texelXHorizontal);
                    //color = new Color(texelColor);
                    color = textureAtlas.getColor(((textureAtlas.size - 1) - texelYHorizontal) + textureAtlas.size * textureAtlasOffset, texelXHorizontal);
                }
                else{
                    // Get the RGB value of the texture at the texture atlas offset for vertical walls.
                    //texelColor = textureAtlas.getRGB((textureAtlas.size - 1 - texelYVertical) + textureAtlas.size * textureAtlasOffset, texelXVertical + textureAtlas.size);
                    //color = new Color(texelColor);//.darker().darker();
                    color = textureAtlas.getColor((textureAtlas.size - 1 - texelYVertical) + textureAtlas.size * textureAtlasOffset, texelXVertical + textureAtlas.size);
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
    }

    /**
     * Render the pseudo 3D walls from the players perspective.
     */
    public void render() {
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

        Arrays.fill(zBuffer, Double.MAX_VALUE);


        obstacles.sort(Comparator.comparingDouble(o -> o.getPosition().distance(player.position)));

        renderWalls(g);
        for (Obstacle obstacle:obstacles) {
            renderSprites(obstacle, g);
        }
        for (Drone d : drones) {
            renderSprites(d.obstacle, g);
        }

        bs.show();
    }
    public synchronized void start(){
        running = true;
        thread.start();
    }

    public synchronized void stop(){
        running = false;
        try{
            thread.join();
        } catch (InterruptedException e){
            System.out.println("threads couldn't join");
        }
    }
    @Override
    public void run() {
        long lastTime = System.nanoTime();
        long timer = System.currentTimeMillis();
        final double ns = 1000000000.0 / 60.0;//60 times per second
        double delta = 0;
        int frames = 0;

        while(running){

            // Calculate the time difference.
            long now = System.nanoTime();
            delta = delta + ((now-lastTime) / ns);
            lastTime = now;

            // While loop only gets executed 60 times per second.
            while (delta >= 1)
            {
                frames++;

                // Player gets updated.
                this.render();

                delta--;

            }
        }
    }


}
