package core.misc;

import core.entities.Player;
import core.graphics.Display;

/**
 * Represents a Thread that handles the rendering.
 */
public class RenderingThread implements Runnable {
    private final Display display;
    private final Player player;

    /**
     * Constructs a new rendering thread.
     *
     * @param display The display to render on.
     * @param player The player object whose view will be rendered.
     */
    public RenderingThread(Display display, Player player){
        this.display = display;
        this.player = player;
    }

    @Override
    public void run() {
        while(true){
            display.render();
        }
    }
}
