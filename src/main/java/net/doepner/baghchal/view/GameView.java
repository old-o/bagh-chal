package net.doepner.baghchal.view;

import net.doepner.baghchal.model.GameTable;
import net.doepner.baghchal.model.Position;
import org.guppy4j.g2d.Size;
import org.guppy4j.run.Startable;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;

/**
 * View interface
 */
public interface GameView extends Startable {

    void addMouseAdapter(MouseAdapter adapter);

    void removeMouseAdapter(MouseAdapter dndHandler);

    void setLastDragPoint(Point p);

    Point getLastDragPoint();

    void repaintForDrag(Rectangle rectangle, BufferedImage image);

    Position getMaxPosition(Size screenSize);

    void setBoardSize(Size size);

    void repaint();

    Size getScreenSize();

    default <T> T as(Class<T> type) {
        return type.cast(this);
    }

    GameTable getGameTable();
}
