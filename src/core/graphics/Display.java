package core.graphics;

import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JFrame;

import core.entities.Drone;
import core.entities.Obstacle;
import core.entities.Player;
import core.misc.InputHandler;
import core.misc.Map;
import core.utils.Config;
import core.utils.Ray;
import core.utils.Vector2D;

/**
 * The Display class represents the graphical display for rendering the game world.
 */
public class Display extends JFrame implements Runnable{
    MapRender mapRender;
    Map map;

    // Displayed image.
    BufferedImage image;
    private final int DIS_HEIGHT = Config.HEIGHT;
    private final int DIS_WIDTH = Config.WIDTH;
    Texture textureAtlas;
    ArrayList<Obstacle> obstacles;
    ArrayList<Drone> drones;
    ArrayList<Obstacle> renderSprite;

    Player player = null;

    InputHandler inputHandler;

    private final Thread thread;
    boolean running;

    double[] zBuffer;
    double[] depth;

    /**
     * Constructs a Display object and initializes the display window.
     */
    public Display(){

        this.inputHandler = new InputHandler();

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
        renderSprite = new ArrayList<>();


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
        this.inputHandler = (InputHandler) k;
        addKeyListener(k);
    }

    /**
     * Add a map to the renderer.
     * @param map The map to be added.
     */
    public void addMap(Map map){
        this.map = map;
    }

    /**
     * Add a player to the renderer.
     * @param player The player to be added.
     */
    public void addPlayer(Player player){ this.player = player; }

    /**
     * Add a drone to the list of obstacles.
     * @param drones The drone to be added.
     */
    public void addDrones(ArrayList<Drone> drones){
        this.drones = drones;
        for(Drone drone : drones){
            obstacles.add(drone.renderSprite);
        }
    }

    /**
     * Add an obstacle to the list of obstacles.
     * @param obstacles The obstacle to be added.
     */
    public void addObstacle(ArrayList<Obstacle> obstacles) {
        this.obstacles = obstacles;
        this.renderSprite.addAll(obstacles);
    }

    public boolean isSpriteBehindPlayer(Player player, Obstacle obstacle){

        Vector2D diff = obstacle.getPosition().sub(player.position);

        double angle = Math.atan2(diff.y, diff.x) - Math.atan2(player.direction.y, player.direction.x);

        angle = Math.atan2(Math.sin(angle), Math.cos(angle));

        double thresholdAngle = Math.PI / 2;
        return Math.abs(angle) > Math.PI - thresholdAngle;
    }

    /**
     * Render the sprites from the players perspective.
     * @param obstacle The obstacle to be rendered.
     * @param g Graphics2D component.
     */
    public void renderSprites(Obstacle obstacle, Graphics2D g) {
        double fovRadians = Math.toRadians(Config.FOV);
        double viewPlaneWidth = 2 * Math.tan(fovRadians / 2);

        Vector2D relativePosVector2d = obstacle.getPosition().sub(player.position);
        double distance = relativePosVector2d.length();
        double spriteScreenSize = (((double) (DIS_WIDTH) / distance));

        double angle = Math.atan2(relativePosVector2d.y, relativePosVector2d.x) - Math.atan2(player.direction.y, player.direction.x);
        angle = (angle + 2 * Math.PI) % (2 * Math.PI); // Ensure angle is within [0, 2*PI]

        if (angle > Math.PI) {
            angle -= 2 * Math.PI;
        }
        int rectX = (int) (angle * Config.WIDTH / fovRadians + Config.WIDTH / 2 - spriteScreenSize / 2);
        int rectY = (int) (Config.HEIGHT / 2 - spriteScreenSize / 2);

        int rectWidth = (int) spriteScreenSize;
        int rectHeight = (int) spriteScreenSize;

        // Adjust the position to render the sprite from its center
        int centerX = rectX + rectWidth / 2;
        int centerY = rectY + rectHeight / 2;

        // Calculate the new position for the smaller rectangle within the larger one
        int smallerRectSize = rectWidth;
        int smallerRectX = centerX - smallerRectSize / 2;
        int smallerRectY = centerY - smallerRectSize / 2;

        int lowerBound = (int) (0.35 * zBuffer.length);
        int upperBound = (int) (0.65 * zBuffer.length);

        int index = (int) ((double) (centerX)  / Config.WIDTH * zBuffer.length);
        if (index >= 0 && index < zBuffer.length) {
            if (distance < zBuffer[index]) {
                obstacle.setActive(true);
                if (obstacle.isVisible()) {
                    // Render the image from its center
                    g.drawImage(obstacle.getTexture().getImage(), smallerRectX, smallerRectY, smallerRectSize, smallerRectSize, null);

                    if(index < upperBound && index > lowerBound) {
                        obstacle.setShootable(true);
                    }
                    else obstacle.setShootable(false);
                }
                //TODO: implement pick up for player
                // player.add(item);
            }
            else {
                obstacle.setActive(false);
            }
        }
    }


    /**
     * Render the walls from the players perspective.
     * @param g Graphics2D component.
     */
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

                if((textureAtlasOffset == -2)){
                    color = Color.BLACK;
                } else if(rays[i].getHorizontal()) {
                    // Get the RGB value of the texture at the texture atlas offset for horizontal walls.
                    //texelColor = textureAtlas.getRGB(((textureAtlas.size - 1) - texelYHorizontal) + textureAtlas.size * textureAtlasOffset, texelXHorizontal);
                    //color = new Color(texelColor);
                    color = textureAtlas.getColor(((textureAtlas.size - 1) - texelYHorizontal) + textureAtlas.size * textureAtlasOffset, texelXHorizontal);
                }
                else {
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
     * Render the image to the window.
     */
    public void render() {
        BufferStrategy bs = getBufferStrategy();
        if(bs == null){
            createBufferStrategy(3);
            return;
        }

        // Get Graphics2D component for more functionality.
        Graphics2D g = (Graphics2D) bs.getDrawGraphics();

        renderSprite.addAll(obstacles);
        drones.forEach(drone -> renderSprite.add(drone.renderSprite));
        //renderSprite.add(obstacles.get(0));
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

        renderSprite.sort((o1, o2) -> Double.compare(o2.getPosition().sub(player.position).length(), o1.getPosition().sub(player.position).length()));

        renderWalls(g);
        for (Obstacle obstacle:renderSprite) {
            renderSprites(obstacle, g);
        }

        renderSprite.clear();
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

                if(inputHandler.map) {
                    add(new MapRender(map, player, drones));
                    System.out.println("map");
                }

                delta--;

            }

            if(System.currentTimeMillis() - timer > 1000){
                timer += 1000;
                setTitle("Tomb3D - FPS: " + frames);
                frames = 0;
            }
        }
    }


}
