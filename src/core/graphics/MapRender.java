package core.graphics;


import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.*;

import core.entities.Drone;
import core.entities.Player;
import core.misc.Map;
import core.utils.Config;
import core.utils.Ray;
import core.utils.Vector2D;

/**
 * Represents the map renderer for the top-down perspective of the game.
 */
public class MapRender extends JPanel {
    Map map;
    Player player;
    ArrayList<Drone> enemies;
    BufferedImage image;

    final int MAP_WIDTH = Config.MAP_WIDTH;
    final int MAP_HEIGHT = Config.MAP_HEIGHT;

    final int TILE_X;
    final int TILE_Y;

    /**
     * Constructs a new map renderer with a map object.
     *
     * @param map The map that will be rendered.
     */
    public MapRender(Map map, Player player, ArrayList<Drone> enemies){
        this.map = map;
        this.player = player;
        this.enemies = enemies;
        image = new BufferedImage(MAP_WIDTH, MAP_HEIGHT, BufferedImage.TYPE_INT_RGB);

        TILE_X = MAP_WIDTH / map.map[0].length;
        TILE_Y = MAP_HEIGHT / map.map.length;

        setPreferredSize(new Dimension(MAP_WIDTH, MAP_HEIGHT));
    }

    /**
     * Renders the map with player and enemy from a top-down perspective.
     *
     * @param player The player object that will be rendered.
     * @param enemies The enemy object that will be rendered
     */
    public void render(Player player, ArrayList<Drone> enemies){
        repaint();
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        Color color;

        // For each value in the map, a tile will be rendered.
        for (int y = 0; y < map.map.length; y++) {
            for (int x = 0; x < map.map[y].length; x++) {

                color = map.getColor(x,y);
                g2d.setColor(color);
                g2d.fillPolygon(
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
        g2d.setColor(Color.red);
        g2d.fillRect((int)(player.getPosition().x * TILE_X) -  5, (int) (player.getPosition().y * TILE_Y) - 5, 10, 10);
        g2d.setStroke(new BasicStroke(5));

        // Render the rays of the player.
        for (Ray ray : player.getRays()) {
            g.setColor(Color.ORANGE.darker());
            g.drawLine(
            (int) (player.getX() * TILE_X), (int) (player.getY() * TILE_Y),
            (int) ((ray.getX()) * TILE_X), (int) ((ray.getY()) * TILE_Y));
        }

        // Render the directional vector where player is looking at.
        g.setColor(Color.BLUE);
        g.drawLine(
                (int) (player.getPosition().x * TILE_X), (int) (player.getPosition().y * TILE_Y),
                (int) ((player.getPosition().x + player.getDirection().x) * TILE_X), (int) ((player.getPosition().y + player.getDirection().y) * TILE_Y));


        // If an enemy exists, it should be rendered as a square with a width of 5 and its directional vector.
        if(enemies != null){
            for (Drone enemy : enemies) {
                g.setColor(Color.orange);
                g.fillRect((int)(enemy.getPosition().x * TILE_X) -  5, (int) (enemy.getPosition().y * TILE_Y) - 5, 10, 10);
                g.setColor(Color.BLUE);
                g.drawLine(
                        (int) (enemy.getPosition().x * TILE_X), (int) (enemy.getPosition().y * TILE_Y),
                        (int) ((enemy.getPosition().x + enemy.getDirection().x) * TILE_X), (int) ((enemy.getPosition().y + enemy.getDirection().y) * TILE_Y));
            }
        }
    }
}
