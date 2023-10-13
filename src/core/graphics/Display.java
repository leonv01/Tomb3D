package core.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import core.utils.Config;

public class Display extends JFrame{

    BufferedImage image;
    private final int DIS_HEIGHT = Config.HEIGHT;
    private final int DIS_WIDTH = Config.WIDTH;

    public Display(){
        image = new BufferedImage(DIS_WIDTH, DIS_HEIGHT, BufferedImage.TYPE_INT_RGB);
        setSize(DIS_WIDTH, DIS_HEIGHT);
        setResizable(false);
        setTitle("Tomb3D");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(Color.BLACK);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void setKeyListener(KeyListener k){
        addKeyListener(k);
    }

    public void render(){
        BufferStrategy bs = getBufferStrategy();
        if(bs == null){
            createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();
        
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, DIS_WIDTH, DIS_HEIGHT / 2);

        g.setColor(Color.lightGray);
        g.fillRect(0, DIS_HEIGHT / 2, DIS_WIDTH, DIS_HEIGHT);
         
        bs.show();
    }
}
