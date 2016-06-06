package net.doepner.baghchal;

import java.awt.event.MouseEvent;

/**
 * Handles game events
 */
public interface EventHandler {

    void draggingStarted();

    void dragged(MouseEvent e);

    void moveDone();

}
