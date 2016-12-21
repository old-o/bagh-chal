package net.doepner.baghchal.model;

/**
 * Creates GameTable instances
 */
public interface GameTableFactory {

    GameTable getGameTable(int xSize, int ySize);

}
