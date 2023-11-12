package core.utils;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class SoundManager extends JFrame {

    private static SoundManager soundManager;
    private Clip clip;
    private Thread audioThread;

    private SoundManager() {
    }

    public static SoundManager getInstance() {
        if (soundManager == null)
            soundManager = new SoundManager();
        return soundManager;
    }

    public void playAudio(String filePath) {
        if (audioThread != null && audioThread.isAlive()) {
            stopAudio();
        }

        audioThread = new Thread(() -> {
            try {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filePath));
                clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                clip.start();
                clip.drain();

                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        audioThread.start();
    }

    public void stopAudio() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.close();
        }
    }
}
