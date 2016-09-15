package net.doepner.baghchal;

import static net.doepner.baghchal.model.Piece.PREDATOR;

import net.doepner.baghchal.model.Piece;
import net.doepner.baghchal.resources.Sound;

/**
 * Plays sounds as game table events occur
 */
public class Sounds implements Listener {

    private final Sound sound;

    public Sounds(Sound sound) {
        this.sound = sound;
    }

    @Override
    public void afterJump(Piece piece) {
        if (piece == PREDATOR) {
            sound.playPredatorKills();
        }
    }

    @Override
    public void afterStep(Piece piece) {
        if (piece == PREDATOR) {
            sound.playPredatorStep();
        }
    }

    @Override
    public void afterPicked(Piece piece) {
        if (piece == Piece.PREY) {
            sound.playPrey();
        }
    }

    @Override
    public void afterReset() {
        sound.play("welcome.wav");
    }
}
