package net.doepner.baghchal.play;

import net.doepner.baghchal.model.Board;
import net.doepner.baghchal.model.Move;

/**
 * Player interface
 */
public interface Player {

    Move play(Board board);

}
