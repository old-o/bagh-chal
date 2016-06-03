package net.doepner.baghchal;

import java.awt.event.MouseEvent;

/**
 * Handles game events
 */
public interface EventHandler {

    void boardChanged(MouseEvent e);

    void goatMoveDone();

    void goatDraggingStarted();
}
