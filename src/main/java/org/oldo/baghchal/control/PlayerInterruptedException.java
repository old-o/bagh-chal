package org.oldo.baghchal.control;

/**
 * Indicate that a player was interrupted
 */
public class PlayerInterruptedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    PlayerInterruptedException(String s) {
        super(s);
    }

    // no detailed behavior

}
