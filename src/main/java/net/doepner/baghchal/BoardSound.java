package net.doepner.baghchal;

import static net.doepner.baghchal.Piece.PREDATOR;

/**
 * Plays sounds as board events occur
 */
public class BoardSound implements BoardListener {

    private final Sound sound;

    public BoardSound(Sound sound) {
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
