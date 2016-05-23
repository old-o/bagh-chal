package net.doepner.baghchal;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.net.URL;

import static javax.sound.sampled.AudioSystem.getAudioInputStream;
import static javax.sound.sampled.AudioSystem.getLine;
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

            final Clip clip = (Clip) getLine(new Info(Clip.class, stream.getFormat()));

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
