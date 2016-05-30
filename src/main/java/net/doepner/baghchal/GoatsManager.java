package net.doepner.baghchal;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static net.doepner.baghchal.Piece.GOAT;

/**
 * Manages the goats
 */
public class GoatsManager extends MouseAdapter {

    private static final int BOARD_END = 450;
    private static final int BOARD_START = 20;
    private static final int TOTAL_GOATS = 20;

    private int selectedGoat;

    private boolean dragging = false;

    private int mouseX;
    private int mouseY;

    private int draggedPieceX;
    private int draggedPieceY;

    private final boolean remainingGoat[] = new boolean[20];

    private final Image goat;

    private final Board board;
    private final Phases phases;

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
        System.out.println("Mouse pressed: Source width: " + e.getComponent().getWidth());
        int x = e.getX();
        int y = e.getY();
        if (x >= BOARD_END + 10) {
            int i = (y - 10) / 40;
            dragAvailableGoat(i);
        } else if (y >= BOARD_END + 10) {
            int i = (x - 10) / 40 + 10;
            dragAvailableGoat(i);
        }
        if (phases.isMiddle() && isPositionOnBoard(x, y)) {
            int i = getIndex(x);
            int j = getIndex(y);
            if (board.get(i, j) == GOAT) {
                draggedPieceX = i;
                draggedPieceY = j;
                board.clear(i, j);
                dragging = true;
            }
        }
    }

    private boolean isPositionOnBoard(int x, int y) {
        return x >= BOARD_START && x <= BOARD_END && y >= BOARD_START && y <= BOARD_END;
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
        int x = e.getX();
        int y = e.getY();
        if (x < BOARD_START || y < BOARD_START || x > BOARD_END || y > BOARD_END) {
            if (phases.isBeginning()) {
                remainingGoat[selectedGoat] = true;
            } else {
                board.set(draggedPieceX, draggedPieceY, GOAT);
            }
        } else {
            int i = getIndex(x);
            int j = getIndex(y);
            i = normalize(i);
            j = normalize(j);
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
        }
        boardChanged();
    }

    private int getIndex(double pos) {
        return (int) (pos / 100D + 0.25D);
    }

    private int normalize(int i) {
        if (i < 0) {
            return  0;
        } else if (i > 4) {
            return 4;
        } else {
            return i;
        }
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

    void drawRemainingGoats(Graphics2D g2) {
        for (int i = 0; i < TOTAL_GOATS / 2; i++)
            if (remainingGoat[i])
                g2.drawImage(goat, BOARD_END + 10, 10 + i * 40, null);

        for (int i = 0; i < TOTAL_GOATS - (TOTAL_GOATS / 2); i++)
            if (remainingGoat[i + 10])
                g2.drawImage(goat, 10 + i * 40, BOARD_END + 10, null);

    }
}
