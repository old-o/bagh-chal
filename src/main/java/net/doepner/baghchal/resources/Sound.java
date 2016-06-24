package net.doepner.baghchal.resources;

import static javax.sound.sampled.AudioSystem.getAudioInputStream;
import static javax.sound.sampled.AudioSystem.getLine;
import static javax.sound.sampled.LineEvent.Type.STOP;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import net.doepner.baghchal.model.Levels;

/**
 *
 */
public class Sound {

    private final Levels levels;

    private Clip lastPrey = null;
    private int preyIndex = 1;

    public Sound(Levels levels) {
        this.levels = levels;
    }

    public void playPrey() {
        lastPrey = play("prey" + preyIndex + ".wav");
        preyIndex = (preyIndex % 3) + 1;
    }

    public void playPredatorKills() {
        if (lastPrey != null) {
            lastPrey.stop();
            lastPrey = null;
        }
        play("predator-kills.wav");
    }


    public void playPredatorStep() {
        play("predator-step.wav");
    }

    public Clip play(String resourceFile) {
        return play(levels.getResource(resourceFile));
    }

    public void playResource(String resourcePath) {
        play(getClass().getResource(resourcePath));
    }

    private Clip play(URL url) {
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
