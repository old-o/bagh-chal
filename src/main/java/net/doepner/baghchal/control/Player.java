package net.doepner.baghchal.control;

import net.doepner.baghchal.model.GameTable;
import net.doepner.baghchal.model.Move;

/**
 * Player interface
 */
public interface Player {

    Move play(GameTable gameTable);

    boolean isComputer();

}
