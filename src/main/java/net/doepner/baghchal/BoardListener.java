package net.doepner.baghchal;

import net.doepner.baghchal.model.Piece;

/**
 * Listens to board events
 */
public interface BoardListener {

    void afterJump(Piece piece);

    void afterStep(Piece piece);

    void afterPicked(Piece piece);

    void afterReset();

    BoardListener NONE = new BoardListener() {

        @Override
        public void afterJump(Piece piece) {
            // ignore
        }

        @Override
        public void afterStep(Piece piece) {
            // ignore
        }

        @Override
        public void afterPicked(Piece piece) {
            // ignore
        }

        @Override
        public void afterReset() {
            // ignore
        }
    };
}
