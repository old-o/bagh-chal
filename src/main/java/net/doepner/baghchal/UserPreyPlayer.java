package net.doepner.baghchal;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Lets the user play the prey pieces
 */
public final class UserPreyPlayer implements Player {

    private final BoardPanel boardPanel;
    private final Images images;

    public UserPreyPlayer(BoardPanel boardPanel, Images images) {
        this.boardPanel = boardPanel;
        this.images = images;
    }

    private static class Result {
        public Move move;
    }

    @Override
    public Move play(Board board) {
        final Result result = new Result();
        final BufferedImage preyImage = images.getImage("prey.png");
        final PreyDragAndDrop preyDragAndDrop = new PreyDragAndDrop(board, boardPanel, preyImage,
                move -> {
                    synchronized (result) {
                        result.move = move;
                        result.notify();
                    }
                });
        boardPanel.addMouseMotionListener(preyDragAndDrop);
        boardPanel.addMouseListener(preyDragAndDrop);
        try {
            synchronized (result) {
                while (result.move == null) {
                    result.wait();
                }
            }
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
        boardPanel.removeMouseListener(preyDragAndDrop);
        boardPanel.removeMouseMotionListener(preyDragAndDrop);
        return result.move;
    }
}
