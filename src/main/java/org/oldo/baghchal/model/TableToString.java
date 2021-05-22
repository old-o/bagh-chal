package org.oldo.baghchal.model;

import org.oldo.text.CharCanvas;
import org.oldo.text.CharDrawing;

import static org.oldo.baghchal.model.Direction.*;
import static org.oldo.baghchal.model.Direction.RIGHT;

public class TableToString {

    private final GameTable gameTable;
    private final CharCanvas charCanvas;

    public TableToString(GameTable gameTable, CharCanvas charCanvas) {
        this.gameTable = gameTable;
        this.charCanvas = charCanvas;
    }

    public String toString() {
        final int xStep = 2;
        final int yStep = 2;

        final CharDrawing drawing = charCanvas.newDrawing(
                gameTable.getXSize() * xStep,
                gameTable.getYSize() * yStep
        );

        for (Position p : positions().getAll()) {
            final int x = p.x() * xStep;
            final int y = p.y() * yStep;

            for (Move step : gameTable.getStepsAlongLineFrom(p)) {
                drawing.addLine(x, y, xStep, yStep, step);
            }
            final Piece piece = gameTable.get(p);
            if (piece != null) {
                drawing.addChar(x, y, piece.asChar());
            } else if (positions().isBoard(p)) {
                drawing.addChar(x, y, getBoardChar(p));
            }
        }
        return drawing.toString();
    }

    private char getBoardChar(Position p) {
        if (isBorder(p, UP)) {
            return isBorder(p, LEFT) ? '┌' : isBorder(p, RIGHT) ? '┐' : '┬';
        }
        if (isBorder(p, DOWN)) {
            return isBorder(p, LEFT) ? '└' : isBorder(p, RIGHT) ? '┘' : '┴';
        }
        else {
            return isBorder(p, LEFT) ? '├' : isBorder(p, RIGHT) ? '┤' : '┼';
        }
    }

    private boolean isBorder(Position p, Direction d) {
        return positions().isBorder(d.addTo(p));
    }

    private TablePositions positions() {
        return gameTable.getPositions();
    }
}
