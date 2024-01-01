package core.misc;

import core.entities.Drone;
import core.entities.Obstacle;
import core.entities.Player;
import core.utils.Config;
import core.utils.Vector2D;

import java.awt.*;
import java.util.ArrayList;

/**
 * The Map class represents the game map with wall, floor and other tile values.
 */
public class Map {
    public int[][] map;

    private Player player;
    private ArrayList<Drone> enemies;
    private ArrayList<Obstacle> obstacles;

    public Player getPlayer() {
        return player;
    }

    public enum WALLS{
        EMPTY,
        WOOD,
        STONE,
        DOOR,
        STONEBRICKS,
        GOAL
        ;

        private static final WALLS[] list = WALLS.values();
        public static WALLS getWall(int idx){
            return list[idx];
        }
    };

    public Map(int[][] map, ArrayList<Obstacle> obstacles, ArrayList<Drone>enemies, Player player){
        initMap(map);
        this.player = player;
        this.obstacles = obstacles;
        this.enemies = new ArrayList<>();
        enemies.forEach(enemy -> this.enemies.add(new Drone(enemy.getPosition(), this, enemy.getType(), player)));
    }

    /**
     * Constructor for the Map class. Initializes the map and sets configuration values.
     */
    public Map() {

        initMap(new int[][]
                {
                        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 4, 4, 0, 1},
                        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 4, 4, 0, 1},
                        {1, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 2, 4, 4, 0, 1},
                        {1, 0, 3, 1, 1, 2, 0, 0, 0, 0, 0, 0, 3, 4, 0, 1},
                        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 1},
                        {1, 1, 4, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                        {1, 0, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                        {1, 2, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                        {1, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1},
                        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1},
                        {1, 0, 0, 1, 0, 0, 2, 1, 0, 0, 0, 1, 1, 2, 3, 1},
                        {1, 0, 0, 2, 0, 0, 1, 1, 0, 0, 0, 0, 2, 2, 3, 1},
                        {1, 0, 0, 2, 1, 1, 2, 1, 0, 0, 0, 0, 0, 0, 0, 1},
                        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                }
                );
        player = new Player(
                new Vector2D((double) map.length / 2, (double) map.length / 2)
        );
        enemies = new ArrayList<>(4);
        enemies.add(new Drone(new Vector2D(1.5, 1.5), this, Drone.Type.LIGHT, player));
        enemies.add(new Drone(new Vector2D(6.5, 4.5), this, Drone.Type.MEDIUM, player));
        enemies.add(new Drone(new Vector2D(7.5, 1.5), this, Drone.Type.HEAVY, player));
        enemies.add(new Drone(new Vector2D(9.5, 2.5), this, Drone.Type.BOSS, player));

