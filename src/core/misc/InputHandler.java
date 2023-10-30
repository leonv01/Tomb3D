package core.misc;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class InputHandler implements KeyListener {

    public boolean forward;
    public boolean back;
    public boolean left;
    public boolean right;
    public boolean shoot;
    public boolean map;
    public boolean run;

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
        if(e.getKeyCode() == KeyEvent.VK_SHIFT){
            run = true;
        }
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
            System.exit(69);
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
       if(e.getKeyCode() == KeyEvent.VK_M){
            map = false;
       }
       if(e.getKeyCode() == KeyEvent.VK_SHIFT){
            run = false;
       }
    }
}
