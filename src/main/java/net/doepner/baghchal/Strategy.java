package net.doepner.baghchal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toList;
import static net.doepner.baghchal.Piece.PREDATOR;
import static net.doepner.baghchal.Piece.PREY;

/**
 * The computer player's strategy (playing the tigers)
 */
public class Strategy {

    private static final Piece NONE = null;
    private final List<Move> possibleMoves = new ArrayList<>();

    private final Board board;

    public Strategy(Board board) {
        this.board = board;
    }

    public boolean doMoveOrEndPhase(Phases phases) {
        updatePossibleMoves();
        if (isOver()) {
            phases.setEnd();
            return false;
        } else {
            return tryMoveFrom(getPossibleTakes())
                    || tryThreateningMove(phases.getLevel())
                    || tryMoveFrom(possibleMoves);
        }
    }

    private List<Move> getPossibleTakes() {
        return possibleMoves.stream().filter(Move::isJump).collect(toList());
    }

    private Piece board(Position p) {
        return board.isValidPosition(p) ? board.get(p) : Piece.UNDEFINED;
    }

    private boolean tryMoveFrom(List<Move> moves) {
        return !moves.isEmpty() && board.doMove(getRandomFrom(moves));
    }

    private void updatePossibleMoves() {
        possibleMoves.clear();
        tryStepsWhere(NONE, possibleMoves::add);
        tryStepsWhere(PREY, step -> addPossibleJump(possibleMoves, step));
    }

    private void tryStepsWhere(Piece requiredPiece, Consumer<Move> moveProcessor) {
        board.forAllPositions(p -> {
            if (board.get(p) == PREDATOR) {
                tryDirections(p, requiredPiece, moveProcessor);
            }
        });
    }

    private final static int[] STEPS = {-1, 0, +1};

    private void tryDirections(Position p, Piece requiredPiece, Consumer<Move> moveProcessor) {
        for (int xStep : STEPS) {
            for (int yStep : STEPS) {
                final Position p1 = p.add(xStep, yStep);
                if (board(p1) == requiredPiece) {
                    final Move step1 = new Move(p, p1);
                    if (board.validStep(step1)) {
                        moveProcessor.accept(step1);
                    }
                }
            }
        }
    }

    private void addPossibleJump(List<Move> list, Move step1) {
        final Move step2 = step1.repeat();
        if (board.validStep(step2) && board.isEmpty(step2.p2())) {
            final Move jump = new Move(step1.p1(), step2.p2());
            list.add(jump);
        }
    }

    private Move getRandomFrom(List<Move> list) {
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }

    private boolean isOver() {
        return possibleMoves.size() == 0;
    }

    private boolean tryThreateningMove(int level) {
        if (level > 2) {
            final Map<Integer, List<Move>> threateningMoves = getThreateningMoves();
            final int tryToThreaten = level - 1;
            for (int i = tryToThreaten; i > 0; i--) {
                if (tryMoveFrom(threateningMoves.get(tryToThreaten))) {
                    return true;
                }
            }
        }
        return false;
    }

    private Map<Integer, List<Move>> getThreateningMoves() {
        final Map<Integer, List<Move>> threateningMoves;
        threateningMoves = new HashMap<>();
        for (int i = 1; i < 8; i++) {
            threateningMoves.put(i, new ArrayList<>());
        }
        for (Move m : possibleMoves) {
            int npt = numberOfPiecesThreatened(m);
            if (npt > 0) {
                threateningMoves.get(npt).add(m);
            }
        }
        return threateningMoves;
    }

    private int numberOfPiecesThreatened(Move m) {
        final Board b = board.copyBoard();
        b.movePiece(m);
        int r = 0;
        for (int i = 0; i < board.getXSize(); i++) {
            for (int j = 0; j < board.getYSize(); j++)
                if (b.get(i,j) == PREDATOR) {
                    // TODO: Simplify code below by looping over STEPS
                    if (i > 1) {
                        if (j > 1 && b.get(i - 1,j - 1) == PREY && b.get(i - 2,j - 2) == null) {
                            r++;
                        }
                        if (b.get(i - 1,j) == PREY && b.get(i - 2,j) == null) {
                            r++;
                        }
                        if (j < 3 && b.get(i - 1,j + 1) == PREY && b.get(i - 2,j + 2) == null) {
                            r++;
                        }
                    }
                    if (j < 3 && b.get(i,j + 1) == PREY && b.get(i,j + 2) == null) {
                        r++;
                    }
                    if (i < 3) {
                        if (j < 3 && b.get(i + 1,j + 1) == PREY && b.get(i + 2,j + 2) == null) {
                            r++;
                        }
                        if (b.get(i + 1,j) == PREY && b.get(i + 2,j) == null) {
                            r++;
                        }
                        if (j > 1 && b.get(i + 1,j - 1) == PREY && b.get(i + 2,j - 2) == null) {
                            r++;
                        }
                    }
                    if (j > 1 && b.get(i,j - 1) == PREY && b.get(i,j - 2) == null) {
                        r++;
                    }
                }
        }
        return r;
    }
}
