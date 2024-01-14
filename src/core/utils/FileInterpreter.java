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

    public static ArrayList<Map> loadMapCollection(){

        String path = "src/maps/";

        File directory = new File(path);
        File[] files = directory.listFiles();

        assert files != null;
        ArrayList<Map> maps = new ArrayList<>(files.length);

        for(File file : files)
            maps.add(importMap(file));

        return maps;
    }

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

        int healthFactor = 25;
        int scoreFactor = 200;

        Map map = null;

        ArrayList<Obstacle> obstacles;
        ArrayList<Drone> enemies;
        Player player = null;

        try {
            obstacles = new ArrayList<>();
            enemies = new ArrayList<>();
            reader = new BufferedReader(new FileReader(file));
            String line;
            lineIndex = 0;
            while ((line = reader.readLine()) != null) {
                line = line.toLowerCase().trim();
                if (line.contains("dim")) {
                    int dimension = Integer.parseInt(line.trim().split(":")[1]);
                    mapArrangement = new int[dimension][dimension];
                } else if (line.contains("health")) {
                    healthFactor = Integer.parseInt(line.trim().split(":")[1]);
                } else if (line.contains("score")) {
                    scoreFactor = Integer.parseInt(line.trim().split(":")[1]);
                }
                else if (mapArrangement != null) {
                    String[] lineContent = line.trim().split(",");

                    int columnIndex = 0;
                    for (String character : lineContent) {
                        String temp = character;
                        double objectPositionX = (double) columnIndex + 0.5;
                        double objectPositionY = (double) lineIndex + 0.5;

                        Vector2D position = new Vector2D(objectPositionX, objectPositionY);

                        switch (temp) {
                            case " " -> temp = "0";
                            case "x" -> temp = "5";
                            case "d" -> temp = "3";
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
                                        healthFactor
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
                            case "#" -> {
                                obstacles.add(new Obstacle(
                                        Math.random() < 0.3 ? "src/textures/collectibles/score64_2.png" :
                                        "src/textures/collectibles/score64.png",
                                        position,
                                        Obstacle.Type.COLLECTIBLE,
                                        scoreFactor
                                ));
                                temp = "0";
                            }
                            case "o" -> {
                                obstacles.add(new Obstacle(
                                        Math.random() < 0.5 ? "src/textures/obstacles/ceilingLamp.png" :
                                                "src/textures/obstacles/rock64.png",
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

        } catch (IOException e) {
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

    public static void exportHighscore(File file, HighscoreEntry entry, String time) {
        System.out.println("Exporting highscore");
        BufferedReader reader;
        BufferedWriter writer;

        ArrayList<HighscoreEntry> highscoreEntries = new ArrayList<>(MAX_HIGHSCORE_ENTRIES);
        highscoreEntries.add(new HighscoreEntry(entry.getName(), entry.getScore(), time));

        int i = 0;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;

            while ((line = reader.readLine()) != null) {
                if(line.contains("Name:") && line.contains("Score:")){
                    String[] nameString = line.split("Name:");
                    String name = nameString[1].trim();

                    String[] scoreString = line.split("Score:");
                    int score = Integer.parseInt(scoreString[1].split("\\|")[0].trim());

                    String[] timeString = line.split("Time:");
                    String timeVal = timeString[1].trim();

                    highscoreEntries.add(new HighscoreEntry(name, score, timeVal));
                }
            }
            reader.close();
        } catch (IOException ignored) {
        }
        finally {
            try{
                writer = new BufferedWriter(new FileWriter(file));
                writer.write("-------Highscore-------\n");
                highscoreEntries.sort((o1, o2) -> o2.getScore() - o1.getScore());
                highscoreEntries.forEach(highscoreEntry -> {
                    try {
                        writer.write("-------------------------------------------------------------------------------------------------------------\n");
                        writer.write(String.format("Name: %s\t|\tScore: %d\t|\tTime: %s\n", highscoreEntry.getName(), highscoreEntry.getScore(), highscoreEntry.getTimeVal()));
                        writer.write("-------------------------------------------------------------------------------------------------------------\n");
                    } catch (IOException ignored) {
                    }
                });
                writer.close();
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    public static String loadHighscore(File file) {
        StringBuilder highscore = new StringBuilder();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                highscore.append(line).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return highscore.toString();
    }

    public static void clearHighscore(File file) {
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write("-------Highscore-------\n");
            writer.close();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }

    }

    public static void main(String[] args) {
        FileInterpreter.exportHighscore(new File("src/highscore/highscore.txt"), new HighscoreEntry("Test", 100), "");
    }


}