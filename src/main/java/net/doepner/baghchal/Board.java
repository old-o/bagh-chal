package net.doepner.baghchal;

/**
 * The game board model
 */
public class Board {

    private final int board[][] = new int[5][5];

    void doMove(Move m) {
        board[m.x2()][m.y2()] = board[m.x1()][m.y1()];
        board[m.x1()][m.y1()] = 0;
        if (isTakingMove(m))
            board[m.x1() + m.x2() >> 1][m.y1() + m.y2() >> 1] = 0;
    }

    public boolean isTakingMove(Move m) {
        return m.x1() - m.x2() == 2 || m.x2() - m.x1() == 2 || m.y1() - m.y2() == 2 || m.y2() - m.y1() == 2;
    }

    boolean validGoatMove(int x1, int y1, int x2, int y2) {
        if (x1 < 0 || x1 > 4 || x2 < 0 || x2 > 4 || y1 < 0 || y1 > 4 || y2 < 0 || y2 > 4) {
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

    int b(int i, int j) {
        if (i < 0 || i > 4 || j < 0 || j > 4)
            return -1;
        else
            return board[i][j];
    }

    void reset() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++)
                board[i][j] = 0;

        }
        board[0][0] = board[4][0] = board[0][4] = board[4][4] = 2;
    }

    int[][] copyBoard() {
        int a[][] = new int[5][5];
        for (int i = 0; i < 5; i++) {
            System.arraycopy(board[i], 0, a[i], 0, 5);

        }
        return a;
    }

    public int get(int i, int j) {
        return board[i][j];
    }

    public void set(int i, int j, int value) {
        board[i][j] = value;
    }
}
