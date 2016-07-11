package net.doepner.baghchal;

import static net.doepner.baghchal.model.Piece.PREDATOR;
import static net.doepner.baghchal.model.Piece.PREY;

import net.doepner.baghchal.model.GameTable;
import net.doepner.baghchal.model.Position;

/**
 * Sets up the pieces on the game table
 */
public final class GameSetup {

    public void setup(GameTable gameTable) {
        for (Position p : gameTable.getCornerPositions()) {
            gameTable.set(p, PREDATOR);
        }

        final Position p1 = gameTable.getTopLeft();
        final Position p2 = gameTable.getBottomRight();

        for (int x = p1.x(); x <= p2.x(); x++) {
            gameTable.set(x, p1.y() - 1, PREY);
            gameTable.set(x, p2.y() + 1, PREY);
        }
        for (int y = p1.y(); y <= p2.y(); y++) {
            gameTable.set(p1.x() - 1, y, PREY);
            gameTable.set(p2.x() + 1, y, PREY);
        }
    }
}
