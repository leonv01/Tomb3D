package core.misc;

import core.entities.Player;
import core.graphics.Display;
import core.graphics.MapRender;
import core.utils.Vector2D;
import core.entities.Drone;

public class Game implements Runnable{

    private boolean running;
    private final Thread thread;
    private final Display display;
    private final Player player;
    private final Map map;
    private final MapRender mapRender;
    private Drone enemy;

    public Game(){
        thread = new Thread(this);
        display = new Display();
        map = new Map();
        
        player = new Player(
            new Vector2D((double) map.map.length / 2, (double) map.map.length / 2)
        );
        enemy = new Drone(new Vector2D(1.5, 1.5));
        mapRender = new MapRender(map);

        mapRender.setKeyListener(player.inputHandler);
        display.setKeyListener(player.inputHandler);

        RenderingThread renderingThread = new RenderingThread(display, player);
        Thread renderThread = new Thread(renderingThread);
        renderThread.start();

        start();
    }

    public static void main(String[] args) {
        new Game();

    }

    public synchronized void start(){
        running = true;
        thread.start();
    }

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
        final double ns = 1000000000.0 / 60.0;//60 times per second
        double delta = 0;
        while(running) {
            long now = System.nanoTime();
            delta = delta + ((now-lastTime) / ns);
            lastTime = now;
            while (delta >= 1)//Make sure update is only happening 60 times a second
            {
                player.update(map);
                enemy.update(map, player);
                delta--;
            }
            //display.render(player);//displays to the screen unrestricted time
            mapRender.render(player, enemy);
        }
    }
}
