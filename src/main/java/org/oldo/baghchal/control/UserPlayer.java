package org.oldo.baghchal.control;

import org.oldo.baghchal.model.GameTable;
import org.oldo.baghchal.model.Move;
import org.oldo.baghchal.model.Piece;
import org.oldo.baghchal.view.DragAndDropHandler;
import org.oldo.baghchal.view.GameView;

import java.awt.event.MouseAdapter;

/**
 * Lets the user control pieces
 */
public final class UserPlayer implements Player {

    private final GameView view;
    private final Piece piece;

    public UserPlayer(Piece piece, GameView view) {
        this.view = view;
        this.piece = piece;
    }

    @Override
    public boolean isComputer() {
        return false;
    }

    @Override
    public Move play(GameTable gameTable) {
        final Result result = new Result();

        final MouseAdapter dndHandler = new DragAndDropHandler(gameTable, view, move -> done(result, move), piece);
        gameTable.addDiscardListener(() -> done(result, null));

        view.addMouseAdapter(dndHandler);
        try {
            synchronized (result) {
                result.wait();
            }
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
        view.removeMouseAdapter(dndHandler);

        if (result.move == null) {
            throw new PlayerInterruptedException("Game table changed during player's turn!");
        } else {
            return result.move;
        }
    }

    private static void done(Result result, Move move) {
        synchronized (result) {
            result.move = move;
            result.notifyAll();
        }
    }

    private static class Result {
        Move move;
    }
}
