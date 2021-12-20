package model;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import java.io.IOException;
import java.io.InputStream;

public class GameSound {
    public static void playBackgroundSound() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Player mineAudio = null;
                try {
                    // play the background
                    while (true) {
                        InputStream in = this.getClass().getResourceAsStream("/bg.mp3");
                        mineAudio = new Player(in);
                        mineAudio.play();
                        in.close();
                    }
                } catch (JavaLayerException | IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void playSoundEffect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // place the disk
                Player mineAudio = null;
                InputStream in = this.getClass().getResourceAsStream("/sound.mp3");
                try {
                    mineAudio = new Player(in);
                    mineAudio.play();
                } catch (JavaLayerException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}