package net.doepner.baghchal;

import java.util.ArrayList;
import java.util.List;
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

    public void generateMove(int level) {
        switch (level) {
            case 1:
                doAI_1();
                break;
            case 2:
                doAI_2();
                break;
            case 3:
            default:
                doAI_3();
                break;
        }
    }

    boolean doAI_1() {
        return tryMoveFrom(getPossibleTakes()) || tryMoveFrom(possibleMoves);
    }

    boolean doAI_2() {
        if (tryMoveFrom(getPossibleTakes())) {
            return true;
        }
        final List<Move> threatensOne = new ArrayList<>();
        final List<Move> threatensMany = new ArrayList<>();
        for (Move m : possibleMoves) {
            int t = numberOfPiecesThreatened(m);
            if (t > 0) {
                (t == 1 ? threatensOne : threatensMany).add(m);
            }
        }
        return tryMoveFrom(threatensMany) || tryMoveFrom(threatensOne) || tryMoveFrom(possibleMoves);
    }

    private int numberOfPiecesThreatened(Move m) {
        final Board b = board.copyBoard();
        b.movePiece(m);
        int r = 0;
        for (int i = 0; i < board.getXSize(); i++) {
            for (int j = 0; j < board.getYSize(); j++)
                if (b.get(i,j) == PREDATOR) {
                    // TODO: DImplify code below by looping over STEPS
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

    boolean doAI_3() {
        final List<Move> takes = getPossibleTakes();
        if (takes.isEmpty()) {
            tryStepsWhere(PREY, step -> addPossibleJump(takes, step));
        }
        return tryMoveFrom(takes) || tryMoveFrom(possibleMoves);
    }

    private List<Move> getPossibleTakes() {
        return possibleMoves.stream().filter(Move::isJump).collect(toList());
    }

    private Piece board(Position p) {
        return board.isValidPosition(p) ? board.get(p) : Piece.UNDEFINED;
    }

    private Piece board(int i, int j) {
        return board(new Position(i, j));
    }

    private boolean tryMoveFrom(List<Move> moves) {
        return !moves.isEmpty() && board.doMove(getRandomFrom(moves));
    }

    void updatePossibleMoves() {
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
        if (board.validStep(step2) && board.get(step2.p2()) == null) {
            final Move jump = new Move(step1.p1(), step2.p2());
            list.add(jump);
        }
    }

    private Move getRandomFrom(List<Move> list) {
        return list.get((int) (Math.random() * (double) list.size()));
    }

    public boolean isOver() {
        return possibleMoves.size() == 0;
    }

    public void doMoveOrEndPhase(Phases phases) {
        updatePossibleMoves();
        if (isOver()) {
            phases.setEnd();
        } else {
            generateMove(phases.getLevel());
        }
    }
}
