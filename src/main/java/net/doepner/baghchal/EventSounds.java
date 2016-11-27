package net.doepner.baghchal;

import net.doepner.baghchal.model.Piece;
import net.doepner.baghchal.resources.AudioPlayer;

import static net.doepner.baghchal.model.Piece.PREDATOR;

/**
 * Plays sounds as game table events occur
 */
public class EventSounds implements Listener {

    private final AudioPlayer audioPlayer;

    public EventSounds(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
    }

    @Override
    public void afterJump(Piece piece) {
        if (piece == PREDATOR) {
            audioPlayer.playPredatorKills();
        }
    }

    @Override
    public void afterStep(Piece piece) {
        if (piece == PREDATOR) {
            audioPlayer.playPredatorStep();
        }
    }

    @Override
    public void afterPicked(Piece piece) {
        if (piece == Piece.PREY) {
            audioPlayer.playPrey();
        }
    }

    @Override
    public void afterReset() {
        audioPlayer.play("welcome.wav");
    }
}
