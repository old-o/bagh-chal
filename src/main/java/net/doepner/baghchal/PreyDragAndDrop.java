package net.doepner.baghchal;

import static net.doepner.baghchal.Piece.PREY;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Manages the prey pieces (e.g. goats)
 */
public class PreyDragAndDrop extends MouseAdapter {

    private final Board board;
    private final PlayFlow playFlow;

    private DragEventHandler dragEventHandler;

    private Position dragStart;

    PreyDragAndDrop(Board board, PlayFlow playFlow) {
        this.board = board;
        this.playFlow = playFlow;
    }

    void setDragEventHandler(DragEventHandler dragEventHandler) {
        this.dragEventHandler = dragEventHandler;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        final Position p = getPosition(e);
            dragStart = board.pick(p, PREY);
            if (dragStart != null) {
                dragEventHandler.draggingStarted(e.getPoint());
            }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (dragStart != null) {
            final Point point = e.getPoint();
            dragEventHandler.draggedAt(point);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        final Position p = getPosition(e);
        if (dragStart != null) {
            final Move move = new Move(dragStart, p);
            final boolean validMove = board.isValid(move);
            final Position resultingPosition = validMove ? move.p2() : dragStart;
            board.set(resultingPosition, PREY);
            dragEventHandler.releasedAt(e.getPoint());
            dragStart = null;
            if (validMove) {
                playFlow.moveDone(move);
            }
        }
    }

    private Position getPosition(MouseEvent e) {
        final Point p = e.getPoint();
        final Component c = e.getComponent();
        final double xStep = c.getWidth() / board.getXSize();
        final double yStep = c.getHeight() / board.getYSize();
        return new Position((int) (p.x / xStep), (int) (p.y / yStep));
    }
}
