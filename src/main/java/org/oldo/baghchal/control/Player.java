package org.oldo.baghchal.control;

import org.oldo.baghchal.model.GameTable;
import org.oldo.baghchal.model.Move;

/**
 * Player interface
 */
public interface Player {

    Move play(GameTable gameTable);

    boolean isComputer();

}
