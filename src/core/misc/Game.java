package core.misc;

import core.entities.Player;
import core.graphics.Display;
import core.graphics.MapRender;
import core.utils.Vector2D;

public class Game implements Runnable{

    private boolean running;
    private Thread thread;
    private Display display;
    private Player player;
    private Map map;
    private MapRender mapRender;

    public Game(){
        thread = new Thread(this);
        display = new Display();
        map = new Map();
        
        player = new Player(
            new Vector2D(map.map.length / 2, map.map.length / 2)
        );
        mapRender = new MapRender(map, player);

        display.setKeyListener(player.inputHandler);
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
            e.printStackTrace();
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
                delta--;
            }
            display.render();//displays to the screen unrestricted time
            mapRender.render();
        }
    }
}
