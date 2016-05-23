package net.doepner.baghchal;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

import static javax.sound.sampled.AudioSystem.getAudioInputStream;
import static javax.sound.sampled.LineEvent.Type.STOP;

/**
 *
 */
public class Sound {

    private final URL[] goats = new URL[]{
            getClass().getResource("goat1.wav"),
            getClass().getResource("goat2.wav"),
            getClass().getResource("goat3.wav"),
            getClass().getResource("sheep.wav")
    };

    private final URL tiger = getClass().getResource("tiger.wav");

    private Clip lastGoat = null;
    private int goatIndex = 0;

    public void playGoat() {
        lastGoat = play(goats[goatIndex]);
        goatIndex = (goatIndex + 1) % goats.length;
    }

    public void playTiger() {
        if (lastGoat != null) {
            lastGoat.stop();
            lastGoat = null;
        }
        play(tiger);
    }

    public Clip play(URL url) {
        try (AudioInputStream stream = getAudioInputStream(url)) {

            final Clip clip = (Clip) AudioSystem.getLine(
                    new DataLine.Info(Clip.class, stream.getFormat()));

            clip.open(stream);
            clip.start();

            clip.addLineListener(event -> {
                if (STOP.equals(event.getType())) {
                    clip.close();
                }
            });

            return clip;

        } catch (UnsupportedAudioFileException | LineUnavailableException e) {
            throw new IllegalArgumentException(e);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

}
