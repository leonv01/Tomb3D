package core.graphics;

import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import core.entities.Player;
import core.utils.Config;
import core.utils.Vector2D;

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

    public void render(Player player){
        BufferStrategy bs = getBufferStrategy();
        if(bs == null){
            createBufferStrategy(3);
            return;
        }
        Graphics2D g = (Graphics2D) bs.getDrawGraphics();
        
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, DIS_WIDTH, DIS_HEIGHT / 2);

        g.setColor(Color.lightGray);
        g.fillRect(0, DIS_HEIGHT / 2, DIS_WIDTH, DIS_HEIGHT);

        g.setColor(Color.BLUE);
        double[] rayLength = player.raysLength;
        for (int i = 0; i < rayLength.length; i++) {

            double wallHeight = (double)DIS_HEIGHT / rayLength[i];

            if(wallHeight > DIS_HEIGHT) wallHeight = DIS_HEIGHT;

            double lineOffset = (DIS_HEIGHT / 2.0);

            int xOffset = DIS_WIDTH / rayLength.length;

            int temp = (rayLength.length - 1) - i;

            g.setStroke(new BasicStroke(xOffset));
            g.drawLine(temp * xOffset, (int)(lineOffset + wallHeight), temp * xOffset, (int)(lineOffset - wallHeight));
        }
         
        bs.show();
    }
}
