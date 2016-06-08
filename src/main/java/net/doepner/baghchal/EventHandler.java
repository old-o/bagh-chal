package net.doepner.baghchal;

import java.awt.Rectangle;

/**
 * Handles game events
 */
public interface EventHandler {

    void draggingStarted();

    void dragged(Rectangle rectangle);

    void moveDone();

}
