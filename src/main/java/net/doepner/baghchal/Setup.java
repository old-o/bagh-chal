package net.doepner.baghchal;

import net.doepner.baghchal.model.GameTable;

import static net.doepner.baghchal.model.Piece.PREDATOR;
import static net.doepner.baghchal.model.Piece.PREY;

/**
 * Sets up the pieces on the game table
 */
public final class Setup {

    public void prepare(GameTable gameTable) {
        gameTable.setCornerPositions(PREDATOR);
        gameTable.setBorderPositions(PREY, 20);
    }
}
