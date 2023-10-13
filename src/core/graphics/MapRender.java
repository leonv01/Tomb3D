package core.graphics;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;


import javax.swing.JFrame;

import core.entities.Player;
import core.misc.Map;
import core.utils.Config;

public class MapRender extends JFrame{
    Map map;
    Player player;
    BufferedImage image;

    final int MAP_WIDTH = Config.WIDTH / 2;
    final int MAP_HEIGHT = Config.HEIGHT / 2;

    final int TILE_X;
    final int TILE_Y;

    public MapRender(Map map, Player player){
        this.map = map;
        this.player = player;
        image = new BufferedImage(MAP_WIDTH, MAP_HEIGHT, BufferedImage.TYPE_INT_RGB);

        TILE_X = MAP_WIDTH / map.map[0].length;
        TILE_Y = MAP_HEIGHT / map.map.length;

        setSize(MAP_WIDTH, MAP_HEIGHT);
        setResizable(false);
        setTitle("Map Renderer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(Color.BLACK);
        setLocationRelativeTo(null);
        setVisible(true);
        addKeyListener(player.inputHandler);
    }

    public void render(){
        BufferStrategy bs = getBufferStrategy();
        if(bs == null){
            createBufferStrategy(3);
            return;
        }

        Color color;
        Graphics g = bs.getDrawGraphics();
        for (int y = 0; y < map.map.length - 1; y++) {
            for (int x = 0; x < map.map[y].length - 1; x++) {
                if(map.map[y][x] == 0)
                    color = Color.WHITE;
                else 
                    color = Color.BLACK;
                g.setColor(color);
                g.fillPolygon(
                    new int[]{
                        x * TILE_X, (x + 1) * TILE_X, (x + 1) * TILE_X, x * TILE_X
                    }, 
                    new int[]{
                        y * TILE_Y, y * TILE_Y, (y + 1) * TILE_Y, (y + 1) * TILE_Y
                    }, 
                    4);
            }
        }

        g.setColor(Color.red);
        g.drawRect((int)(player.position.x * TILE_X) -  5, (int) (player.position.y * TILE_Y) - 5, 10, 10);
        bs.show();
    }
}
