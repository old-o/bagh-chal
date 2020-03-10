package net.doepner.baghchal.model;

import org.guppy4j.g2d.Size;

/**
 * Creates GameTable instances
 */
@FunctionalInterface
public interface GameTableFactory {

    GameTable getGameTable(Size size);

}
