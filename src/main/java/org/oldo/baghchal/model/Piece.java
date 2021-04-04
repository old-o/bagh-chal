package org.oldo.baghchal.model;

/**
 * Possible control pieces with their move-ability constraints
 */
public enum Piece implements MoveConstraints {

    PREY('⬤') {
        @Override
        public boolean isValid(Move move, GameTable gameTable) {
//            return true;
//            return isStepOrJumpOverOtherPiece(move, gameTable);
            return isPlacementOrStep(move, gameTable);
        }
    },
    PREDATOR('◯') {
        @Override
        public boolean isValid(Move move, GameTable gameTable) {
            return isStepOrJumpOverOtherPiece(move, gameTable);
        }
    };

    private final char c;

    Piece(char c) {
        this.c = c;
    }

    public char asChar() {
        return c;
    }

}
