package net.doepner.baghchal.control;

import java.awt.event.MouseAdapter;

import net.doepner.baghchal.model.GameTable;
import net.doepner.baghchal.model.Move;
import net.doepner.baghchal.model.Piece;
import net.doepner.baghchal.resources.Images;
import net.doepner.baghchal.view.GamePanel;
import net.doepner.baghchal.view.DragAndDropHandler;

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
                move -> {
                    synchronized (result) {
                        result.move = move;
                        result.notifyAll();
                    }
                });
        gamePanel.addMouseAdapter(dndHandler);
        try {
            synchronized (result) {
                while (result.move == null) {
                    result.wait();
                }
            }
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
        gamePanel.removeMouseAdapter(dndHandler);
        return result.move;
    }

    private static class Result {
        Move move;
    }
}
