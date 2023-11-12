package core.misc;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * The InputHandler class represents the input handler in the game.
 */
public class InputHandler implements KeyListener {

    public boolean forward;
    public boolean back;
    public boolean left;
    public boolean right;
    public boolean shoot;
    public boolean map;
    public boolean run;
    public boolean use;
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Keyboard Input for forward movement
        if (e.getKeyCode() == KeyEvent.VK_W ||
                e.getKeyCode() == KeyEvent.VK_UP) {
            forward = true;
        }
        // Keyboard Input for backwards movement
        if (e.getKeyCode() == KeyEvent.VK_S ||
                e.getKeyCode() == KeyEvent.VK_DOWN) {
            back = true;
        }
        // Keyboard Input for left rotation
        if (e.getKeyCode() == KeyEvent.VK_A ||
                e.getKeyCode() == KeyEvent.VK_LEFT) {
            left = true;
        }
        // Keyboard Input for right rotation
        if (e.getKeyCode() == KeyEvent.VK_D ||
                e.getKeyCode() == KeyEvent.VK_RIGHT) {
            right = true;
        }
        // Keyboard Input for map
        if(e.getKeyCode() == KeyEvent.VK_M){
            map = true;
        }
        // Keyboard Input for running
        if(e.getKeyCode() == KeyEvent.VK_SHIFT){
            run = true;
        }
        // Keyboard Input for escaping the game
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.exit(69);
        }
        // Keyboard Input for using item/door
        if(e.getKeyCode() == KeyEvent.VK_E) {
            use = true;
        }
        if(e.getKeyCode() == KeyEvent.VK_SPACE){
            shoot = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Keyboard Input for forward movement 
       if(
        e.getKeyCode() == KeyEvent.VK_W ||
        e.getKeyCode() == KeyEvent.VK_UP
       ){
        forward = false;
       }
       // Keyboard Input for backwards movement 
       if(
        e.getKeyCode() == KeyEvent.VK_S ||
        e.getKeyCode() == KeyEvent.VK_DOWN
       ){
        back = false;
       }
       // Keyboard Input for left rotation 
       if(
        e.getKeyCode() == KeyEvent.VK_A ||
        e.getKeyCode() == KeyEvent.VK_LEFT
       ){
        left = false;
       }
       // Keyboard Input for right rotation 
       if(
        e.getKeyCode() == KeyEvent.VK_D ||
        e.getKeyCode() == KeyEvent.VK_RIGHT
       ){
        right = false;
       }
       // Keyboard Input for opening the map
       if(e.getKeyCode() == KeyEvent.VK_M){
            map = false;
       }
       // Keyboard Input for running
       if(e.getKeyCode() == KeyEvent.VK_SHIFT){
            run = false;
       }
       // Keyboard Input for using item/door
       if(e.getKeyCode() == KeyEvent.VK_E){
           use = false;
       }
       if(e.getKeyCode() == KeyEvent.VK_SPACE){
           shoot = false;
       }
    }
}
