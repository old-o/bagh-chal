package net.doepner.baghchal;

import java.util.Arrays;
import java.util.function.Consumer;

import static net.doepner.baghchal.Piece.PREDATOR;

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

    private Board(Board board) {
        listener = BoardListener.NONE;
        for (int x = 0; x < X_SIZE; x++) {
            System.arraycopy(board.grid[x], 0, grid[x], 0, Y_SIZE);
        }
    }

    boolean doMove(Move move) {
        final Piece piece = movePiece(move);

        if (move.isJump()) {
            clear(move.middle());
            listener.onPredatorTake();
        } else if (piece == PREDATOR) {
            listener.onPredatorStep();
        }
        return true;
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
                positionConsumer.accept(new Position(i,j));
            }
        }
    }
}
