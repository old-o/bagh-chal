package net.doepner.baghchal;

import net.doepner.baghchal.model.Piece;
import net.doepner.baghchal.resources.AudioPlayer;
import net.doepner.baghchal.view.Theme;

import static net.doepner.baghchal.model.Piece.PREDATOR;
import static net.doepner.baghchal.view.Theme.SoundResourceId.PREDATOR_KILLS;
import static net.doepner.baghchal.view.Theme.SoundResourceId.PREDATOR_MOVES;
import static net.doepner.baghchal.view.Theme.SoundResourceId.PREY_MOVES;
import static net.doepner.baghchal.view.Theme.SoundResourceId.WELCOME;

/**
 * Plays sounds as game table events occur
 */
public class EventSounds implements Listener {

    private final AudioPlayer audioPlayer;
    private final Theme theme;

    public EventSounds(AudioPlayer audioPlayer, Theme theme) {
        this.audioPlayer = audioPlayer;
        this.theme = theme;
    }

    @Override
    public void afterJump(Piece piece) {
        if (piece == PREDATOR) {
            play(PREDATOR_KILLS);
        }
    }

    @Override
    public void afterStep(Piece piece) {
        if (piece == PREDATOR) {
            play(PREDATOR_MOVES);
        }
    }

    @Override
    public void afterPicked(Piece piece) {
        if (piece == Piece.PREY) {
            play(PREY_MOVES);
        }
    }

    @Override
    public void afterReset() {
        play(WELCOME);
    }

    private void play(Theme.SoundResourceId resourceId) {
        audioPlayer.play(theme.getSoundResource(resourceId));
    }
}
