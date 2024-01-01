package core.utils;

import core.entities.Drone;
import core.entities.Obstacle;
import core.entities.Player;
import core.misc.HighscoreEntry;
import core.misc.Map;

import java.io.*;
import java.util.ArrayList;

public class FileInterpreter {

    private final static int MAX_HIGHSCORE_ENTRIES = 10;

    /**
     * Imports a map from a file.
     *
     * @param file The file to import the map from.
     * @return The imported map.
     */
    public static Map importMap(File file) {
        BufferedReader reader;
        int[][] mapArrangement = null;
        int lineIndex;
        Map map = null;

        ArrayList<Obstacle> obstacles;
        ArrayList<Drone> enemies;
        Player player = null;

        try{
            obstacles = new ArrayList<>();
            enemies = new ArrayList<>();
            reader = new BufferedReader(new FileReader(file));
            String line;
            lineIndex = 0;
            while((line = reader.readLine()) != null){
                line = line.toLowerCase().trim();
                if(line.contains("dim")){
                    int dimension = Integer.parseInt(line.trim().split(":")[1]);
                    mapArrangement = new int[dimension][dimension];
                }
                else if(mapArrangement != null){
                    String[] lineContent = line.trim().split(",");

                    int columnIndex = 0;
                    for (String character: lineContent) {
                        String temp = character;
                        double objectPositionX = (double) columnIndex + 0.5;
                        double objectPositionY = (double) lineIndex + 0.5;

                        Vector2D position = new Vector2D(objectPositionX, objectPositionY);

                        switch (temp){
                            case " " -> temp = "0";
                            case "x" -> temp = "5";
                            case "k" -> {
                                obstacles.add(new Obstacle(
                                        "src/textures/collectibles/key_yellow64.png",
                                        position,
                                        Obstacle.Type.KEY,
                                        1
                                ));
                                temp = "0";
                            }
                            case "l" -> {
                                enemies.add(new Drone(
                                        position,
                                        Drone.Type.LIGHT
                                ));
                                temp = "0";
                            }
                            case "m" -> {
                                enemies.add(new Drone(
                                        position,
                                        Drone.Type.MEDIUM
                                ));
                                temp = "0";
                            }
                            case "h" -> {
                                enemies.add(new Drone(
                                        position,
                                        Drone.Type.HEAVY
                                ));
                                temp = "0";
                            }
                            case "b" -> {
                                enemies.add(new Drone(
                                        position,
                                        Drone.Type.BOSS
                                ));
                                temp = "0";
                            }
                            case "p" -> {
                                player = new Player(position);
                                temp = "0";
                            }
                            case "+" -> {
                                obstacles.add(new Obstacle(
                                        "src/textures/collectibles/heal64.png",
                                        position,
                                        Obstacle.Type.HEAL_ITEM,
                                        25
                                ));
                                temp = "0";
                            }
                            case "*" -> {
                                obstacles.add(new Obstacle(
                                        "src/textures/collectibles/ammo64.png",
                                        position,
                                        Obstacle.Type.AMMO_PACK,
                                        30
                                ));
                                temp = "0";
                            }
                            case "#" ->{
                                obstacles.add(new Obstacle(
                                        "src/textures/collectibles/score64.png",
                                        position,
                                        Obstacle.Type.COLLECTIBLE ,
                                        200
                                ));
                                temp = "0";
                            }
                            case "o" -> {
                                obstacles.add(new Obstacle(
                                        "src/textures/obstacles/ceilingLamp.png",
                                        position,
                                        Obstacle.Type.OBSTACLE,
                                        0
                                ));
                                temp = "0";
                            }
                        }
                        mapArrangement[lineIndex][columnIndex] = Integer.parseInt(temp);
                        columnIndex++;
                    }
                    lineIndex++;
                }
            }
            map = new Map(
                    mapArrangement,
                    obstacles,
                    enemies,
                    player
            );

        }catch (IOException e){
            System.out.println("Map not found");
        }
        return map;
    }

    /**
     * Exports a map to a file.
     *
     * @param file The file to export the map to.
     * @param data The map data to export.
     */
    public static void exportMap(File file, String[] data) {
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write("dimension: " + data.length);
            for (String line : data) {
                writer.write(line);
                writer.newLine();
            }
            writer.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void exportHighscore(File file, HighscoreEntry entry) {
        BufferedReader reader;
        BufferedWriter writer;

        ArrayList<HighscoreEntry> highscoreEntries = new ArrayList<>(MAX_HIGHSCORE_ENTRIES);

        int i = 0;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;

            while ((line = reader.readLine()) != null) {
                String splitEntry;
                if ((splitEntry = line.split("Entry")[1]) != null && i++ < MAX_HIGHSCORE_ENTRIES) {
                    String[] tempEntry = splitEntry.split("\0");
                    int scoreEntry;
                    try {
                        scoreEntry = Integer.parseInt(tempEntry[1]);
                    } catch (NumberFormatException e) {
                        scoreEntry = 0;
                    }
                    highscoreEntries.add(new HighscoreEntry(tempEntry[0], scoreEntry));
                }
            }
            reader.close();

            int idx = 0;
            for (HighscoreEntry e : highscoreEntries) {
                if (e.getScore() < entry.getScore()) {
                    highscoreEntries.add(idx, entry);
                }
                idx++;
            }

            writer = new BufferedWriter(new FileWriter(file));
            writer.write("---\tHighscore\t---");
            writer.write("Name\tScore");

            idx = 0;
            for (HighscoreEntry e : highscoreEntries) {
                if (idx++ >= MAX_HIGHSCORE_ENTRIES) break;
                writer.write(e.toString());
            }

            writer.close();

        } catch (IOException ignored) {
        }
    }

    public static void main(String[] args) {
        Map map = importMap(new File("src/maps/level1.txt"));
        map.getEnemies().forEach(drone -> System.out.println(drone.getPlayer()));
        Obstacle obstacle = map.getObstacles().get(2);
        System.out.println(obstacle.getPosition().x);
    }
}