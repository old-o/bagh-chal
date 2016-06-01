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

    void doMove(Move m) {
        set(m.x2(), m.y2(), get(m.x1(), m.y1()));
        clear(m.x1(), m.y1());

        if (isTakingMove(m)) {
            clear(m.x1() + m.x2() >> 1, m.y1() + m.y2() >> 1);
            sound.playTiger();
        }
    }

    public boolean isTakingMove(Move m) {
        return m.x1() - m.x2() == 2 || m.x2() - m.x1() == 2 || m.y1() - m.y2() == 2 || m.y2() - m.y1() == 2;
    }

    boolean validGoatMove(int x1, int y1, int x2, int y2) {
        if (x1 < 0 || x1 >= X_SIZE || x2 < 0 || x2 >= X_SIZE
                || y1 < 0 || y1 >= Y_SIZE || y2 < 0 || y2 >= Y_SIZE) {
            return false;
        }
        int dx = x1 - x2;
        int dy = y1 - y2;
        if (dx == 0 && dy == 0) {
            return false;
        }
        if (dx > 1 || dx < -1 || dy > 1 || dy < -1) {
            return false;
        }
        if (dx == 1) {
            if (dy == 1) {
                return canMoveUpLeft(x1, y1);
            }
            if (dy == -1) {
                return canMoveDownLeft(x1, y1);
            }
        } else if (dx == -1) {
            if (dy == 1) {
                return canMoveUpRight(x1, y1);
            }
            if (dy == -1) {
                return canMoveDownRight(x1, y1);
            }
        }
        return true;
    }


    boolean canMoveDownLeft(int i, int j) {
        return !(i == 0 || j == 4) && (i + j == 4 || i == 2 && j == 0 || i == 1 && j == 1 || i == 4 && j == 2 || i == 3 && j == 3);
    }

    boolean canMoveDownRight(int i, int j) {
        return !(i == 4 || j == 4) && (i == j || i == 0 && j == 2 || i == 1 && j == 3 || i == 2 && j == 0 || i == 3 && j == 1);
    }

    boolean canMoveUpRight(int i, int j) {
        return j != 0 && (i + j == 4 || i == 0 && j == 2 || i == 1 && j == 1 || i == 2 && j == 4 || i == 3 && j == 3);
    }

    boolean canMoveUpLeft(int i, int j) {
        return !(j == 0 || i == 0) && (i == j || i == 1 && j == 3 || i == 2 && j == 4 || i == 3 && j == 1 || i == 4 && j == 2);
    }

    void reset() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++)
                clear(i, j);

        }
        set(0, 0, TIGER);
        set(4, 0, TIGER);
        set(0, 4, TIGER);
        set(4, 4, TIGER);
    }

    Piece[][] copyBoard() {
        Piece a[][] = new Piece[X_SIZE][Y_SIZE];
        for (int i = 0; i < X_SIZE; i++) {
            System.arraycopy(board[i], 0, a[i], 0, Y_SIZE);

        }
        return a;
    }

    public Piece get(int i, int j) {
        return board[i][j];
    }

    public void set(int i, int j, Piece piece) {
        if (piece == TIGER) {
            sound.play("step.wav");
        }
        board[i][j] = piece;
    }

    void clear(int i, int j) {
        set(i, j, null);
    }

    public boolean empty(int i, int j) {
        return get(i, j) == null;
    }

    public int getXSize() {
        return X_SIZE;
    }

    public int getYSize() {
        return Y_SIZE;
    }
}
