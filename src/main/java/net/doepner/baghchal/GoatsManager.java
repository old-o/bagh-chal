package net.doepner.baghchal;

import static net.doepner.baghchal.Piece.GOAT;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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

    private int draggedPieceX;
    private int draggedPieceY;

    private EventHandler eventHandler;

    public GoatsManager(Image goat, Board board, Phases phases) {
        this.goat = goat;
        this.board = board;
        this.phases = phases;
    }

    void reset() {
        draggedPieceX = draggedPieceY = -1;
        for (int i = 0; i < TOTAL_GOATS; i++) {
            remainingGoat[i] = true;
        }
    }

    public void mousePressed(MouseEvent e) {
        final int width = e.getComponent().getWidth();
        final int height = e.getComponent().getHeight();

        int x = e.getX();
        int y = e.getY();

        final int xBoardEnd = width - 50;
        final int yBoardEnd = height - 50;

        if (x >= xBoardEnd + 10) {
            int i = (y - 10) / 40;
            dragAvailableGoat(i);
        } else {

            if (y >= yBoardEnd + 10) {
                int i = (x - 10) / 40 + 10;
                dragAvailableGoat(i);
            }
        }
        if (phases.isMiddle() && isPositionOnBoard(e)) {
            int i = getXIndex(e);
            int j = getYIndex(e);
            if (board.get(i, j) == GOAT) {
                draggedPieceX = i;
                draggedPieceY = j;
                board.clear(i, j);
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
            boardChanged();
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (!dragging) {
            return;
        }
        dragging = false;
        if (isPositionOnBoard(e)) {
            final int i = normalize(getXIndex(e), board.getXSize());
            final int j = normalize(getYIndex(e), board.getYSize());
            if (board.empty(i, j) && (phases.isBeginning() || board.validGoatMove(draggedPieceX, draggedPieceY, i, j))) {
                board.set(i, j, GOAT);
                if (phases.isBeginning() && noRemainingGoats()) {
                    phases.setMiddle();
                }
                if (i != draggedPieceX || j != draggedPieceY) {
                    goatMoveDone();
                }
            } else if (phases.isBeginning()) {
                remainingGoat[selectedGoat] = true;
            } else {
                board.set(draggedPieceX, draggedPieceY, GOAT);
            }
        } else {
            if (phases.isBeginning()) {
                remainingGoat[selectedGoat] = true;
            } else {
                board.set(draggedPieceX, draggedPieceY, GOAT);
            }
        }
        boardChanged();
    }

    private int getXIndex(MouseEvent e) {
        final double xStep = e.getComponent().getWidth() / board.getXSize();
        return (int) (e.getX() / xStep + 0.25D);
    }

    private int getYIndex(MouseEvent e) {
        final double yStep = e.getComponent().getHeight() / board.getYSize();
        return (int) (e.getY() / yStep + 0.25D);
    }

    private int normalize(int i, int max) {
        return i < 0 ? 0 : i >= max ? max : i;
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

    private void boardChanged() {
        if (eventHandler != null) {
            eventHandler.boardChanged();
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
        for (int i = 0; i < TOTAL_GOATS / 2; i++)
            if (remainingGoat[i])
                g2.drawImage(goat, (width - 50) + 10, 10 + i * 40, null);

        for (int i = 0; i < TOTAL_GOATS - (TOTAL_GOATS / 2); i++)
            if (remainingGoat[i + 10])
                g2.drawImage(goat, 10 + i * 40, (height - 50) + 10, null);

    }
}
