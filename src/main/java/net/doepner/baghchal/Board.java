package net.doepner.baghchal;

import static net.doepner.baghchal.Piece.TIGER;

/**
 * The game board model
 */
public class Board {

    private static final int X_SIZE = 5;
    private static final int Y_SIZE = 5;

    private final Piece[][] board = new Piece[X_SIZE][Y_SIZE];
    private final Sound sound;

    public Board(Sound sound) {
        this.sound = sound;
    }

    void doMove(Move move) {
        final Piece piece = get(move.p1());
        set(move.p2(), piece);
        clear(move.p1());

        if (piece == TIGER) {
            sound.play("step.wav");
        }
        if (move.isTakingMove()) {
            clear(move.middle());
            sound.playTiger();
        }
    }

    boolean validMove(Move move) {
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
            return canMoveLeftUp(x1, y1);
        }
        if (x1 - 1 == x2 && y1 + 1 == y2) {
            return canMoveLeftDown(x1, y1);
        }
        if (x1 - x2 == -1 && y1 - y2 == 1) {
            return canMoveRightUp(x1, y1);
        }
        if (x1 + 1 == x2 && y1 + 1 == y2) {
            return canMoveRightDown(x1, y1);
        }
        return false;
    }

    boolean isValidPosition(Position pos) {
        final int x = pos.x();
        final int y = pos.y();
        return x >= 0 && y >= 0 && x < X_SIZE && y < Y_SIZE;
    }

    boolean canMoveLeftDown(int x, int y) {
        return !(x == 0 || y == 4) && (x + y == 4 || x == 2 && y == 0 || x == 1 && y == 1 || x == 4 && y == 2 || x == 3 && y == 3);
    }

    boolean canMoveRightDown(int x, int y) {
        return !(x == 4 || y == 4) && (x == y || x == 0 && y == 2 || x == 1 && y == 3 || x == 2 && y == 0 || x == 3 && y == 1);
    }

    boolean canMoveRightUp(int x, int y) {
        return y != 0 && (x + y == 4 || x == 0 && y == 2 || x == 1 && y == 1 || x == 2 && y == 4 || x == 3 && y == 3);
    }

    boolean canMoveLeftUp(int x, int y) {
        return !(y == 0 || x == 0) && (x == y || x == 1 && y == 3 || x == 2 && y == 4 || x == 3 && y == 1 || x == 4 && y == 2);
    }

    void reset() {
        for (int x = 0; x < X_SIZE; x++) {
            for (int y = 0; y < Y_SIZE; y++) {
                clear(x, y);
            }
        }
        set(0, 0, TIGER);
        set(4, 0, TIGER);
        set(0, 4, TIGER);
        set(4, 4, TIGER);
    }

    Piece[][] copyBoard() {
        final Piece a[][] = new Piece[X_SIZE][Y_SIZE];
        for (int x = 0; x < X_SIZE; x++) {
            System.arraycopy(board[x], 0, a[x], 0, Y_SIZE);

        }
        return a;
    }

    public Piece get(Position p) {
        return get(p.x(), p.y());
    }

    public void set(Position p, Piece piece) {
        board[p.x()][p.y()] = piece;
    }

    void clear(Position p) {
        set(p, null);
    }

    public boolean isEmpty(Position p) {
        return get(p) == null;
    }

    protected Piece get(int x, int y) {
        return board[x][y];
    }

    private void set(int x, int y, Piece piece) {
        board[x][y] = piece;
    }

    private void clear(int x, int y) {
        set(x, y, null);
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
        return new Position(x,y);
    }

    private int normalize(int n, int max) {
        return n < 0 ? 0 : n >= max ? max - 1 : n;
    }
}
