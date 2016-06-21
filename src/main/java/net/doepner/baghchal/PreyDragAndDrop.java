package net.doepner.baghchal;

import static net.doepner.baghchal.Piece.PREY;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Manages the prey pieces (e.g. goats)
 */
public class PreyDragAndDrop extends MouseAdapter {

    private final Board board;
    private final BoardPanel boardPanel;
    private final Image image;

    private final PlayFlow playFlow;

    private Position dragStart;
    private Point dragStartPoint;

    PreyDragAndDrop(Board board, BoardPanel boardPanel, Image image, PlayFlow playFlow) {
        this.board = board;
        this.playFlow = playFlow;
        this.boardPanel = boardPanel;
        this.image = image;
        boardPanel.setDraggedImage(image);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        final Position p = getPosition(e);
            dragStart = board.pick(p, PREY);
            if (dragStart != null) {
                dragStartPoint = e.getPoint();
                lastDragPoint(e.getPoint());
            }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (dragStart != null) {
            final Point point = e.getPoint();
            point.translate(-image.getWidth(null) / 2, -image.getHeight(null) / 2);
            repaintDraggedAt(point);
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
            repaintDraggedAt(e.getPoint());
            repaintDraggedAt(dragStartPoint);
            lastDragPoint(null);
            dragStartPoint = null;
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

    public void repaintDraggedAt(Point p) {
        if (lastDragPoint() != null) {
            repaintRectangle(lastDragPoint());
        }
        if (p != null && !p.equals(lastDragPoint())) {
            repaintRectangle(p);
        }
        lastDragPoint(p);
    }

    private Point lastDragPoint() {
        return boardPanel.getLastDragPoint();
    }

    private void lastDragPoint(Point p) {
        boardPanel.setLastDragPoint(p);
    }

    private void repaintRectangle(Point p) {
        int imgWidth = image.getWidth(null);
        int imgHeight = image.getHeight(null);
        final Rectangle rectangle = new Rectangle(p.x - imgWidth, p.y - imgHeight, 2 * imgWidth, 2 * imgHeight);
        boardPanel.repaintForDrag(rectangle);
    }
}
