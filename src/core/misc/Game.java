package core.misc;

import core.entities.Player;
import core.graphics.Display;
import core.graphics.MapRender;
import core.utils.Vector2D;
import core.entities.Drone;

/**
 * The Game class represents the main game engine that manages the game loop and components.
 */
public class Game implements Runnable{

    // flag for thread.
    private boolean running;

    // Thread for the game engine.
    private final Thread thread;

    // Display object to render the game.
    private final Display display;

    // Player object.
    private final Player player;

    // Map object.
    private final Map map;

    // MapRender object to render 2D view.
    private final MapRender mapRender;

    private Drone enemy;

    private final InputHandler inputHandler;

    /**
     * Constructor for the Game class. Initializes game components and starts the game loop.
     */
    public Game(){
        thread = new Thread(this);
        display = new Display();
        map = new Map();
        inputHandler = new InputHandler();


        player = new Player(
            new Vector2D((double) map.map.length / 2, (double) map.map.length / 2)
        );
        enemy = new Drone(new Vector2D(1.5, 1.5));
        mapRender = new MapRender(map);

        mapRender.setKeyListener(inputHandler);
        display.setKeyListener(inputHandler);
        player.setKeyListener(inputHandler);

        // Create a new thread for display rendering.
        RenderingThread renderingThread = new RenderingThread(display, player);
        Thread renderThread = new Thread(renderingThread);
        renderThread.start();

        start();
    }

    /**
     * The main entry point for the game. Creates an instance of the `Game` class to start the game.
     */
    public static void main(String[] args) {
        new Game();

    }

    /**
     * Start the game loop by setting the 'running' flag to true and starting a new thread.
     */
    public synchronized void start(){
        running = true;
        thread.start();
    }

    /**
     * Stop the game loop by setting the 'running' flag to false and joining the thread.
     */
    public synchronized void stop(){
        running = false;
        try{
            thread.join();
        }catch (InterruptedException e){
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

        // Game loop.
        while(running) {

            // Calculate the time difference.
            long now = System.nanoTime();
            delta = delta + ((now-lastTime) / ns);
            lastTime = now;

            // While loop only gets executed 60 times per second.
            while (delta >= 1)
            {
                frames++;

                // Player gets updated.
                player.update(map);

                // Enemy gets updated.
                //enemy.update(map, player);

                delta--;

                // The MapRender function is called to update.
                mapRender.render(player, enemy);
            }
            //display.render(player);

            // Update the FPS text in the window.
            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                mapRender.setTitle(String.format("%s | %d fps", "MapRender", frames));
                frames = 0;
            }
        }
    }
}
