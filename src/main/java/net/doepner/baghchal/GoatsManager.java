package net.doepner.baghchal;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Manages the goats
 */
public class GoatsManager extends MouseAdapter {

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
        for (int i = 0; i < 20; i++) {
            remainingGoat[i] = true;
        }
    }

    public void mousePressed(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        if (x >= 460) {
            int i = (y - 10) / 40;
            if (remainingGoat[i]) {
                selectedGoat = i;
                remainingGoat[i] = false;
                dragging = true;
            }
        } else if (y >= 460) {
            int i = (x - 10) / 40 + 10;
            if (remainingGoat[i]) {
                selectedGoat = i;
                remainingGoat[i] = false;
                dragging = true;
            }
        }
        if (phases.isMiddle() && x >= 20 && x <= 450 && y >= 20 && y <= 450) {
            int i = (int) ((double) x / 100D + 0.25D);
            int j = (int) ((double) y / 100D + 0.25D);
            if (board.get(i, j) == 1) {
                draggedPieceX = i;
                draggedPieceY = j;
                board.set(i, j, 0);
                dragging = true;
            }
        }
    }

    public void mouseDragged(MouseEvent e) {
        if (dragging) {
            mouseX = e.getX() - 16;
            mouseY = e.getY() - 16;
            boardChanged();
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (!dragging)
            return;
        dragging = false;
        int x = e.getX();
        int y = e.getY();
        if (x < 20 || y < 20 || x > 450 || y > 450) {
            if (phases.isBeginning()) {
                remainingGoat[selectedGoat] = true;
            } else {
                board.set(draggedPieceX, draggedPieceY, 1);
            }
        } else {
            int i = (int) ((double) x / 100D + 0.25D);
            int j = (int) ((double) y / 100D + 0.25D);
            if (i < 0)
                i = 0;
            else if (i > 4)
                i = 4;
            if (j < 0)
                j = 0;
            else if (j > 4)
                j = 4;
            if (board.get(i, j) == 0 && (phases.isBeginning() || board.validGoatMove(draggedPieceX, draggedPieceY, i, j))) {
                board.set(i, j, 1);
                if (phases.isBeginning() && noRemainingGoats()) {
                    phases.setMiddle();
                }
                if (i != draggedPieceX || j != draggedPieceY) {
                    goatMoveDone();
                }
            } else if (phases.isBeginning()) {
                remainingGoat[selectedGoat] = true;
            } else {
                board.set(draggedPieceX, draggedPieceY, 1);
            }
        }
        boardChanged();
    }

    boolean noRemainingGoats() {
        for (int i = 0; i < 20; i++) {
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

    public void drawDraggedGoat(Graphics2D g2) {
        if (dragging) {
            g2.drawImage(goat, mouseX, mouseY, null);
        }
    }

    void drawRemainingGoats(Graphics2D g2) {
        for (int i = 0; i < 10; i++)
            if (remainingGoat[i])
                g2.drawImage(goat, 460, 10 + i * 40, null);

        for (int i = 0; i < 10; i++)
            if (remainingGoat[i + 10])
                g2.drawImage(goat, 10 + i * 40, 460, null);

    }

    public Image getGoat() {
        return goat;
    }
}
