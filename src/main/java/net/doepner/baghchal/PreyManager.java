package net.doepner.baghchal;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static net.doepner.baghchal.Piece.PREY;

/**
 * Manages the prey pieces (e.g. goats)
 */
class PreyManager extends MouseAdapter {

    private final Board board;

    private EventHandler eventHandler;

    private Position dragStart;

    PreyManager(Board board) {
        this.board = board;
    }

    void setEventHandler(EventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        final Position p = getPosition(e);
        if (board.get(p) == PREY) {
            board.clear(p);
            dragStart = p;
            eventHandler.draggingStarted(e.getPoint());
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (dragStart != null) {
            final Point point = e.getPoint();
            eventHandler.draggedAt(point);
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
            eventHandler.releasedAt(e.getPoint());
            dragStart = null;
            if (validMove) {
                eventHandler.moveDone(move);
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
