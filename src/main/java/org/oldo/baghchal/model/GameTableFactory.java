package org.oldo.baghchal.model;

import org.oldo.g2d.Size;

/**
 * Creates GameTable instances
 */
@FunctionalInterface
public interface GameTableFactory {

    GameTable getGameTable(Size size);

}
