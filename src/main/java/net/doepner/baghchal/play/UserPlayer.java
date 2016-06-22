package net.doepner.baghchal.play;

import net.doepner.baghchal.model.Board;
import net.doepner.baghchal.model.Move;
import net.doepner.baghchal.model.Piece;
import net.doepner.baghchal.resources.Images;
import net.doepner.baghchal.ui.BoardPanel;
import net.doepner.baghchal.ui.DragAndDropHandler;

/**
 * Lets the user play the prey pieces
 */
public final class UserPlayer implements Player {

    private final BoardPanel boardPanel;
    private final Images images;
    private final Piece piece;

    public UserPlayer(Piece piece, BoardPanel boardPanel, Images images) {
        this.boardPanel = boardPanel;
        this.images = images;
        this.piece = piece;
    }

    private static class Result {
        public Move move;
    }

    @Override
    public Move play(Board board) {
        final Result result = new Result();
        final DragAndDropHandler dndHandler = new DragAndDropHandler(piece, board, boardPanel, images,
                move -> {
                    synchronized (result) {
                        result.move = move;
                        result.notify();
                    }
                });
        boardPanel.addMouseAdapter(dndHandler);
        try {
            synchronized (result) {
                while (result.move == null) {
                    result.wait();
                }
            }
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
        boardPanel.removeMouseAdapter(dndHandler);
        return result.move;
    }
}
