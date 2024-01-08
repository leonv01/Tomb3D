package core.utils;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SoundManager extends Thread {

    Thread thread;

    private final Map<String, Clip> soundMap;
    private FloatControl gainControl;

    private static SoundManager instance = null;

    public SoundManager() {
        thread = new Thread(this);
        this.soundMap = new HashMap<>();
        loadSound("shoot", "/sfx/Shot.wav");
         loadSound("door", "/sfx/DoorOpen.wav");
        loadSound("reload", "/sfx/Reload.wav");
        loadSound("ammo", "/sfx/Ammopickup.wav");
        loadSound("ambient", "/sfx/DungeonSlayer.wav");
        thread.start();
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    public void loadSound(String name, String filename){
        try{
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
                    Objects.requireNonNull(SoundManager.class.getResourceAsStream(filename))
            );

            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            soundMap.put(name, clip);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void playSound(String name){
        Clip clip = soundMap.get(name);
        gainControl = (FloatControl) soundMap.get(name).getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(-10.0f);
        if(clip == null) return;
        if(clip.isRunning()) clip.stop();
        clip.setFramePosition(0);
        clip.start();
    }

    public void stopSound(String name){
        Clip clip = soundMap.get(name);
        if(clip == null) return;
        if(clip.isRunning()) clip.stop();
    }

    public static void main(String[] args) {
        SoundManager soundManager = new SoundManager();
        soundManager.playSound("ambient");
    }
}