        obstacles = new ArrayList<>(3);
        obstacles.add(new Obstacle("src/textures/collectibles/heal64.png", new Vector2D(8.5,10.5), Obstacle.Type.HEAL_ITEM, 40));
        obstacles.add(new Obstacle("src/textures/collectibles/ammo64.png", new Vector2D(7.5,10.5), Obstacle.Type.AMMO_PACK, 60));
        obstacles.add(new Obstacle("src/textures/collectibles/key_yellow64.png", new Vector2D(6.5,10.5), Obstacle.Type.KEY, 1));
        obstacles.add(new Obstacle("src/textures/collectibles/score64.png", new Vector2D(5.5,10.5), Obstacle.Type.COLLECTIBLE, 2000));
        obstacles.add(new Obstacle("src/textures/obstacles/ceilingLamp.png", new Vector2D(7.5, 7.5), Obstacle.Type.OBSTACLE, 0));
    }

    public Map(int[][] map){
        initMap(map);
    }

    public void initMap(int[][] map){
        this.map = map;

        enemies = new ArrayList<>();
        obstacles = new ArrayList<>();

        // Maybe unused
        Config.CELL_SIZE_X = Config.WIDTH / map[0].length;
        Config.CELL_SIZE_Y = Config.HEIGHT / map.length;

        // Sets the amount of cells for x and y.
        Config.CELL_COUNT_X = map[0].length;
        Config.CELL_COUNT_Y = map.length;

        Config.CELL_SCREEN_WIDTH = Config.WIDTH / Config.CELL_COUNT_X;
        Config.CELL_SCREEN_HEIGHT = Config.HEIGHT / Config.CELL_COUNT_Y;
    }

    /**
     * Get the value of a specific cell in the map.
     *
     * @param x The x-index of the cell.
     * @param y The y-index of the cell.
     * @return The value of the cell at the specified indexes.
     */
    public int getValue(int x, int y) {
        return map[y][x];
    }

    /**
     * Get the current wall ID by the map index.
     *
     * @param x The x-index of the cell.
     * @param y The y-index of the cell.
     * @return The enum item/wall ID of the x and y index.
     */
    public WALLS getWall(int x, int y){
        return WALLS.getWall(getValue(x,y));
    }
    /**
     * Check if the specified indexes are within the bounds of the map.
     *
     * @param x The x-index to check.
     * @param y The y-index to check.
     * @return 'true' if indexes are within map bounds, 'false' if not.
     */
    public boolean inBounds(int x, int y) {
        return (
                x >= 0 && x < map[0].length &&
                        y >= 0 && y < map.length
        );
    }

    /**
     * Sets the value at the x and y indexes.
     *
     * @param x The x-index of the cell.
     * @param y The y-index of the cell.
     * @param value The value to be set in the map array.
     */
    public void setValue(int x, int y, int value){ map[y][x] = value; }

    /**
     * Sets the value at the x and y indexes.
     *
     * @param x The x-index of the cell.
     * @param y The y-index of the cell.
     * @param walls The value to be set in the map array.
     */
    public void setValue(int x, int y, WALLS walls){ map[y][x] = walls.ordinal(); }

    /**
     * Get the color associated with a specific cell in the map for rendering.
     *
     * @param x The x-index of the cell.
     * @param y The y-index of the cell.
     * @return The color to render for the specified cell based on its value.
     */
    public Color getColor(int x, int y){
        switch(map[y][x]){
            case 1 ->{
                return Color.DARK_GRAY;
            }
            case 2 ->{
                return Color.RED.darker();
            }
            case 3 ->{
                return Color.ORANGE.darker();
            }
            case 4 ->{
                return Color.GREEN.darker();
            }
            default -> {
                return Color.GRAY;
            }
        }
    }

    public void addEnemy(Drone drone){ enemies.add(drone); }
    public void addEnemies(ArrayList<Drone> enemies){
        this.enemies.addAll(enemies);
    }
    public ArrayList<Drone> getEnemies(){ return enemies; }

    public void addObstacle(Obstacle obstacle){ obstacles.add(obstacle); }
    public ArrayList<Obstacle> getObstacles(){ return obstacles; }


    int[][] bigMap ={
                    {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 0, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 0, 1},
                    {1, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 2, 3, 4, 0, 1, 1, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 2, 3, 4, 0, 1},
                    {1, 0, 3, 1, 1, 2, 0, 0, 0, 0, 0, 0, 3, 4, 0, 1, 1, 0, 3, 1, 1, 2, 0, 0, 0, 0, 0, 0, 3, 4, 0, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 1},
                    {1, 1, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                    {1, 0, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                    {1, 2, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 3, 3, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                    {1, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1},
                    {1, 0, 0, 1, 0, 0, 2, 1, 0, 0, 0, 1, 1, 2, 3, 1, 1, 0, 0, 1, 0, 0, 2, 1, 0, 0, 0, 1, 1, 2, 3, 1},
                    {1, 0, 0, 2, 0, 0, 1, 1, 0, 0, 0, 0, 2, 2, 3, 1, 1, 0, 0, 2, 0, 0, 1, 1, 0, 0, 0, 0, 2, 2, 3, 1},
                    {1, 0, 0, 2, 1, 1, 2, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 2, 1, 1, 2, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                    {1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 1, 1, 1, 3, 1, 0, 0, 1, 1, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1},
                    {1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 1, 1, 1, 3, 1, 0, 0, 1, 1, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 0, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 0, 1},
                    {1, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 2, 3, 4, 0, 3, 3, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 2, 3, 4, 0, 1},
                    {1, 0, 3, 1, 1, 2, 0, 0, 0, 0, 0, 0, 3, 4, 0, 3, 3, 0, 3, 1, 1, 2, 0, 0, 0, 0, 0, 0, 3, 4, 0, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 1},
                    {1, 1, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                    {1, 0, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                    {1, 2, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 2, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                    {1, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1},
                    {1, 0, 0, 1, 0, 0, 2, 1, 0, 0, 0, 1, 1, 2, 3, 1, 1, 0, 0, 1, 0, 0, 2, 1, 0, 0, 0, 1, 1, 2, 3, 1},
                    {1, 0, 0, 2, 0, 0, 1, 1, 0, 0, 0, 0, 2, 2, 3, 1, 1, 0, 0, 2, 0, 0, 1, 1, 0, 0, 0, 0, 2, 2, 3, 1},
                    {1, 0, 0, 2, 1, 1, 2, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 2, 1, 1, 2, 1, 0, 0, 0, 0, 0, 0, 0, 1},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
            };


}
