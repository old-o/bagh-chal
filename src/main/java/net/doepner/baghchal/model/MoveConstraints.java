package net.doepner.baghchal.model;

/**
 * Constrains the validity of moves on a given game table
 */
@FunctionalInterface
public interface MoveConstraints {

    boolean isValid(Move move, GameTable gameTable);

}
