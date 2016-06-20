package net.doepner.baghchal;

import static net.doepner.baghchal.Piece.PREDATOR;
import static net.doepner.baghchal.Piece.PREY;

/**
 * Sets up the pieces on the board
 */
public final class BoardSetup {

    void setup(Board board) {
        final Position p1 = board.getTopLeft();
        final Position p2 = board.getBottomRight();
        board.set(p1, PREDATOR);
        board.set(p1.x(), p2.y(), PREDATOR);
        board.set(p2.x(), p1.y(), PREDATOR);
        board.set(p2, PREDATOR);
        for (int n = 1; n <= 5; n++) {
            board.set(p1.x() - 1, n, PREY);
            board.set(p2.x() + 1, n, PREY);
            board.set(n, p1.y() - 1, PREY);
            board.set(n, p2.y() + 1, PREY);
        }
    }
}
