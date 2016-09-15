package net.doepner.baghchal.model;

/**
 * Possible control pieces with their move-ability constraints
 */
public enum Piece implements MoveConstraints {

    PREY {
        @Override
        public boolean isValid(Move move, GameTable gameTable) {
            return gameTable.isBorderToBoard(move) || gameTable.isValidOnBoardStep(move);
        }
    },
    PREDATOR {
        @Override
        public boolean isValid(Move move, GameTable gameTable) {
            return gameTable.isStepAlongLine(move) || (move.isJump() && gameTable.get(move.middle()) == Piece.PREY);
        }
    },
    INVALID {
        @Override
        public boolean isValid(Move move, GameTable gameTable) {
            return false;
        }
    }

}
