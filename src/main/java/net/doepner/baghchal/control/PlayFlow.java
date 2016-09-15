package net.doepner.baghchal.control;

import net.doepner.baghchal.model.Move;

/**
 * Turn-taking when player is done
 */
public interface PlayFlow {

    void moveDone(Move move);

}
