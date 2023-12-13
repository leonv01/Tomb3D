package core.misc;

import core.utils.Config;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class MainMenu extends JFrame {

    private final int BUTTON_WIDTH = 100;
    private final int BUTTON_HEIGHT = 50;

    public MainMenu() {

        JPanel panel = new JPanel();
        JButton startButton = new JButton("Start");
        JButton exitButton = new JButton("Exit");
        JButton settingsButton = new JButton("Settings");
        JButton creditsButton = new JButton("Credits");
        JButton highscoreButton = new JButton("Highscore");
        JButton mapEditorButton = new JButton("Map Editor");

        startButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        exitButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        settingsButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        creditsButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        highscoreButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        mapEditorButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));

        GridLayout layout = new GridLayout(6, 1, 10, 10);

        startButton.addActionListener(e -> startGame());
        exitButton.addActionListener(e -> exitGame());
        settingsButton.addActionListener(e -> openSettings());
        creditsButton.addActionListener(e -> openCredits());
        highscoreButton.addActionListener(e -> openHighscore());
        mapEditorButton.addActionListener(e -> openMapEditor());

        panel.setLayout(layout);

        panel.add(startButton);
        panel.add(exitButton);
        panel.add(settingsButton);
        panel.add(creditsButton);
        panel.add(highscoreButton);
        panel.add(mapEditorButton);

        this.add(panel);
        this.setSize(500, 500);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    private void openMapEditor() {
    }

    private void openHighscore() {
    }

    private void openCredits() {
    }

    private void openSettings() {
        GridLayout layout = new GridLayout(5, 1, 10, 10);

        JFrame settingsFrame = new JFrame();
        JPanel settingsPanel = new JPanel();
        JButton backButton = new JButton("Back");
        JButton applyButton = new JButton("Apply");

        TextField nameField = new TextField("Enter your name here");
        TextField fovField = new TextField("Enter your fov here");
        TextField resolutionField = new TextField("Enter your resolution here");

        nameField.addActionListener(e -> System.out.println("TEST"));
        fovField.addActionListener(e -> fovField.setText(""));
        resolutionField.addActionListener(e -> resolutionField.setText(""));

        backButton.addActionListener(e -> settingsFrame.setVisible(false));
        applyButton.addActionListener(e -> applySettings(nameField.getText(), fovField.getText(), resolutionField.getText()));

        settingsPanel.setLayout(layout);

        settingsPanel.add(nameField);
        settingsPanel.add(fovField);
        settingsPanel.add(resolutionField);
        settingsPanel.add(backButton);
        settingsPanel.add(applyButton);

        settingsFrame.add(settingsPanel);
        settingsFrame.setSize(500, 500);
        settingsFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        settingsFrame.setVisible(true);

    }

    private void applySettings(String name, String fov, String resolution) {
        try{
            Config.FOV = Integer.parseInt(fov);
            Config.PLAYER_NAME = name;
            Config.RESOLUTION = Integer.parseInt(resolution);
        }catch (NumberFormatException e){
            System.out.println("Invalid input");
        }finally {
            System.out.println("Settings applied");
            System.out.println("Name: " + Config.PLAYER_NAME);
            System.out.println("FOV: " + Config.FOV);
            System.out.println("Resolution: " + Config.RESOLUTION);
        }
    }

    private void exitGame() {
        System.exit(0);
    }

    private void startGame() {
        new Game();
        setVisible(false);
    }

    public static void main(String[] args) {
        new MainMenu();
    }
}
