package core.misc;

import core.entities.Player;
import core.graphics.Display;

public class RenderingThread implements Runnable {
    private Display display;
    private Player player;

    public RenderingThread(Display display, Player player){
        this.display = display;
        this.player = player;
    }


    @Override
    public void run() {
        while(true){
            display.render(player);
        }
    }
}
