package net.doepner.baghchal;

import java.awt.Point;

/**
 * Handles game events
 */
interface EventHandler {

    void draggingStarted(Point point);

    void draggedAt(Point point);

    void releasedAt(Point point);

    void moveDone(Move move);
}
