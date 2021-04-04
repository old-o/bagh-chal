package org.oldo.baghchal;

import org.oldo.baghchal.model.*;

/**
 * Sets up the pieces on the game table
 */
public final class AlquerqueSetup {

    static void prepare(GameTable gameTable, Piece piece1, Piece piece2) {
        final int xMiddle = gameTable.getXSize() / 2;
        final int yMiddle = gameTable.getYSize() / 2;
        for (Position p : gameTable.getPositions().getAll()) {
            if (gameTable.getPositions().isBoard(p)) {
                if (p.y() < yMiddle || p.y() == yMiddle && p.x() < xMiddle) {
                    gameTable.set(p, piece1);
                }
                if (yMiddle < p.y() || p.y() == yMiddle && xMiddle < p.x()) {
                    gameTable.set(p, piece2);
                }
            }
        }
    }

}
