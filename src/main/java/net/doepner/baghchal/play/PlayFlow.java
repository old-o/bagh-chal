package net.doepner.baghchal.play;

import net.doepner.baghchal.model.Move;

/**
 * Turn-taking when player is done
 */
public interface PlayFlow {

    void moveDone(Move move);

}
