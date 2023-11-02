package core.graphics;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import core.entities.Drone;
import core.entities.Player;
import core.misc.Map;
import core.utils.Config;
import core.utils.Ray;
import core.utils.Vector2D;

/**
 * Represents the map renderer for the top-down perspective of the game.
 */
public class MapRender extends JFrame{
    Map map;
    BufferedImage image;

    final int MAP_WIDTH = Config.WIDTH;
    final int MAP_HEIGHT = Config.HEIGHT;

    final int TILE_X;
    final int TILE_Y;

    /**
     * Constructs a new map renderer with a map object.
     *
     * @param map The map that will be rendered.
     */
    public MapRender(Map map){
        this.map = map;
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
    }

    /**
     * Sets the KeyListener for this window.
     *
     * @param k The KeyListener for input detection.
     */
    public void setKeyListener(KeyListener k){
        addKeyListener(k);
    }

    /**
     * Renders the map with player and enemy from a top-down perspective.
     *
     * @param player The player object that will be rendered.
     * @param enemy The enemy object that will be rendered
     */
    public void render(Player player, Drone enemy){
        BufferStrategy bs = getBufferStrategy();
        if(bs == null){
            createBufferStrategy(3);
            return;
        }

        Color color;
        Graphics2D g = (Graphics2D) bs.getDrawGraphics();

        // For each value in the map, a tile will be rendered.
        for (int y = 0; y < map.map.length; y++) {
            for (int x = 0; x < map.map[y].length; x++) {

                color = map.getColor(x,y);
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

        // Render the player as a square with a width of 5.
        g.setColor(Color.red);
        g.fillRect((int)(player.position.x * TILE_X) -  5, (int) (player.position.y * TILE_Y) - 5, 10, 10);
        g.setStroke(new BasicStroke(5));

        // Render the directional vector where player is looking at.
        g.setColor(Color.BLUE);
        g.drawLine(
            (int) (player.position.x * TILE_X), (int) (player.position.y * TILE_Y),
            (int) ((player.position.x + player.direction.x) * TILE_X), (int) ((player.position.y + player.direction.y) * TILE_Y));

        // Render the rays of the player.
        for (Ray ray : player.rays) {
            g.setColor(ray.getColor());
            g.drawLine(
            (int) (player.getX() * TILE_X), (int) (player.getY() * TILE_Y),
            (int) ((ray.getX()) * TILE_X), (int) ((ray.getY()) * TILE_Y));
        }

        // If an enemy exists, it should be rendered as a square with a width of 5 and its directional vector.
        if(enemy != null){
            g.setColor(Color.orange);
            g.fillRect((int)(enemy.position.x * TILE_X) -  5, (int) (enemy.position.y * TILE_Y) - 5, 10, 10);
            g.setColor(Color.BLUE);
            g.drawLine(
                    (int) (enemy.position.x * TILE_X), (int) (enemy.position.y * TILE_Y),
                    (int) ((enemy.position.x + enemy.direction.x) * TILE_X), (int) ((enemy.position.y + enemy.direction.y) * TILE_Y));

        }
        bs.show();

    }
}
