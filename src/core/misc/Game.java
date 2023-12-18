package core.misc;

import core.entities.Obstacle;
import core.entities.Player;
import core.graphics.Display;
import core.graphics.MapRender;
import core.utils.Vector2D;
import core.entities.Drone;

import java.util.ArrayList;

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



    // MapRender object to render 2D view.
 //   private final MapRender mapRender;
    private Map currentMap;

    // Map object.
    private final ArrayList<Map> map;
    private final ArrayList<Drone> enemy;
    private final ArrayList<Obstacle> obstacles;
    private final InputHandler inputHandler;

    /**
     * Constructor for the Game class. Initializes game components and starts the game loop.
     */
    public Game(){
        thread = new Thread(this);
        display = new Display();
        inputHandler = new InputHandler();

        map = new ArrayList<>();
        map.add(new Map());
        currentMap = map.get(0);

        player = new Player(
                new Vector2D((double) currentMap.map.length / 2, (double) currentMap.map.length / 2)
        );

        player.setMap(currentMap);

        enemy = new ArrayList<>(4);
        enemy.add(new Drone(new Vector2D(1.5, 1.5), currentMap, Drone.Type.LIGHT, player));
        enemy.add(new Drone(new Vector2D(6.5, 4.5), currentMap, Drone.Type.MEDIUM, player));
        enemy.add(new Drone(new Vector2D(7.5, 1.5), currentMap, Drone.Type.HEAVY, player));
        enemy.add(new Drone(new Vector2D(9.5, 2.5), currentMap, Drone.Type.BOSS, player));

        obstacles = new ArrayList<>(3);
        obstacles.add(new Obstacle("src/textures/collectibles/heal64.png", new Vector2D(8.5,10.5), Obstacle.Type.HEAL_ITEM, 40));
        obstacles.add(new Obstacle("src/textures/collectibles/ammo64.png", new Vector2D(7.5,10.5), Obstacle.Type.AMMO_PACK, 60));
        obstacles.add(new Obstacle("src/textures/collectibles/key_yellow64.png", new Vector2D(6.5,10.5), Obstacle.Type.KEY, 1));
        obstacles.add(new Obstacle("src/textures/collectibles/score64.png", new Vector2D(5.5,10.5), Obstacle.Type.COLLECTIBLE, 2000));
        obstacles.add(new Obstacle("src/textures/obstacles/ceilingLamp.png", new Vector2D(7.5, 7.5), Obstacle.Type.OBSTACLE, 0));

        currentMap.addEnemies(enemy);
       // mapRender = new MapRender(currentMap);

      //  mapRender.setKeyListener(inputHandler);
        display.setKeyListener(inputHandler);
        player.setKeyListener(inputHandler);

        display.addMap(currentMap);
        display.addDrones(enemy);
        display.addPlayer(player);
        display.addObstacle(obstacles);


        // Create a new thread for display rendering.

        display.start();
        start();
    }

    public void initGame(){
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
                player.update();

                Vector2D temp = player.position;
                for (Obstacle obstacle : obstacles){

                    obstacle.checkCollision(player);
                }

                // Enemy gets updated.
                for (Drone d : enemy) {

                    d.update();
                }

                //mapRender.render(player, enemy);

                delta--;

            }
            //display.render(player);

            // Update the FPS text in the window.
            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
               // mapRender.setTitle(String.format("%s | %d fps", "MapRender", frames));
                frames = 0;
            }
        }
    }
}
