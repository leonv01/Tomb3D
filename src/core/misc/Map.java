package core.misc;

import core.utils.Config;

import java.awt.*;

/**
 * The Map class represents the game map with wall, floor and other tile values.
 */
public class Map {
    public int[][] map;

    public enum WALLS{
        EMPTY,
        WOOD,
        STONE,
        DOOR,
        STONEBRICKS;

        private static final WALLS[] list = WALLS.values();
        public static WALLS getWall(int idx){
            return list[idx];
        }
    };

    /**
     * Constructor for the Map class. Initializes the map and sets configuration values.
     */
    public Map() {
        /*
        Value index:
        0: No wall, empty space
        >0: Walls, Enemies
         */
        map = new int[][]{
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 0, 1},
                {1, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 2, 3, 4, 0, 1},
                {1, 0, 3, 1, 0, 0, 0, 0, 0, 0, 0, 0, 3, 4, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 1},
                {1, 1, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 2, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1},
                {1, 0, 0, 1, 0, 0, 2, 1, 0, 0, 0, 1, 1, 2, 3, 1},
                {1, 0, 0, 2, 0, 0, 1, 1, 0, 0, 0, 0, 2, 2, 3, 1},
                {1, 0, 0, 2, 1, 1, 2, 1, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        };

        // Maybe unused
        Config.CELL_SIZE_X = Config.WIDTH / map[0].length;
        Config.CELL_SIZE_Y = Config.HEIGHT / map.length;

        // Sets the amount of cells for x and y.
        Config.CELL_COUNT_X = map[0].length;
        Config.CELL_COUNT_Y = map.length;
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
}
