package net.doepner.baghchal.play;

import static net.doepner.baghchal.model.Piece.PREDATOR;
import static net.doepner.baghchal.model.Piece.PREY;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.doepner.baghchal.model.Board;
import net.doepner.baghchal.model.Levels;
import net.doepner.baghchal.model.Move;

/**
 * The computer player's strategy (playing the tigers)
 */
public class PredatorStrategy implements Player {

    private final Levels levels;

    public PredatorStrategy(Levels levels) {
        this.levels = levels;
    }

    @Override
    public String getName() {
        return "Predator AI";
    }

    @Override
    public Move play(Board board) {
        final Move take = board.tryMoveFrom(board.getPossibleJumps(PREDATOR, PREY));
        if (take != null) {
            return take;
        }
        final List<Move> possibleSteps = board.getPossibleSteps(PREDATOR);
        if (possibleSteps.isEmpty()) {
            return null;
        }
        final Move threateningMove = tryThreateningMove(levels.getLevel(), possibleSteps, board);
        return threateningMove != null ? threateningMove : board.tryMoveFrom(possibleSteps);
    }

    private static Move tryThreateningMove(int level, Iterable<Move> possibleMoves, Board board) {
        final Map<Integer, List<Move>> threateningMoves = getThreateningMoves(possibleMoves, board);
        for (int i = level; i > 0; i--) {
            final Move move = board.tryMoveFrom(threateningMoves.get(i));
            if (move != null) {
                return move;
            }
        }
        return null;
    }

    private static Map<Integer, List<Move>> getThreateningMoves(Iterable<Move> possibleMoves, Board board) {
        final Map<Integer, List<Move>> threateningMoves = new HashMap<>();
        for (int i = 1; i <= 8; i++) {
            threateningMoves.put(i, new ArrayList<>());
        }
        for (Move m : possibleMoves) {
            int npt = numberOfPiecesThreatened(m, board);
            if (npt > 0) {
                threateningMoves.get(npt).add(m);
            }
        }
        return threateningMoves;
    }

    private static int numberOfPiecesThreatened(Move m, Board board) {
        final Board b = board.copyBoard();
        b.movePiece(m);
        final List<Move> jumps = new ArrayList<>();
        for (Move step : b.getStepsWhere(PREDATOR, PREY)) {
            b.addPossibleJump(jumps, step);
        }
        return jumps.size();
    }
}
