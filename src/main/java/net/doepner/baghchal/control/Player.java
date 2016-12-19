package net.doepner.baghchal.control;

import net.doepner.baghchal.model.GameTable;
import net.doepner.baghchal.model.Move;

/**
 * Player interface
 */
@FunctionalInterface
public interface Player {

    Move play(GameTable gameTable);

}
