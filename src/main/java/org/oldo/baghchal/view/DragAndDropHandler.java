package org.oldo.baghchal.view;

import org.oldo.baghchal.control.PlayFlow;
import org.oldo.baghchal.model.GameTable;
import org.oldo.baghchal.model.Move;
import org.oldo.baghchal.model.Piece;
import org.oldo.baghchal.model.Position;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Manages the moving of pieces via drag and drop
 */
public class DragAndDropHandler extends MouseAdapter {

    private final GameTable gameTable;
    private final GameView view;

    private final PlayFlow playFlow;
    private final Piece piece;

    private Position dragStart;

    public DragAndDropHandler(GameTable gameTable, GameView view, PlayFlow playFlow, Piece piece) {
        this.gameTable = gameTable;
        this.view = view;
        this.playFlow = playFlow;
        this.piece = piece;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        final Position p = view.getPosition(e.getPoint());
        dragStart = gameTable.pick(p, piece);
        if (dragStart != null) {
            view.notifyDraggedTo(p, piece);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (dragStart != null) {
            view.notifyDraggedTo(e.getPoint(), piece);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        final Position p = view.getPosition(e.getPoint());
        if (dragStart != null) {
            final Move move = new Move(dragStart, p);
            final boolean validMove = gameTable.isValid(move, piece);
            gameTable.set(dragStart, piece);
            if (validMove) {
                playFlow.moveDone(move);
            } else {
                view.notifyDraggedTo(dragStart, piece);
            }
            resetDragState();
        }
    }

    private void resetDragState() {
        view.setLastDragPoint(null);
        dragStart = null;
    }

}
