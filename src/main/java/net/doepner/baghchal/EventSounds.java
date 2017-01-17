package net.doepner.baghchal;

import net.doepner.baghchal.model.Piece;
import net.doepner.baghchal.theming.Theme;
import net.doepner.baghchal.theming.Theme.SoundResourceId;

import java.net.URL;
import java.util.function.Consumer;

import static net.doepner.baghchal.model.Piece.PREDATOR;
import static net.doepner.baghchal.theming.Theme.SoundResourceId.PREDATOR_KILLS;
import static net.doepner.baghchal.theming.Theme.SoundResourceId.PREDATOR_MOVES;
import static net.doepner.baghchal.theming.Theme.SoundResourceId.PREY_MOVES;

/**
 * Plays sounds as game table events occur
 */
public final class EventSounds implements Listener {

    private final Consumer<URL> audioPlayMethod;
    private final Theme theme;

    EventSounds(Consumer<URL> audioPlayMethod, Theme theme) {
        this.audioPlayMethod = audioPlayMethod;
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
//        play(WELCOME);
    }

    private void play(SoundResourceId resourceId) {
        audioPlayMethod.accept(theme.getSoundResource(resourceId));
    }
}
