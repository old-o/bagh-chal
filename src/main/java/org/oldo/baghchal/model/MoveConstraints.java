package org.oldo.baghchal.model;

import static org.guppy4j.Booleans.not;

/**
 * Constrains the validity of moves on a given game table
 */
@FunctionalInterface
public interface MoveConstraints {

    boolean isValid(Move move, GameTable gameTable);

    default boolean isStepOrJumpOverOtherPiece(Move move, GameTable gameTable) {
        final Piece otherPiece = gameTable.get(move.middle());
        final boolean jumpOverOther = otherPiece != null && not(otherPiece.equals(this));
        return gameTable.isStepAlongLine(move) || (move.isJump() && jumpOverOther);
    }

    default boolean isPlacementOrStep(Move move, GameTable gameTable) {
        return gameTable.getPositions().isBorderToBoard(move)
                || (gameTable.isBorderEmpty() && gameTable.isStepAlongLine(move));
    }

}
