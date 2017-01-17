package net.doepner.baghchal;

import net.doepner.baghchal.model.Direction;
import net.doepner.baghchal.model.GameTable;
import net.doepner.baghchal.model.Piece;
import net.doepner.baghchal.model.Position;
import net.doepner.baghchal.model.TablePositions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static net.doepner.baghchal.model.Piece.PREDATOR;
import static net.doepner.baghchal.model.Piece.PREY;

/**
 * Sets up the pieces on the game table
 */
public final class GameTableSetup {

    static void prepare(GameTable gameTable) {
        final int boardSize = gameTable.getPositions().getBoardSize();
        final int predatorCount = (boardSize / 5) - 1;
        setCornerPositions(gameTable, PREDATOR, predatorCount);
        setBorderPositions(gameTable, PREY, boardSize - 5);
    }

    private static void setCornerPositions(GameTable gameTable, Piece piece, int predatorCount) {
        int remaining = predatorCount;

        for (Position p : gameTable.getPositions().getCorners()) {
            gameTable.set(p, piece);
            remaining--;
        }
        for (Position p : getPositionsToFill(gameTable, remaining, piece)) {
            gameTable.set(p, piece);
        }
    }

    private static Iterable<Position> getPositionsToFill(GameTable gameTable, int remaining, Piece piece) {
        final TablePositions positions = gameTable.getPositions();
        if (remaining > 0) {
            final Collection<Position> toFill = new ArrayList<>();
            final Iterable<Position> board = positions.getBoard();
            for (Position p : board) {
                if (gameTable.get(p) == piece) {
                    for (Direction d : Direction.values()) {
                        final Position p1 = d.addTo(p);
                        if (positions.isBoard(p1) && gameTable.isEmptyAt(p1)) {
                            toFill.add(p);
                            remaining--;
                            if (remaining <= 0) {
                                return toFill;
                            }
                        }
                    }
                }
            }
            return toFill;
        }
        return Collections.emptyList();
    }

    private static void setBorderPositions(GameTable gameTable, Piece piece, int count) {
        for (Position p : gameTable.getPositions().getBorder()) {
            if (count > 0) {
                gameTable.set(p, piece);
                count--;
            }
        }
        gameTable.setHiddenBorderPieceCount(count);
    }
}
