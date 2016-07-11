package net.doepner.baghchal.play;

import net.doepner.baghchal.model.GameTable;
import net.doepner.baghchal.model.Move;

/**
 * Player interface
 */
public interface Player {

    String getName();

    Move play(GameTable gameTable);

}
