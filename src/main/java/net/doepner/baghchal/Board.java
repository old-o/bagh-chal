package net.doepner.baghchal;

import static net.doepner.baghchal.Piece.PREDATOR;

import java.util.Arrays;

/**
 * The game board model
 */
public class Board {

    private static final int X_SIZE = 5;
    private static final int Y_SIZE = 5;

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
        final Piece piece = get(move.p1());
        clear(move.p1());
        set(move.p2(), piece);

        if (move.isJump()) {
            clear(move.middle());
            listener.onPredatorTake();
        } else if (piece == PREDATOR) {
            listener.onPredatorStep();
        }
        return true;
    }

    boolean validStep(Move move) {
        final Position p1 = move.p1();
        final Position p2 = move.p2();
        if (!isValidPosition(p1) || !isValidPosition(p2)) {
            return false;
        }
        final int x1 = p1.x();
        final int y1 = p1.y();
        final int x2 = p2.x();
        final int y2 = p2.y();

        if (x1 == x2) {
            return y1 + 1 == y2 || y1 - 1 == y2;
        }
        if (y1 == y2) {
            return x1 + 1 == x2 || x1 - 1 == x2;
        }
        if (x1 - 1 == x2 && y1 - 1 == y2) {
            return !(y1 == 0 || x1 == 0) && (x1 == y1 || x1 == 1 && y1 == 3 || x1 == 2 && y1 == 4 || x1 == 3 && y1 == 1 || x1 == 4 && y1 == 2);
        }
        if (x1 - 1 == x2 && y1 + 1 == y2) {
            return !(x1 == 0 || y1 == 4) && (x1 + y1 == 4 || x1 == 2 && y1 == 0 || x1 == 1 && y1 == 1 || x1 == 4 && y1 == 2 || x1 == 3 && y1 == 3);
        }
        if (x1 - x2 == -1 && y1 - y2 == 1) {
            return y1 != 0 && (x1 + y1 == 4 || x1 == 0 && y1 == 2 || x1 == 1 && y1 == 1 || x1 == 2 && y1 == 4 || x1 == 3 && y1 == 3);
        }
        if (x1 + 1 == x2 && y1 + 1 == y2) {
            return !(x1 == 4 || y1 == 4) && (x1 == y1 || x1 == 0 && y1 == 2 || x1 == 1 && y1 == 3 || x1 == 2 && y1 == 0 || x1 == 3 && y1 == 1);
        }
        return false;
    }

    boolean isValidPosition(Position pos) {
        final int x = pos.x();
        final int y = pos.y();
        return x >= 0 && y >= 0 && x < X_SIZE && y < Y_SIZE;
    }

    void reset() {
        for (int x = 0; x < X_SIZE; x++) {
            Arrays.fill(grid[x], null);
        }
        set(0, 0, PREDATOR);
        set(X_SIZE - 1, 0, PREDATOR);
        set(0, Y_SIZE - 1, PREDATOR);
        set(X_SIZE - 1, Y_SIZE - 1, PREDATOR);
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
}
