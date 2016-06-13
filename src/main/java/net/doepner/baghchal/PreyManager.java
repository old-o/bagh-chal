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
public class PreyManager extends MouseAdapter {

    private static final int TOTAL = 20;

    private final boolean remaining[] = new boolean[TOTAL];

    private final Images images;
    private final Board board;
    private final Phases phases;

    private int selected;

    private Point dragStartPoint;
    private Point lastDragPoint;

    private EventHandler eventHandler;

    public PreyManager(Images images, Board board, Phases phases) {
        this.images = images;
        this.board = board;
        this.phases = phases;
    }

    void reset() {
        dragStartPoint = null;
        lastDragPoint = null;
        for (int i = 0; i < TOTAL; i++) {
            remaining[i] = true;
        }
    }

    public void mousePressed(MouseEvent e) {
        final int width = e.getComponent().getWidth();
        final int height = e.getComponent().getHeight();

        final int x = e.getX();
        final int y = e.getY();

        final int xBoardEnd = width - 50;
        final int yBoardEnd = height - 50;

        // TODO: Simplify by treating "remaining goats" as pieces with a null Position
        // and make all Move(null,p) as "valid step" if p is on the board,
        // make the "remaining" array, an array of Point and generally pick the prey
        // to be dragged by proximity to the mouse press point

        if (x >= xBoardEnd + 10) {
            int i = (y - 10) / 40;
            dragAvailablePrey(i, e);
        } else if (y >= yBoardEnd + 10) {
            int i = (x - 10) / 40 + (TOTAL / 2);
            dragAvailablePrey(i, e);
        }
        if (phases.isMiddle() && isPointOnBoard(e)) {
            final Position p = getPosition(e);
            if (board.get(p) == PREY) {
                dragStartPoint = getPoint(p, e.getComponent());
                lastDragPoint = dragStartPoint;
                board.clear(p);
            }
        }
    }

    private boolean isPointOnBoard(MouseEvent e) {
        final int xBoardStart = 20;
        final int yBoardStart = 20;
        final Component c = e.getComponent();
        final int xBoardEnd = c.getWidth() - 50;
        final int yBoardEnd = c.getHeight() - 50;
        int x = e.getX();
        int y = e.getY();
        return x >= xBoardStart && x <= xBoardEnd && y >= yBoardStart && y <= yBoardEnd;
    }

    private void dragAvailablePrey(int i, MouseEvent e) {
        if (remaining[i]) {
            selected = i;
            dragStartPoint = getRemainingPreyPoint(selected, e);
            lastDragPoint = dragStartPoint;
            draggingStarted();
            remaining[i] = false;
        }
    }

    public void mouseDragged(MouseEvent e) {
        if (dragging()) {
            repaintRectangleAt(translate(e.getPoint()));
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (!dragging()) {
            return;
        }
        final Position dragStartPos = getPosition(dragStartPoint, e.getComponent());
        final Position pos = board.normalize(getPosition(e));
        final Move move = new Move(dragStartPos, pos);
        if (board.isEmpty(pos) && (phases.isBeginning() || board.validStep(move))) {
            // set the piece
            board.set(pos, PREY);
            final Point point = getPoint(pos, e.getComponent());
            repaintRectangleAt(point);
            if (phases.isBeginning() && noRemaining()) {
                phases.setMiddle();
            }
            if (!point.equals(dragStartPoint)) {
                moveDone();
            }
        } else {
            if (phases.isBeginning()) {
                remaining[selected] = true;
            } else {
                board.set(dragStartPos, PREY);
            }
            repaintRectangleAt(dragStartPoint);
        }
        dragStartPoint = null;
        lastDragPoint = null;
        selected = -1;
    }

    private Point getPoint(Position pos, Component c) {
        final double xStep = c.getWidth() / board.getXSize();
        final double yStep = c.getHeight() / board.getYSize();
        return new Point(14 + (int) (pos.x() * xStep), 14 + (int) (pos.y() * yStep));
    }

    private Point translate(Point point) {
        final Image image = preyImage();
        point.translate(-(image.getWidth(null))/2, -(image.getHeight(null))/2);
        return point;
    }

    private Position getPosition(MouseEvent e) {
        return getPosition(e.getPoint(), e.getComponent());
    }

    private Position getPosition(Point p, Component c) {
        final double xStep = c.getWidth() / board.getXSize();
        final double yStep = c.getHeight() / board.getYSize();
        return new Position((int) (p.x / xStep + 0.25D), (int) (p.y / yStep + 0.25D));
    }

    boolean noRemaining() {
        for (int i = 0; i < TOTAL; i++) {
            if (remaining[i]) {
                return false;
            }
        }
        return true;
    }

    public void setEventHandler(EventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    private void repaintRectangleAt(Point p) {
        if (eventHandler != null) {
            final Image img = preyImage();
            if (lastDragPoint != null) {
                eventHandler.repaintRectangleAt(getRectangle(lastDragPoint, img));
            }
            if (p != null && !p.equals(lastDragPoint)) {
                eventHandler.repaintRectangleAt(getRectangle(p, img));
            }
            lastDragPoint = p;
        }
    }

    private Rectangle getRectangle(Point p, Image img) {
        return new Rectangle(p.x, p.y, img.getWidth(null), img.getHeight(null));
    }

    private void moveDone() {
        if (eventHandler != null) {
            eventHandler.moveDone();
        }
    }

    private void draggingStarted() {
        if (eventHandler != null) {
            eventHandler.draggingStarted();
        }
    }

    public void drawDraggedPrey(Graphics2D g2) {
        if (dragging()) {
            g2.drawImage(preyImage(), lastDragPoint.x, lastDragPoint.y, null);
        }
    }

    private boolean dragging() {
        return lastDragPoint != null;
    }

    void drawRemainingPrey(Graphics2D g2, int width, int height) {
        for (int i = 0; i < TOTAL; i++) {
            if (remaining[i]) {
                final Point p = getRemainingPreyPoint(i, width, height);
                g2.drawImage(preyImage(), p.x, p.y, null);
            }
        }
    }

    private Image preyImage() {
        return images.getImage(PREY);
    }

    private Point getRemainingPreyPoint(int i, MouseEvent e) {
        final Component c = e.getComponent();
        return getRemainingPreyPoint(i, c.getWidth(), c.getHeight());
    }

    private Point getRemainingPreyPoint(int i, int width, int height) {
        final int half = TOTAL / 2;
        final int x, y;
        if (i < half) {
            x = 10 + (width - 50);
            y = 10 + i * 40;
        } else {
            x = 10 + (i - half) * 40;
            y = 10 + (height - 50);
        }
        return new Point(x, y);
    }
}
