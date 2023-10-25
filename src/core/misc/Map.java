package core.misc;

import core.utils.Config;

public class Map {
    public int[][] map;

    public Map(){
        map = new int[][]{
            {1,1,1,1,1,1,1,1},
            {1,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,1},
            {1,2,2,0,0,2,2,1},
            {1,1,1,1,1,1,1,1}
        };

        Config.CELL_SIZE_X = Config.WIDTH / map[0].length;
        Config.CELL_SIZE_Y = Config.HEIGHT / map.length;

        Config.CELL_COUNT_X = map[0].length;
        Config.CELL_COUNT_Y = map.length;
    }

    public int getValue(int x, int y){
        return map[y][x];
    }

    public boolean inBounds(int x, int y){
        return (
            x >= 0 && x < map[0].length &&
            y >= 0 && y < map.length
        );
    }
}
