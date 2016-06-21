package net.doepner.baghchal;

import static java.util.stream.Collectors.toList;
import static net.doepner.baghchal.Piece.PREDATOR;
import static net.doepner.baghchal.Piece.PREY;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The computer player's strategy (playing the tigers)
 */
public class PredatorStrategy implements Player {

    private final Levels levels;

    public PredatorStrategy(Levels levels) {
        this.levels = levels;
    }

    @Override
    public Move play(Board board) {
        final int level = levels.getLevel();
        final List<Move> possibleMoves = new ArrayList<>();
        board.addPossibleStepsTo(possibleMoves, PREDATOR);
        board.addPossibleJumpsTo(possibleMoves, PREDATOR, PREY);
        if (possibleMoves.isEmpty()) {
            return null;
        }
        final Move take = tryMoveFrom(allTakesIn(possibleMoves), board);
        if (take != null) {
            return take;
        }
        final Move threateningMove = tryThreateningMove(level, possibleMoves, board);
        if (threateningMove != null) {
            return threateningMove;
        }
        return tryMoveFrom(possibleMoves, board);
    }

    private static Move tryMoveFrom(List<Move> moves, Board board) {
        if (moves.isEmpty()) {
            return null;
        } else {
            final Move move = getRandomFrom(moves);
            board.doMove(move);
            return move;
        }
    }

    private static Move getRandomFrom(List<Move> list) {
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }

    private static List<Move> allTakesIn(List<Move> moveList) {
        return moveList.stream().filter(Move::isJump).collect(toList());
    }

    private static Move tryThreateningMove(int level, Iterable<Move> possibleMoves, Board board) {
        if (level > 1) {
            final int tryToThreaten = level - 1;
            final Map<Integer, List<Move>> threateningMoves = getThreateningMoves(possibleMoves, board);
            for (int i = tryToThreaten; i > 0; i--) {
                final Move move = tryMoveFrom(threateningMoves.get(tryToThreaten), board);
                if (move != null) {
                    return move;
                }
            }
        }
        return null;
    }

    private static Map<Integer, List<Move>> getThreateningMoves(Iterable<Move> possibleMoves, Board board) {
        final Map<Integer, List<Move>> threateningMoves = new HashMap<>();
        for (int i = 1; i < 8; i++) {
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
        b.tryStepsWhere(PREDATOR, PREY, step -> b.addPossibleJump(jumps, step));
        return jumps.size();
    }
}
