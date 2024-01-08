package core.misc;

import core.entities.EntityAttributes;
import core.entities.Obstacle;
import core.entities.Player;
import core.graphics.Display;
import core.utils.FileInterpreter;
import core.utils.TimeCounter;
import core.utils.Vector2D;
import core.entities.Drone;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;

/**
 * The Game class represents the main game engine that manages the game loop and components.
 */
public class Game implements Runnable{

    // flag for thread.
    private boolean running;
    private boolean paused;
    private boolean gameEnd;

    // Thread for the game engine.
    private final Thread thread;

    // Display object to render the game.
    private final Display display;

    // Player object.
    private Player player;


    private Map currentMap;

    // Map object.
    private final ArrayList<Map> maps;
    private int mapIndex;
    private final ArrayList<Drone> enemies;
    private final ArrayList<Obstacle> obstacles;
    private final InputHandler inputHandler;

    private MainMenu mainMenu;
    private Timer gameTimer;
    private TimeCounter timeCounter;
    /**
     * Constructor for the Game class. Initializes game components and starts the game loop.
     */
    public Game(){

        paused = false;

        timeCounter = new TimeCounter();
        gameTimer = new Timer(1000, e -> timeCounter.update());

        mainMenu = MainMenu.getInstance();
        mainMenu.setVisibility(false);

        gameEnd = false;
        thread = new Thread(this);
        display = new Display();
        inputHandler = new InputHandler();

        mapIndex = 0;

        this.player = new Player();

        maps = FileInterpreter.loadMapCollection();


        this.enemies = new ArrayList<>();
        this.obstacles = new ArrayList<>();

        changeMap();

        display.setKeyListener(inputHandler);
        player.setKeyListener(inputHandler);

        // Create a new thread for display rendering.

        display.start();
        start();
    }

    public void changeMap(){
        enemies.forEach(Drone::stopTimer);
        gameEnd = mapIndex == maps.size();
        if(gameEnd) {
            saveHighsore();
            display.setGameEnd(true);
            return;
        }
        Map map = this.maps.get(mapIndex++);
        EntityAttributes playerAttributes = this.player.getAttributes();
        playerAttributes.setKey(false);
        this.player = map.getPlayer();
        this.player.setAttributes(playerAttributes);
        this.player.setMap(map);
        this.player.setKeyListener(inputHandler);
        this.enemies.clear();
        this.enemies.addAll(map.getEnemies());
        this.obstacles.clear();
        this.obstacles.addAll(map.getObstacles());
        this.currentMap = map;
        this.display.addMap(map);

        currentMap.setConfig();
    }

    private void saveHighsore() {
        gameTimer.stop();
        FileInterpreter.exportHighscore(new File("src/highscore/highscore.txt"), player.getHighscore(), timeCounter.getTime());
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
            System.out.println("Joined");
        }catch (InterruptedException e){
            System.out.println("threads couldn't join");
        }
    }

    @Override
    public void run() {
        boolean endGame = false;

        long lastTime = System.nanoTime();
        long timer = System.currentTimeMillis();
        final double ns = 1000000000.0 / 60.0;//60 times per second
        double delta = 0;
        int frames = 0;

        gameTimer.start();

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

                Vector2D temp = player.getPosition();
                for (Obstacle obstacle : obstacles){

                    obstacle.checkCollision(player);
                }

                // Enemy gets updated.
                for (Drone d : enemies) {

                    d.update();
                }

                if(player.getActivatedGoal()){
                    changeMap();
                }

                endGame = !player.isAlive() || gameEnd;

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



            if(endGame)
                break;
        }
        System.out.println("END");
        try {
            Thread.sleep(gameEnd ? 5000 : 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        mainMenu.setVisibility(true);
        display.setVisible(false);
    }

    public void setPaused(boolean b) {
        paused = b;
    }

    public boolean getPaused() {
        return paused;
    }
}
