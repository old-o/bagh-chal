package net.doepner.baghchal;

import net.doepner.baghchal.model.GameTable;
import net.doepner.baghchal.model.Piece;
import net.doepner.baghchal.model.Position;

import static net.doepner.baghchal.model.Piece.PREDATOR;
import static net.doepner.baghchal.model.Piece.PREY;

/**
 * Sets up the pieces on the game table
 */
public final class Setup {

    public void prepare(GameTable gameTable) {
        setCornerPositions(gameTable, PREDATOR);
        setBorderPositions(gameTable, PREY, gameTable.getPositions().getBoardSize() - 5);
    }

    public void setCornerPositions(GameTable gameTable, Piece piece) {
        for (Position p : gameTable.getPositions().getCorners()) {
            gameTable.set(p, piece);
        }
    }

    public void setBorderPositions(GameTable gameTable, Piece piece, int count) {
        for (Position p : gameTable.getPositions().getBorder()) {
            if (count > 0) {
                gameTable.set(p, piece);
                count--;
            }
        }
        gameTable.setHiddenBorderPieceCount(count);
    }
}
