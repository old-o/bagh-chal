package org.oldo.baghchal;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;

/**
 * Checks if the audio system uses a broken implementation
 */
public final class AudioSystemChecker implements Runnable {

    private static final String PULSEAUDIO_BUG_REFERENCE =
            "https://stackoverflow.com/questions/45847635/java-audio-clip-cannot-be-closed-when-using-linux-pulseaudio";

    public void run() {
        if (audioClipClassNameContains("PulseAudioClip")) {
            throw new IllegalStateException("Buggy AudioSystem detected (PulseAudio/Java bindings). "
                    + "Please adjust your Java sound.properties. See " + PULSEAUDIO_BUG_REFERENCE + " for details.");
        }
    }

    private static boolean audioClipClassNameContains(String s) {
        try (final Clip clip = AudioSystem.getClip()) {
            return clip == null || clip.getClass().getName().contains(s);
        } catch (LineUnavailableException e) {
            throw new IllegalStateException(e);
        }
    }
}
