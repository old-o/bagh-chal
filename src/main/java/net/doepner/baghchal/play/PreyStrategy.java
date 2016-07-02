package net.doepner.baghchal.play;

import net.doepner.baghchal.model.Board;
import net.doepner.baghchal.model.Move;
import net.doepner.baghchal.model.Position;

import java.util.ArrayList;
import java.util.List;

import static net.doepner.baghchal.model.Piece.PREDATOR;
import static net.doepner.baghchal.model.Piece.PREY;

/**
 * Computer player for prey pieces
 */
public final class PreyStrategy implements Player {

    @Override
    public Move play(Board board) {
        final List<Move> predatorJumps = new ArrayList<>();
        board.addPossibleJumpsTo(predatorJumps, PREDATOR, PREY);

        final List<Move> defenseMoves = new ArrayList<>();
        for (Move possibleJump : predatorJumps) {
            final Position p2 = possibleJump.p2();
            board.forAllPositions(p1 -> {
                if (board.get(p1) == PREY) {
                    final Move move = new Move(p1, p2);
                    if (board.isValid(move, PREY)) {
                        defenseMoves.add(move);
                    }
                }
            });
        }
        final Move move = board.tryMoveFrom(defenseMoves);
        if (move != null) {
            return move;
        }
        if (board.isBorderEmpty()) {
            // TODO
            return null;
        } else {
            // 1) place in a corner if possible
            // 2) place on the board edge next to a prey if possible
            // 3) place in a position that cannot be jumped over, preferably next to other prey that can also not be jumped over
            // 3a) if there is a choice to block off an empty position (make it unreachable for predator, do it
            // 3b) if a predator can be safely blocked, then do it
            return null;
        }
    }
}
