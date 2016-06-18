package net.doepner.baghchal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.stream.Collectors.toList;
import static net.doepner.baghchal.Piece.PREY;

/**
 * The computer player's strategy (playing the tigers)
 */
public class Strategy {

    boolean doMoveOrEndPhase(Board board, int level) {
        final List<Move> possibleMoves = new ArrayList<>();
        board.addPossibleStepsTo(possibleMoves);
        board.addPossibleJumpsTo(possibleMoves, PREY);
        return possibleMoves.isEmpty() || !(
                tryMoveFrom(allTakesIn(possibleMoves), board)
                        || tryThreateningMove(level, possibleMoves, board)
                        || tryMoveFrom(possibleMoves, board));
    }

    private static boolean tryMoveFrom(List<Move> moves, Board board) {
        return !moves.isEmpty() && board.doMove(getRandomFrom(moves));
    }

    private static Move getRandomFrom(List<Move> list) {
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }

    private static List<Move> allTakesIn(List<Move> moveList) {
        return moveList.stream().filter(Move::isJump).collect(toList());
    }

    private static boolean tryThreateningMove(int level, Iterable<Move> possibleMoves, Board board) {
        if (level > 1) {
            final int tryToThreaten = level - 1;
            final Map<Integer, List<Move>> threateningMoves = getThreateningMoves(possibleMoves, board);
            for (int i = tryToThreaten; i > 0; i--) {
                if (tryMoveFrom(threateningMoves.get(tryToThreaten), board)) {
                    return true;
                }
            }
        }
        return false;
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
        b.tryStepsWhere(PREY, step -> b.addPossibleJump(jumps, step));
        return jumps.size();
    }
}
