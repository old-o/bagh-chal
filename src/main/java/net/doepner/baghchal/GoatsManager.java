package net.doepner.baghchal;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static net.doepner.baghchal.Piece.GOAT;

/**
 * Manages the goats
 */
public class GoatsManager extends MouseAdapter {

    private static final int TOTAL_GOATS = 20;

    private final boolean remainingGoat[] = new boolean[TOTAL_GOATS];

    private final Image goat;

    private final Board board;
    private final Phases phases;

    private int selectedGoat;

    private boolean dragging = false;

    private int mouseX;
    private int mouseY;

    private Position draggedPiecePos = null;

    private EventHandler eventHandler;

    public GoatsManager(Images images, Board board, Phases phases) {
        this.goat = images.getGoatImage();
        this.board = board;
        this.phases = phases;
    }

    void reset() {
        draggedPiecePos = null;
        for (int i = 0; i < TOTAL_GOATS; i++) {
            remainingGoat[i] = true;
        }
    }

    public void mousePressed(MouseEvent e) {
        final int width = e.getComponent().getWidth();
        final int height = e.getComponent().getHeight();

        final int x = e.getX();
        final int y = e.getY();

        final int xBoardEnd = width - 50;
        final int yBoardEnd = height - 50;

        if (x >= xBoardEnd + 10) {
            int i = (y - 10) / 40;
            dragAvailableGoat(i);
        } else if (y >= yBoardEnd + 10) {
            int i = (x - 10) / 40 + (TOTAL_GOATS / 2);
            dragAvailableGoat(i);
        }
        if (phases.isMiddle() && isPositionOnBoard(e)) {
            final Position p = getPosition(e);
            if (board.get(p) == GOAT) {
                draggedPiecePos = p;
                board.clear(p);
                dragging = true;
            }
        }
    }

    private boolean isPositionOnBoard(MouseEvent e) {
        final int xBoardStart = 20;
        final int yBoardStart = 20;

        final int xBoardEnd = e.getComponent().getWidth() - 50;
        final int yBoardEnd = e.getComponent().getHeight() - 50;

        int x = e.getX();
        int y = e.getY();

        return x >= xBoardStart && x <= xBoardEnd && y >= yBoardStart && y <= yBoardEnd;
    }

    private void dragAvailableGoat(int i) {
        if (remainingGoat[i]) {
            goatDraggingStarted();
            selectedGoat = i;
            remainingGoat[i] = false;
            dragging = true;
        }
    }

    public void mouseDragged(MouseEvent e) {
        if (dragging) {
            mouseX = e.getX() - 16; // hard-coded assumption
            mouseY = e.getY() - 16; // that images are 32x32
            boardChanged(e);
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (!dragging) {
            return;
        }
        dragging = false;
        final Position pos = board.normalize(getPosition(e));
        final Move move = new Move(draggedPiecePos, pos);
        if (board.isEmpty(pos) && (phases.isBeginning() || board.validStep(move))) {
            board.set(move.p2(), GOAT);
            if (phases.isBeginning() && noRemainingGoats()) {
                phases.setMiddle();
            }
            if (!pos.equals(draggedPiecePos)) {
                goatMoveDone();
            }
        } else {
            if (phases.isBeginning()) {
                remainingGoat[selectedGoat] = true;
            } else {
                board.set(draggedPiecePos, GOAT);
            }
        }
        boardChanged(e);
    }

    private Position getPosition(MouseEvent e) {
        return new Position(getXIndex(e), getYIndex(e));
    }

    private int getXIndex(MouseEvent e) {
        final double xStep = e.getComponent().getWidth() / board.getXSize();
        return (int) (e.getX() / xStep + 0.25D);
    }

    private int getYIndex(MouseEvent e) {
        final double yStep = e.getComponent().getHeight() / board.getYSize();
        return (int) (e.getY() / yStep + 0.25D);
    }

    boolean noRemainingGoats() {
        for (int i = 0; i < TOTAL_GOATS; i++) {
            if (remainingGoat[i]) {
                return false;
            }
        }
        return true;
    }

    public void setEventHandler(EventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    private void boardChanged(MouseEvent e) {
        if (eventHandler != null) {
            eventHandler.boardChanged(e);
        }
    }

    private void goatMoveDone() {
        if (eventHandler != null) {
            eventHandler.goatMoveDone();
        }
    }

    private void goatDraggingStarted() {
        if (eventHandler != null) {
            eventHandler.goatDraggingStarted();
        }
    }

    public void drawDraggedGoat(Graphics2D g2) {
        if (dragging) {
            g2.drawImage(goat, mouseX, mouseY, null);
        }
    }

    void drawRemainingGoats(Graphics2D g2, int width, int height) {
        for (int i = 0; i < TOTAL_GOATS; i++) {
            if (remainingGoat[i]) {
                final Point p = getPoint(i, width, height);
                g2.drawImage(goat, p.x, p.y, null);
            }
        }
    }

    private Point getPoint(int i, int width, int height) {
        final int halfOfTheGoats = TOTAL_GOATS / 2;
        final int x, y;
        if (i < halfOfTheGoats) {
            x = (width - 50) + 10;
            y = 10 + i * 40;
        } else {
            x = 10 + (i - halfOfTheGoats) * 40;
            y = (height - 50) + 10;
        }
        return new Point(x,y);
    }
}
