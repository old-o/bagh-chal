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
        setBorderPositions(gameTable, PREY, boardSize - 1 - predatorCount);
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
        if (remaining <= 0) {
            return Collections.emptyList();
        }
        final Collection<Position> toFill = new ArrayList<>();
        for (int i = 1; i < gameTable.getMaxStepFromCorner(); i++) {
            for (Direction d : Direction.values()) {
                for (Position p : positions.getBoard()) {
                    if (gameTable.get(p) == piece) {
                        final Position p1 = addSteps(p, d, i);
                        if (positions.isBoard(p1) && gameTable.isEmptyAt(p1)) {
                            toFill.add(p1);
                            remaining--;
                            if (remaining <= 0) {
                                return toFill;
                            }
                        }
                    }
                }
            }
        }
        return toFill;
    }

    private static Position addSteps(Position p, Direction d, int i) {
        Position x = p;
        for (int j = 0; j < i; j++) {
            x = d.addTo(x);
        }
        return x;
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
