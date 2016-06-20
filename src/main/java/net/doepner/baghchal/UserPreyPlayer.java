package net.doepner.baghchal;

import java.awt.Point;
import java.awt.image.BufferedImage;

/**
 * Lets the user play the prey pieces
 */
public final class UserPreyPlayer implements Player {

    private final Images images;
    private final UI ui;
    private final Sound sound;

    public UserPreyPlayer(Images images, UI ui, Sound sound) {
        this.images = images;
        this.ui = ui;
        this.sound = sound;
    }

    @Override
    public Move play(Board board) {
        final Move[] result = new Move[1];

        final PreyManager preyManager = new PreyManager(board);
        preyManager.setEventHandler(
                new EventHandler() {

                    private Point dragStartPoint;

                    @Override
                    public void draggingStarted(Point point) {
                        dragStartPoint = point;
                        sound.playPrey();
                        ui.setLastDragPoint(point);
                    }

                    @Override
                    public void draggedAt(Point p) {
                        final BufferedImage img = getPreyImage();
                        p.translate(-img.getWidth(null) / 2, -img.getHeight(null) / 2);
                        ui.draggedAt(p, img);
                    }

                    @Override
                    public void moveDone(Move move) {
                        synchronized (result) {
                            result[0] = move;
                            result.notify();
                        }
                    }

                    @Override
                    public void releasedAt(Point point) {
                        final BufferedImage img = getPreyImage();
                        ui.draggedAt(point, img);
                        ui.draggedAt(dragStartPoint, img);
                        ui.clearLastDragPoint();
                        dragStartPoint = null;
                    }
                }
        );
        ui.addMouseMotionListener(preyManager);
        ui.addMouseListener(preyManager);
        try {
            synchronized (result) {
                while (result[0] == null) {
                    result.wait();
                }
            }
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
        ui.removeMouseListener(preyManager);
        ui.removeMouseMotionListener(preyManager);
        return result[0];
    }

    private BufferedImage getPreyImage() {
        return images.getImage("prey.png");
    }

}
