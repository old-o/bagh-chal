package net.doepner.baghchal;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static net.doepner.baghchal.Piece.PREDATOR;
import static net.doepner.baghchal.Piece.PREY;

/**
 * The game board model
 */
public class Board {

    private static final int X_SIZE = 5;
    private static final int Y_SIZE = 5;

    private final Position p1 = new Position(0, 0);
    private final Position p2 = new Position(X_SIZE, Y_SIZE);

    private final Piece[][] grid = new Piece[X_SIZE][Y_SIZE];

    private final BoardListener listener;

    public Board(BoardListener listener) {
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
        for (int x = 0; x < X_SIZE; x++) {
            System.arraycopy(board.grid[x], 0, grid[x], 0, Y_SIZE);
        }
    }

    boolean doMove(Move move) {
        final Piece piece = movePiece(move);
        if (piece == PREDATOR) {
            if (move.isJump()) {
                clear(move.middle());
                listener.onPredatorTake();
            } else {
                listener.onPredatorStep();
            }
        }
        return true;
    }

    private final static int[] STEPS = {-1, 0, +1};

    public void tryStepsWhere(Piece requiredPiece, Consumer<Move> moveProcessor) {
        forAllPositions(p -> {
            if (get(p) == PREDATOR) {
                tryDirections(p, requiredPiece, moveProcessor);
            }
        });
    }

    public void tryDirections(Position p, Piece requiredPiece, Consumer<Move> moveProcessor) {
        for (int xStep : STEPS) {
            for (int yStep : STEPS) {
                final Position p1 = p.add(xStep, yStep);
                final Move step1 = new Move(p, p1);
                if (validStep(step1) && get(p1) == requiredPiece) {
                    moveProcessor.accept(step1);
                }
            }
        }
    }

    public void addPossibleStepsTo(List<Move> moveList) {
        tryStepsWhere(null, moveList::add);
    }

    public void addPossibleJumpsTo(List<Move> moveList) {
        tryStepsWhere(PREY, step -> addPossibleJump(moveList, step));
    }

    public void addPossibleJump(List<Move> list, Move step1) {
        final Move step2 = step1.repeat();
        if (validStep(step2) && isEmpty(step2.p2())) {
            list.add(new Move(step1.p1(), step2.p2()));
        }
    }

    public Piece movePiece(Move move) {
        final Piece piece = get(move.p1());
        clear(move.p1());
        set(move.p2(), piece);
        return piece;
    }

    boolean validStep(Move move) {
        return isValidPosition(move.p1()) && isValidPosition(move.p2())
                && move.isStep() && (move.p1().hasEvenCoordSum() || move.isOneDimensional());
    }

    boolean isValidPosition(Position pos) {
        return pos.isGreaterOrEqualTo(p1) && pos.isLessThan(p2);
    }

    void reset() {
        for (int x = 0; x < X_SIZE; x++) {
            Arrays.fill(grid[x], null);
        }
        set(0, 0, PREDATOR);
        set(X_SIZE - 1, 0, PREDATOR);
        set(0, Y_SIZE - 1, PREDATOR);
        set(X_SIZE - 1, Y_SIZE - 1, PREDATOR);
        listener.afterReset();
    }

    public Board copyBoard() {
        return new Board(this);
    }

    public Piece get(Position p) {
        return get(p.x(), p.y());
    }

    public void set(Position p, Piece piece) {
        grid[p.x()][p.y()] = piece;
    }

    void clear(Position p) {
        set(p, null);
    }

    public boolean isEmpty(Position p) {
        return get(p) == null;
    }

    protected Piece get(int x, int y) {
        return grid[x][y];
    }

    private void set(int x, int y, Piece piece) {
        grid[x][y] = piece;
    }

    public int getXSize() {
        return X_SIZE;
    }

    public int getYSize() {
        return Y_SIZE;
    }

    public Position normalize(Position p) {
        final int x = normalize(p.x(), X_SIZE);
        final int y = normalize(p.y(), Y_SIZE);
        return new Position(x, y);
    }

    private int normalize(int n, int max) {
        return n < 0 ? 0 : n >= max ? max - 1 : n;
    }

    public void forAllPositions(Consumer<Position> positionConsumer) {
        for (int i = 0; i < X_SIZE; i++) {
            for (int j = 0; j < Y_SIZE; j++) {
                positionConsumer.accept(new Position(i, j));
            }
        }
    }
}
