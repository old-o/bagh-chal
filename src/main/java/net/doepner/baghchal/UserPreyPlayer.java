package net.doepner.baghchal;

import java.awt.Component;

/**
 * Lets the user play the prey pieces
 */
public final class UserPreyPlayer implements Player {

    private final DragImageSupport dragImageSupport;
    private final Component ui;

    public UserPreyPlayer(DragImageSupport dragImageSupport, Component ui) {
        this.dragImageSupport = dragImageSupport;
        this.ui = ui;
    }

    @Override
    public Move play(Board board) {
        final Move[] result = new Move[1];

        final PreyDragAndDrop preyDragAndDrop = new PreyDragAndDrop(board, move -> {
            synchronized (result) {
                result[0] = move;
                result.notify();
            }
        });
        preyDragAndDrop.setDragEventHandler(dragImageSupport);
        ui.addMouseMotionListener(preyDragAndDrop);
        ui.addMouseListener(preyDragAndDrop);
        try {
            synchronized (result) {
                while (result[0] == null) {
                    result.wait();
                }
            }
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
        ui.removeMouseListener(preyDragAndDrop);
        ui.removeMouseMotionListener(preyDragAndDrop);
        return result[0];
    }
}
