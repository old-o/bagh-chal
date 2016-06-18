package net.doepner.baghchal;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static net.doepner.baghchal.Piece.PREY;

/**
 * Manages the prey pieces (e.g. goats)
 */
class PreyManager extends MouseAdapter {

    private final Images images;
    private final Board board;

    private Position dragStart;
    private Point lastDragPoint;

    private EventHandler eventHandler;

    PreyManager(Images images, Board board) {
        this.images = images;
        this.board = board;
        reset();
    }

    void setEventHandler(EventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    void reset() {
        dragStart = null;
        lastDragPoint = null;
        for (int n = 1; n <= 5; n++) {
            board.set(0, n, PREY);
            board.set(6, n, PREY);
            board.set(n, 0, PREY);
            board.set(n, 6, PREY);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        final Position p = getPosition(e);
        if (board.get(p) == PREY) {
            board.clear(p);
            dragStart = p;
            lastDragPoint = e.getPoint();
            eventHandler.draggingStarted();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (dragStart != null) {
            final Point point = e.getPoint();
            final Image image = preyImage();
            point.translate(-image.getWidth(null) / 2, -image.getHeight(null) / 2);
            repaintRectangleAt(point);
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
            repaintRectangleAt(e.getPoint());
            dragStart = null;
            lastDragPoint = null;
            if (validMove) {
                eventHandler.moveDone();
            }
        }
    }

    void drawDraggedPrey(Graphics2D g2) {
        if (lastDragPoint != null) {
            g2.drawImage(preyImage(), lastDragPoint.x, lastDragPoint.y, null);
        }
    }

    private Position getPosition(MouseEvent e) {
        final Point p = e.getPoint();
        final Component c = e.getComponent();
        final double xStep = c.getWidth() / board.getXSize();
        final double yStep = c.getHeight() / board.getYSize();
        return new Position((int) (p.x / xStep), (int) (p.y / yStep));
    }

    private void repaintRectangleAt(Point p) {
        final Image img = preyImage();
        if (lastDragPoint != null) {
            eventHandler.repaintRectangleAt(getRectangle(lastDragPoint, img));
        }
        if (p != null && !p.equals(lastDragPoint)) {
            eventHandler.repaintRectangleAt(getRectangle(p, img));
        }
        lastDragPoint = p;
    }

    private Rectangle getRectangle(Point p, Image img) {
        int imgWidth = img.getWidth(null);
        int imgHeight = img.getHeight(null);
        return new Rectangle(p.x - imgWidth, p.y - imgHeight, 2 * imgWidth, 2 * imgHeight);
    }

    private Image preyImage() {
        return images.getImage(PREY);
    }
}
