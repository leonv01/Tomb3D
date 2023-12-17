package core.utils;

import core.entities.Drone;
import core.entities.Obstacle;
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
        int[][] data = new int[0][0];
        int[] tempData = new int[0];

        ArrayList<Obstacle> obstacles = new ArrayList<>();
        ArrayList<Drone> enemies = new ArrayList<>();

        BufferedReader reader;
        int lineIdx = 0;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("dimension:")) {
                    String token = line.split(":")[1].trim();
                    int value = Integer.parseInt(token);
                    data = new int[value][value];
                    tempData = new int[value];
                } else {
                    String[] tokens = line.split(",");
                    int idx = 0;
                    Drone enemy = null;
                    Obstacle obstacle = null;

                    double posX, posY;
                    for (String token : tokens) {
                        String tempToken = token.toLowerCase().trim();

                        posX = idx - 0.5;
                        posY = lineIdx - 0.5;
                        switch (tempToken) {
                            case "el" -> {
                                System.out.println("Light Enemy");
                                enemy = new Drone(new Vector2D(lineIdx, idx), null, Drone.Type.LIGHT, null);
                            }
                            case "em" -> {
                                System.out.println("Medium Enemy");
                                enemy = new Drone(new Vector2D(lineIdx, idx), null, Drone.Type.MEDIUM, null);
                            }
                            case "eh" -> {
                                System.out.println("Heavy Enemy");
                                enemy = new Drone(new Vector2D(lineIdx, idx), null, Drone.Type.HEAVY, null);
                            }
                            case "eb" -> {
                                System.out.println("Boss Enemy");
                                enemy = new Drone(new Vector2D(lineIdx, idx), null, Drone.Type.BOSS, null);
                            }
                            case "ok" -> {
                                System.out.println("Key");
                                obstacle = new Obstacle("src/textures/collectibles/key_yellow64.png", new Vector2D(posX, posY), Obstacle.Type.KEY, 0);
                            }
                            case "mp" -> {
                                System.out.println("Medipack");
                                obstacle = new Obstacle("src/textures/collectibles/heal64.png", new Vector2D(posX, posY), Obstacle.Type.HEAL_ITEM, 25);
                            }
                            case "ap" -> {
                                System.out.println("Ammopack");
                                obstacle = new Obstacle("src/textures/collectibles/ammo64.png", new Vector2D(posX, posY), Obstacle.Type.HEAL_ITEM, 30);
                            }
                            case "sp" -> {
                                System.out.println("Score");
                                obstacle = new Obstacle("src/textures/collectibles/score64.png", new Vector2D(posX, posY), Obstacle.Type.HEAL_ITEM, 200);
                            }
                            default -> {
                                if (tempToken.length() > 1 && tempToken.contains("o")) {
                                    System.out.println("Various Obstacles");
                                    obstacle = new Obstacle("src/textures/collectibles/ammo64.png", new Vector2D(posX, posY), Obstacle.Type.OBSTACLE, 0);
                                }
                            }
                        }
                        if (enemy != null) {
                            enemies.add(enemy);
                            tempData[idx] = 0;
                            enemy = null;
                            continue;
                        }
                        if (obstacle != null) {
                            obstacles.add(obstacle);
                            tempData[idx] = obstacle.getType() == Obstacle.Type.OBSTACLE ? -1 : 0;
                            obstacle = null;
                            continue;
                        }
                        int value = Integer.parseInt(token.trim());
                        tempData[idx] = value;
                    }
                    data[lineIdx++] = tempData;
                    System.out.println(line);
                }
            }
            reader.close();
        } catch (NumberFormatException | IOException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return new Map(data);
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
        importMap(new File("src/maps/map1.txt"));
    }
}