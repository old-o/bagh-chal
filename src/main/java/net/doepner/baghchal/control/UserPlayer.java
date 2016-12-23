package net.doepner.baghchal.control;

import net.doepner.baghchal.model.GameTable;
import net.doepner.baghchal.model.Move;
import net.doepner.baghchal.model.Piece;
import net.doepner.baghchal.theming.Images;
import net.doepner.baghchal.view.DragAndDropHandler;
import net.doepner.baghchal.view.GamePanel;

import java.awt.event.MouseAdapter;

/**
 * Lets the user control the prey pieces
 */
public final class UserPlayer implements Player {

    private final GamePanel gamePanel;
    private final Images images;
    private final Piece piece;

    public UserPlayer(Piece piece, GamePanel gamePanel, Images images) {
        this.gamePanel = gamePanel;
        this.images = images;
        this.piece = piece;
    }

    @Override
    public Move play(GameTable gameTable) {
        final Result result = new Result();

        final MouseAdapter dndHandler = new DragAndDropHandler(piece, gameTable, gamePanel, images,
                move -> done(result, move));

        gameTable.addDiscardListener(() -> done(result, null));

        gamePanel.addMouseAdapter(dndHandler);
        try {
            synchronized (result) {
                result.wait();
            }
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
        gamePanel.removeMouseAdapter(dndHandler);

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
