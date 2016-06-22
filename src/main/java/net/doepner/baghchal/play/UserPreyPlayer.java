package net.doepner.baghchal.play;

import net.doepner.baghchal.model.Board;
import net.doepner.baghchal.model.Move;
import net.doepner.baghchal.resources.Images;
import net.doepner.baghchal.ui.BoardPanel;
import net.doepner.baghchal.ui.PreyDragAndDrop;

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
        final PreyDragAndDrop preyDnd = new PreyDragAndDrop(board, boardPanel, images,
                move -> {
                    synchronized (result) {
                        result.move = move;
                        result.notify();
                    }
                });
        boardPanel.addMouseMotionListener(preyDnd);
        boardPanel.addMouseListener(preyDnd);
        try {
            synchronized (result) {
                while (result.move == null) {
                    result.wait();
                }
            }
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
        boardPanel.removeMouseListener(preyDnd);
        boardPanel.removeMouseMotionListener(preyDnd);
        return result.move;
    }
}
