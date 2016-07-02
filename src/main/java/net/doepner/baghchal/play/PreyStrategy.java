package net.doepner.baghchal.play;

import net.doepner.baghchal.model.Board;
import net.doepner.baghchal.model.Move;
import net.doepner.baghchal.model.Piece;
import net.doepner.baghchal.model.Position;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static net.doepner.baghchal.model.Piece.PREDATOR;
import static net.doepner.baghchal.model.Piece.PREY;
import static net.doepner.baghchal.util.ListUtil.getRandomFrom;

/**
 * Computer player for prey pieces
 */
public final class PreyStrategy implements Player {

    @Override
    public Move play(Board board) {
        final Move defensiveMove = board.tryMoveFrom(getDefensiveMoves(board));
        if (defensiveMove != null) {
            return defensiveMove;
        }
        final Position borderPosition = board.getBorderPosition(PREY);
        final Position boardPosition = getSafeBoardPosition(board);
        if (borderPosition != null && boardPosition != null) {
            final Move m = new Move(borderPosition, boardPosition);
            board.movePiece(m);
            return m;
        }

        // 2) place on the board edge next to a prey if possible
        // 3) place in a position that cannot be jumped over, preferably next to other prey that can also not be jumped over
        // 3a) if there is a choice to block off an empty position (make it unreachable for predator, do it
        // 3b) if a predator can be safely blocked, then do it

        return null;
    }

    private Position getSafeBoardPosition(Board board) {
        final Set<Position> positions = new HashSet<>();
        board.forAllBoardPositions(p1 -> {
            if (board.isEmpty(p1)) {
                positions.add(p1);
                board.tryDirections(p1, m -> {
                    if (positions.contains(p1)) {
                        final Position p3 = p1.add(-m.xStep(), -m.yStep());
                        if (board.isValidOnBoardPosition(p3)) {
                            final Piece piece2 = board.get(m.p2());
                            final Piece piece3 = board.get(p3);
                            if ((piece2 == null || piece2 == PREDATOR)
                                    && (piece3 == null || piece3 == PREDATOR)) {
                                positions.remove(p1);
                                System.out.println("Removed " + p1);
                            }
                        }
                    }
                });
            }
        });
        return getRandomFrom(new ArrayList<>(positions));
    }

    private List<Move> getDefensiveMoves(Board board) {
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
        return defenseMoves;
    }
}
