package org.oldo.baghchal.model;

/**
 * Possible control pieces with their move-ability constraints
 */
public enum Piece implements MoveConstraints {

    PREY('O') {
        @Override
        public boolean isValid(Move move, GameTable gameTable) {
            return gameTable.getPositions().isBorderToBoard(move)
                    || (gameTable.isBorderEmpty() && gameTable.isStepAlongLine(move));
        }
    },
    PREDATOR('X') {
        @Override
        public boolean isValid(Move move, GameTable gameTable) {
            return gameTable.isStepAlongLine(move)
                    || (move.isJump() && (gameTable.get(move.middle()) == PREY));
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
