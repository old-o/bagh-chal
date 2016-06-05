package net.doepner.baghchal;

import java.awt.event.MouseEvent;

/**
 * Handles game events
 */
public interface EventHandler {

    void goatDraggingStarted();

    void goatDragged(MouseEvent e);

    void goatMoveDone();

}
