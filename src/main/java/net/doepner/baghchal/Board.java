package net.doepner.baghchal;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static net.doepner.baghchal.Piece.INVALID;
import static net.doepner.baghchal.Piece.PREDATOR;

/**
 * The game board model
 */
class Board {

    private static final int CENTER_X_SIZE = 5;
    private static final int CENTER_Y_SIZE = 5;

    private static final int X_SIZE = 1 + CENTER_X_SIZE + 1;
    private static final int Y_SIZE = 1 + CENTER_Y_SIZE + 1;

    private final Position p1 = new Position(1, 1);
    private final Position p2 = new Position(CENTER_X_SIZE, CENTER_Y_SIZE);

    private final Piece[][] b = new Piece[X_SIZE][Y_SIZE];

    private final BoardListener listener;

    Board(BoardListener listener) {
        this.listener = listener;
    }

    /**
     * Copy constructor that will copy the grid array of the provide board instance.
     * The resulting board will support no BoardListener functionality.
     *
     * @param board An existing board instance
     */
    private Board(Board board) {
        listener = BoardListener.NONE;
        for (int x = 0; x < b.length; x++) {
            System.arraycopy(board.b[x], 0, b[x], 0, b[x].length);
        }
    }

    void doMove(Move move) {
        final Piece piece = movePiece(move);
        if (piece == PREDATOR) {
            if (move.isJump()) {
                clear(move.middle());
                listener.onPredatorTake();
            } else {
                listener.onPredatorStep();
            }
        }
    }

    private final static int[] STEPS = {-1, 0, +1};

    void tryStepsWhere(Piece requiredPiece, Consumer<Move> moveProcessor) {
        forAllPositions(p -> {
            if (get(p) == PREDATOR) {
                tryDirections(p, requiredPiece, moveProcessor);
            }
        });
    }

    private void tryDirections(Position p, Piece requiredPiece, Consumer<Move> moveProcessor) {
        for (int xStep : STEPS) {
            for (int yStep : STEPS) {
                final Position p1 = p.add(xStep, yStep);
                if (get(p1) == requiredPiece) {
                    final Move step = new Move(p, p1);
                    if (isStepAlongLine(step)) {
                        moveProcessor.accept(step);
                    }
                }
            }
        }
    }

    void addPossibleStepsTo(List<Move> moveList) {
        tryStepsWhere(null, moveList::add);
    }

    void addPossibleJumpsTo(List<Move> moveList, Piece piece) {
        tryStepsWhere(piece, step -> addPossibleJump(moveList, step));
    }

    void addPossibleJump(List<Move> list, Move step1) {
        final Move step2 = step1.repeat();
        if (isStepAlongLine(step2) && isEmpty(step2.p2())) {
            list.add(new Move(step1.p1(), step2.p2()));
        }
    }

    Piece movePiece(Move move) {
        final Piece piece = get(move.p1());
        clear(move.p1());
        set(move.p2(), piece);
        return piece;
    }

    private boolean isStepAlongLine(Move move) {
        return isValidPosition(move.p1()) && isValidPosition(move.p2())
                && move.isStep() && (move.p1().hasEvenCoordSum() || move.isOneDimensional());
    }

    private boolean isValidPosition(Position pos) {
        return pos.isGreaterOrEqualTo(p1) && pos.isLessOrEqualTo(p2);
    }

    void reset() {
        for (Piece[] pieces : b) {
            Arrays.fill(pieces, null);
        }
        set(p1, PREDATOR);
        set(p1.x(), p2.y(), PREDATOR);
        set(p2.x(), p1.y(), PREDATOR);
        set(p2, PREDATOR);
        listener.afterReset();
    }

    Board copyBoard() {
        return new Board(this);
    }

    Piece get(Position p) {
        return get(p.x(), p.y());
    }

    void set(Position p, Piece piece) {
        b[p.x()][p.y()] = piece;
    }

    void clear(Position p) {
        set(p, null);
    }

    private boolean isEmpty(Position p) {
        return get(p) == null;
    }

    Piece get(int x, int y) {
        try {
            return b[x][y];
        } catch (ArrayIndexOutOfBoundsException e) {
            return INVALID;
        }
    }

    void set(int x, int y, Piece piece) {
        b[x][y] = piece;
    }

    int getCentreXSize() {
        return CENTER_X_SIZE;
    }

    int getCentreYSize() {
        return CENTER_Y_SIZE;
    }

    private void forAllPositions(Consumer<Position> positionConsumer) {
        for (int i = 1; i <= CENTER_X_SIZE; i++) {
            for (int j = 1; j <= CENTER_Y_SIZE; j++) {
                positionConsumer.accept(new Position(i, j));
            }
        }
    }

    int getXSize() {
        return X_SIZE;
    }

    int getYSize() {
        return Y_SIZE;
    }

    boolean isValid(Move move) {
        return move.isNotStationary() && isEmpty(move.p2())
                && (isStepAlongLine(move) || (isBorderPosition(move.p1()) && !isBorderPosition(move.p2())));
    }

    private boolean isBorderPosition(Position p) {
        return p.x() == 0 || p.x() == CENTER_X_SIZE + 1
                || p.y() == 0 || p.y() == CENTER_Y_SIZE + 1;
    }
}
