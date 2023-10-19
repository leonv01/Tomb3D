package core.graphics;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import core.entities.Enemy;
import core.entities.Player;
import core.misc.Map;
import core.utils.Config;

public class MapRender extends JFrame{
    Map map;
    Player player;
    Enemy enemy;
    BufferedImage image;

    final int MAP_WIDTH = Config.WIDTH;
    final int MAP_HEIGHT = Config.HEIGHT;

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

    public MapRender(Map map, Player player, Enemy enemy){
        this.map = map;
        this.player = player;
        this.enemy = enemy;
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


    private Color mapColorIndex(int i){
        switch(i){
            case 0:
                return Color.white;
            case 1:
                return Color.black;
            default:
                return Color.red;
        }
    }

    public void render(){
        BufferStrategy bs = getBufferStrategy();
        if(bs == null){
            createBufferStrategy(3);
            return;
        }

        Color color;
        Graphics2D g = (Graphics2D) bs.getDrawGraphics();
        for (int y = 0; y < map.map.length; y++) {
            for (int x = 0; x < map.map[y].length; x++) {

                color = mapColorIndex(map.map[y][x]);
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
        g.fillRect((int)(player.position.x * TILE_X) -  5, (int) (player.position.y * TILE_Y) - 5, 10, 10);

        if(enemy != null){
            g.setColor(Color.orange);
            g.fillRect((int)(enemy.position.x * TILE_X) -  5, (int) (enemy.position.y * TILE_Y) - 5, 10, 10);
            g.setStroke(new BasicStroke(5));
            g.setColor(Color.BLUE);
            g.drawLine(
                (int) (enemy.position.x * TILE_X), (int) (enemy.position.y * TILE_Y), 
                (int) ((enemy.position.x + enemy.direction.x) * TILE_X), (int) ((enemy.position.y + enemy.direction.y) * TILE_Y));

        }

        g.setStroke(new BasicStroke(5));

        // view direction
        g.setColor(Color.BLUE);
        g.drawLine(
            (int) (player.position.x * TILE_X), (int) (player.position.y * TILE_Y), 
            (int) ((player.position.x + player.direction.x) * TILE_X), (int) ((player.position.y + player.direction.y) * TILE_Y));

        g.setColor(Color.RED);
        g.drawLine(
            (int) (player.position.x * TILE_X), (int) (player.position.y * TILE_Y), 
            (int) ((player.ray.x) * TILE_X), (int) ((player.ray.y) * TILE_Y));

        bs.show();
    }
}
