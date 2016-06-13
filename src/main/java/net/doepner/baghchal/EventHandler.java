package net.doepner.baghchal;

import java.awt.Rectangle;

/**
 * Handles game events
 */
public interface EventHandler {

    void draggingStarted();

    void repaintRectangleAt(Rectangle rectangle);

    void moveDone();

}
