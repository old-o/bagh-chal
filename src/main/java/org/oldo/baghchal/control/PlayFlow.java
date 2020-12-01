package org.oldo.baghchal.control;

import org.oldo.baghchal.model.Move;

/**
 * Turn-taking when player is done
 */
@FunctionalInterface
public interface PlayFlow {

    void moveDone(Move move);

}
