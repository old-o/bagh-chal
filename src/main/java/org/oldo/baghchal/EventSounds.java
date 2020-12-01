package org.oldo.baghchal;

import org.oldo.baghchal.model.Piece;
import org.oldo.baghchal.theming.Theme;
import org.oldo.baghchal.theming.Theme.SoundResourceId;

import java.net.URL;
import java.util.function.Consumer;

import static org.oldo.baghchal.model.Piece.PREDATOR;
import static org.oldo.baghchal.theming.Theme.SoundResourceId.PREDATOR_KILLS;
import static org.oldo.baghchal.theming.Theme.SoundResourceId.PREDATOR_MOVES;
import static org.oldo.baghchal.theming.Theme.SoundResourceId.PREY_MOVES;

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
        if (piece == Piece.PREY) {
            play(PREY_MOVES);
        }
    }

    private void play(SoundResourceId resourceId) {
        audioPlayMethod.accept(theme.getSoundResource(resourceId));
    }
}
