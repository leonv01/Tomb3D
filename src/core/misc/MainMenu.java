package core.misc;

import core.utils.Config;
import core.utils.FileInterpreter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class MainMenu extends JFrame {

    private static MainMenu mainMenu;

    private Game game;

    private final int BUTTON_WIDTH = 100;
    private final int BUTTON_HEIGHT = 50;

    private MainMenu() {

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
        panel.add(settingsButton);
        panel.add(creditsButton);
        panel.add(highscoreButton);
        panel.add(mapEditorButton);
        panel.add(exitButton);

        this.add(panel);
        this.setSize(500, 500);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    private void openMapEditor() {
        MapEditor mapEditor = new MapEditor();
        mapEditor.setVisible(true);
    }

    private void openHighscore() {
        JFrame highscoreFrame = new JFrame();
        JPanel highscorePanel = new JPanel();
        JButton backButton = new JButton("Back");
        JButton resetButton = new JButton("Reset");
        JTextArea highscoreArea = new JTextArea();
        highscoreFrame.setResizable(false);

        Box hBox = Box.createHorizontalBox();
        hBox.add(resetButton);
        hBox.add(backButton);

        highscoreArea.setEditable(false);
        highscoreArea.setText(FileInterpreter.loadHighscore(new File("src/highscore/highscore.txt")));

        highscorePanel.setLayout(new BorderLayout());
        highscorePanel.add(highscoreArea, BorderLayout.CENTER);
        highscorePanel.add(hBox, BorderLayout.SOUTH);

        highscoreFrame.add(highscorePanel);
        highscoreFrame.setSize(500, 500);
        highscoreFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        highscoreFrame.setVisible(true);

        backButton.addActionListener(e -> highscoreFrame.setVisible(false));
        resetButton.addActionListener(e -> FileInterpreter.clearHighscore(new File("src/highscore/highscore.txt")));
    }

    private void openCredits() {

        JFrame creditsFrame = new JFrame();
        JPanel creditsPanel = new JPanel();
        JButton backButton = new JButton("Back");
        JTextArea creditsArea = new JTextArea();

        creditsArea.setEditable(false);
        creditsArea.setText("Credits:\n\n" +
                        "Leon Vogt\n" +
                        "Nikita Kovach\n" +
                        "Silas Winter\n" +
                        "Max Staudenmaier\n"+
                        "Beyza Altinay\n");



        creditsArea.setFont(new Font("Arial", Font.PLAIN, 20));

        creditsPanel.setLayout(new BorderLayout());
        creditsPanel.setBorder(new EmptyBorder(40,40,40,40));
        creditsPanel.add(creditsArea, BorderLayout.CENTER);
        creditsPanel.add(backButton, BorderLayout.SOUTH);

        creditsFrame.add(creditsPanel);
        creditsFrame.setSize(500, 500);
        creditsFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        creditsFrame.setVisible(true);

        backButton.addActionListener(e -> creditsFrame.setVisible(false));
    }

    private void openSettings() {
        GridLayout layout = new GridLayout(6, 1, 10, 10);

        JFrame settingsFrame = new JFrame();
        JPanel settingsPanel = new JPanel();
        JButton backButton = new JButton("Back");
        JButton applyButton = new JButton("Apply");

        JSlider fovSlider = new JSlider(JSlider.HORIZONTAL, 20, 80, Config.FOV);
        fovSlider.addChangeListener(e -> Config.FOV = fovSlider.getValue());
        Font font = new Font("Arial", Font.PLAIN, 20);


        Box vBoxFOV = Box.createHorizontalBox();
        vBoxFOV.add(new JLabel("FOV: "));
        vBoxFOV.add(fovSlider);


        JSlider lookSensitivitySlider = new JSlider(JSlider.HORIZONTAL, 10, 100, 75);
        lookSensitivitySlider.addChangeListener(e -> Config.ROTATION_SPEED = lookSensitivitySlider.getValue() / 1000f);
        Box vBoxSensitivity = Box.createHorizontalBox();
        vBoxSensitivity.add(new JLabel("Look Sensitivity: "));
        vBoxSensitivity.add(lookSensitivitySlider);

        JComboBox<String> resolutionBox = new JComboBox<>();
        resolutionBox.addActionListener(e -> {
            String resolution = (String) resolutionBox.getSelectedItem();
            String[] split = resolution.split("x");
            Config.WIDTH = Integer.parseInt(split[0]);
            Config.HEIGHT = Integer.parseInt(split[1]);
        });
        resolutionBox.addItem("200x200");
        resolutionBox.addItem("400x400");
        resolutionBox.addItem("800x800");
        resolutionBox.addItem("1000x1000");
        resolutionBox.addItem("1200x1200");
        resolutionBox.addItem("1400x1400");
        resolutionBox.addItem("1600x1600");
        resolutionBox.addItem("1800x1800");
        resolutionBox.addItem("2000x2000");
        resolutionBox.addItem("2200x2200");
        resolutionBox.setFont(font);

        resolutionBox.setSelectedItem("1000x1000");


        TextField nameField = new TextField("Enter your name here");
        nameField.setText(Config.PLAYER_NAME);
        nameField.setFont(font);
        nameField.addActionListener(e -> {
            Config.PLAYER_NAME = nameField.getText();
            System.out.println(Config.PLAYER_NAME);
        });
       // TextField fovField = new TextField("Enter your fov here");
        TextField resolutionField = new TextField("Enter your resolution here");

        JRadioButton quality1 = new JRadioButton("Quality 1");
        quality1.addActionListener(e -> Config.rayResolution = 1);
        quality1.setSelected(false);
        JRadioButton quality2 = new JRadioButton("Quality 2");
        quality2.addActionListener(e -> Config.rayResolution = 2);
        quality2.setSelected(false);
        JRadioButton quality3 = new JRadioButton("Quality 3 (Recommended)");
        quality3.addActionListener(e -> Config.rayResolution = 3);
        quality3.setSelected(true);
        JRadioButton quality4 = new JRadioButton("Quality 4");
        quality4.addActionListener(e -> Config.rayResolution = 4);
        quality4.setSelected(false);
        JRadioButton quality5 = new JRadioButton("Quality 5");
        quality5.addActionListener(e -> Config.rayResolution = 5);
        quality5.setSelected(false);

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(quality1);
        buttonGroup.add(quality2);
        buttonGroup.add(quality3);
        buttonGroup.add(quality4);
        buttonGroup.add(quality5);

        nameField.addActionListener(e -> System.out.println("TEST"));
       // fovField.addActionListener(e -> fovField.setText(""));
        resolutionField.addActionListener(e -> resolutionField.setText(""));

        backButton.addActionListener(e -> {
            Config.PLAYER_NAME = nameField.getText();
            settingsFrame.setVisible(false);
        });
       // applyButton.addActionListener(e -> applySettings(nameField.getText(), fovField.getText(), resolutionField.getText()));

        settingsPanel.setLayout(layout);

        Box vBox = Box.createHorizontalBox();


        vBox.add(quality1);
        vBox.add(quality2);
        vBox.add(quality3);
        vBox.add(quality4);
        vBox.add(quality5);

        settingsPanel.add(nameField);
        settingsPanel.add(vBox);
        settingsPanel.add(vBoxSensitivity);
        settingsPanel.add(vBoxFOV);
        settingsPanel.add(resolutionBox);
        settingsPanel.add(backButton);

        settingsPanel.getInsets(new Insets(20, 20, 20, 20));

        settingsFrame.add(settingsPanel);
        settingsFrame.setSize(600, 600);
        settingsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        settingsFrame.setVisible(true);

    }

    public void setVisibility(boolean value){
        this.setVisible(value);
    }

    public static MainMenu getInstance(){
        if(mainMenu == null)
            mainMenu = new MainMenu();
        return mainMenu;
    }

    private void exitGame() {
        System.exit(0);
    }

    private void startGame() {
        if(game == null)
            game = new Game();
        else
            if(!game.getPaused())
                game.setPaused(false);
        setVisible(false);
    }

    public static void main(String[] args) {
        new MainMenu();
    }
}