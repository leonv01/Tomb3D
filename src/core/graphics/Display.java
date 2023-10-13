package core.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;


import javax.swing.JFrame;

import core.utils.Config;

public class Display extends JFrame{

    private BufferedImage image;

    public Display(){
        image = new BufferedImage(Config.WIDTH, Config.HEIGHT, BufferedImage.TYPE_INT_RGB);
        setSize(Config.WIDTH, Config.HEIGHT);
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
        g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
        bs.show();
    }
}
