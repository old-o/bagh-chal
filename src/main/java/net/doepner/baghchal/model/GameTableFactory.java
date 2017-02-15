package net.doepner.baghchal.model;

/**
 * Creates GameTable instances
 */
@FunctionalInterface
public interface GameTableFactory {

    GameTable getGameTable(int xSize, int ySize);

}
