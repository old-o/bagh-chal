package net.doepner.baghchal.view;

import net.doepner.baghchal.control.PlayFlow;
import net.doepner.baghchal.model.GameTable;
import net.doepner.baghchal.model.Move;
import net.doepner.baghchal.model.Piece;
import net.doepner.baghchal.model.Position;
import net.doepner.baghchal.resources.Images;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * Manages the moving of pieces via drag and drop
 */
public class DragAndDropHandler extends MouseAdapter {

    private final GameTable gameTable;
    private final GamePanel gamePanel;
    private final Images images;

    private final PlayFlow playFlow;
    private final Piece piece;

    private Position dragStart;
    private Point dragStartPoint;

    public DragAndDropHandler(Piece piece, GameTable gameTable, GamePanel gamePanel,
                              Images images, PlayFlow playFlow) {
        this.piece = piece;
        this.gameTable = gameTable;
        this.playFlow = playFlow;
        this.gamePanel = gamePanel;
        this.images = images;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        final Position p = getPosition(e);
            dragStart = gameTable.pick(p, piece);
            if (dragStart != null) {
                dragStartPoint = e.getPoint();
                lastDragPoint(dragStartPoint);
            }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (dragStart != null) {
            final Point point = e.getPoint();
            final BufferedImage image = images.getImage(piece);
            point.translate(-image.getWidth(null) / 2, -image.getHeight(null) / 2);
            repaintDraggedAt(point);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        final Position p = getPosition(e);
        if (dragStart != null) {
            final Move move = new Move(dragStart, p);
            final boolean validMove = gameTable.isValid(move, piece);
            final Position resultingPosition = validMove ? move.p2() : dragStart;
            gameTable.set(resultingPosition, piece);
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
        final double xStep = c.getWidth() / gameTable.getXSize();
        final double yStep = c.getHeight() / gameTable.getYSize();
        return new Position((int) (p.x / xStep), (int) (p.y / yStep));
    }

    private void repaintDraggedAt(Point p) {
        if (lastDragPoint() != null) {
            repaintRectangleAround(lastDragPoint());
        }
        if (p != null && !p.equals(lastDragPoint())) {
            repaintRectangleAround(p);
        }
        lastDragPoint(p);
    }

    private Point lastDragPoint() {
        return gamePanel.getLastDragPoint();
    }

    private void lastDragPoint(Point p) {
        gamePanel.setLastDragPoint(p);
    }

    private void repaintRectangleAround(Point p) {
        final BufferedImage image = images.getImage(piece);
        int imgWidth = image.getWidth();
        int imgHeight = image.getHeight();
        final Rectangle rectangle = new Rectangle(p.x - (imgWidth / 2), p.y - (imgHeight / 2), 2 * imgWidth, 2 * imgHeight);
        gamePanel.repaintForDrag(rectangle, image);
    }
}
