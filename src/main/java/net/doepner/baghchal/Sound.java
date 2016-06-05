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

    private final Phases phases;

    private Clip lastGoat = null;
    private int goatIndex = 1;

    public Sound(Phases phases) {
        this.phases = phases;
    }

    public void playGoat() {
        lastGoat = play("prey" + goatIndex + ".wav");
        goatIndex = (goatIndex % 3) + 1;
    }

    public void playPredatorKills() {
        if (lastGoat != null) {
            lastGoat.stop();
            lastGoat = null;
        }
        play("predator-kills.wav");
    }


    public void playPredatorStep() {
        play("predator-step.wav");
    }

    public Clip play(String resourceFile) {
        return play(getClass().getResource(phases.getLevel() + "/" + resourceFile));
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
