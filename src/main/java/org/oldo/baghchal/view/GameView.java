package org.oldo.baghchal.view;

import org.guppy4j.run.Startable;
import org.oldo.baghchal.model.GameTable;
import org.oldo.baghchal.model.Piece;
import org.oldo.baghchal.model.Position;
import org.oldo.g2d.Size;

import java.awt.*;
import java.awt.event.MouseAdapter;

/**
 * View interface
 */
public interface GameView extends Startable {

    void addMouseAdapter(MouseAdapter adapter);

    void removeMouseAdapter(MouseAdapter dndHandler);

    Position getPosition(Point p);

    Point getPoint(Position p);

    Position getMaxPosition(Size screenSize);

    void setBoardSize(Size size);

    Size getScreenSize();

    GameTable getGameTable();

    void applyThemeChange();

    void setLastDragPoint(Point p);

    void notifyDraggedTo(Point point, Piece piece);

    default void notifyDraggedTo(Position p, Piece piece) {
        notifyDraggedTo(getPoint(p), piece);
    }

    void repaint();

    default <T> T as(Class<T> type) {
        return type.cast(this);
    }

}
