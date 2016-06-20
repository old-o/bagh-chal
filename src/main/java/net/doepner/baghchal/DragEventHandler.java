package net.doepner.baghchal;

import java.awt.Point;

/**
 * Handles drag events
 */
interface DragEventHandler {

    void draggingStarted(Point point);

    void draggedAt(Point point);

    void releasedAt(Point point);

}
