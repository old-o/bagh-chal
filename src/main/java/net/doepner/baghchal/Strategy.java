package net.doepner.baghchal;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * The computer player's strategy (playing the tigers)
 */
public class Strategy {

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

    void doAI_1() {
        if (!tryToTake())
            board.doMove(possibleMoves.get((int) (Math.random() * (double) possibleMoves.size())));
    }


    void doAI_2() {
        if (tryToTake()) {
            return;
        }
        final int s = possibleMoves.size();
        List<Move> threatensOne = new ArrayList<>();
        List<Move> threatensMany = new ArrayList<>();
        for (Move m : possibleMoves) {
            int brd[][] = board.copyBoard();
            brd[m.x2()][m.y2()] = brd[m.x1()][m.y1()];
            brd[m.x1()][m.y1()] = 0;
            int t = numberOfPiecesThreatened(brd);
            if (t == 1) {
                threatensOne.add(m);
            } else if (t > 1) {
                threatensMany.add(m);
            }
        }

        int s2 = threatensMany.size();
        if (s2 > 0) {
            board.doMove(possibleMoves.get((int) (Math.random() * (double) s2)));
        } else {
            s2 = threatensOne.size();
            if (s2 > 0) {
                board.doMove(possibleMoves.get((int) (Math.random() * (double) s2)));
            } else {
                board.doMove(possibleMoves.get((int) (Math.random() * (double) s)));
            }
        }
    }

    int numberOfPiecesThreatened(int brd[][]) {
        int r = 0;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++)
                if (brd[i][j] == 2) {
                    if (i > 1) {
                        if (j > 1 && brd[i - 1][j - 1] == 1 && brd[i - 2][j - 2] == 0)
                            r++;
                        if (brd[i - 1][j] == 1 && brd[i - 2][j] == 0)
                            r++;
                        if (j < 3 && brd[i - 1][j + 1] == 1 && brd[i - 2][j + 2] == 0)
                            r++;
                    }
                    if (j < 3 && brd[i][j + 1] == 1 && brd[i][j + 2] == 0)
                        r++;
                    if (i < 3) {
                        if (j < 3 && brd[i + 1][j + 1] == 1 && brd[i + 2][j + 2] == 0)
                            r++;
                        if (brd[i + 1][j] == 1 && brd[i + 2][j] == 0)
                            r++;
                        if (j > 1 && brd[i + 1][j - 1] == 1 && brd[i + 2][j - 2] == 0)
                            r++;
                    }
                    if (j > 1 && brd[i][j - 1] == 1 && brd[i][j - 2] == 0)
                        r++;
                }
        }
        return r;
    }


    void doAI_3() {
        List<Move> takes = possibleMoves.stream().filter(board::isTakingMove).collect(toList());

        int s2 = takes.size();
        if (s2 > 0) {
            board.doMove(takes.get((int) (Math.random() * (double) s2)));
            return;
        }
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (b(i, j) == 2) {
                    if (board.canMoveUpLeft(i, j) && i > 1 && j > 1 && b(i - 1, j - 1) == 1 && b(i - 2, j - 2) == 0)
                        takes.add(new Move(i, j, i - 2, j - 2));
                    if (i > 1 && b(i - 1, j) == 1 && b(i - 2, j) == 0)
                        takes.add(new Move(i, j, i - 2, j));
                    if (board.canMoveDownLeft(i, j) && i > 1 && j < 3 && b(i - 1, j + 1) == 1 && b(i - 2, j + 2) == 0)
                        takes.add(new Move(i, j, i - 2, j + 2));
                    if (j < 3 && b(i, j + 1) == 1 && b(i, j + 2) == 0)
                        takes.add(new Move(i, j, i, j + 2));
                    if (board.canMoveDownRight(i, j) && i < 3 && j < 3 && b(i + 1, j + 1) == 1 && b(i + 2, j + 2) == 0)
                        takes.add(new Move(i, j, i + 2, j + 2));
                    if (i < 3 && b(i + 1, j) == 1 && b(i + 2, j) == 0)
                        takes.add(new Move(i, j, i + 2, j));
                    if (board.canMoveUpRight(i, j) && i < 3 && j > 1 && b(i + 1, j - 1) == 1 && b(i + 2, j - 2) == 0)
                        takes.add(new Move(i, j, i + 2, j - 2));
                    if (j > 1 && b(i, j - 1) == 1 && b(i, j - 2) == 0)
                        takes.add(new Move(i, j, i, j - 2));
                }
            }

        }

        s2 = takes.size();
        if (s2 > 0) {
            board.doMove(takes.get((int) (Math.random() * (double) s2)));
        } else {
            board.doMove(possibleMoves.get((int) (Math.random() * (double) possibleMoves.size())));
        }
    }

    private int b(int i, int j) {
        return board.get(i, j);
    }

    boolean tryToTake() {
        List<Move> takingMoves = new ArrayList<>();
        for (Object possibleMove : possibleMoves) {
            Move m = (Move) possibleMove;
            if (board.isTakingMove(m))
                takingMoves.add(m);
        }

        int s2 = takingMoves.size();
        if (s2 > 0) {
            board.doMove(takingMoves.get((int) (Math.random() * (double) s2)));
            return true;
        } else {
            return false;
        }
    }

    void updatePossibleMoves() {
        possibleMoves.clear();
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++)
                if (b(i, j) == 2) {
                    if (j < 4)
                        if (b(i, j + 1) == 0)
                            possibleMoves.add(new Move(i, j, i, j + 1));
                        else if (j < 3 && b(i, j + 1) == 1 && b(i, j + 2) == 0)
                            possibleMoves.add(new Move(i, j, i, j + 2));
                    if (j > 0)
                        if (b(i, j - 1) == 0)
                            possibleMoves.add(new Move(i, j, i, j - 1));
                        else if (j > 1 && b(i, j - 1) == 1 && b(i, j - 2) == 0)
                            possibleMoves.add(new Move(i, j, i, j - 2));
                    if (i > 0)
                        if (b(i - 1, j) == 0)
                            possibleMoves.add(new Move(i, j, i - 1, j));
                        else if (i > 1 && b(i - 1, j) == 1 && b(i - 2, j) == 0)
                            possibleMoves.add(new Move(i, j, i - 2, j));
                    if (i < 4)
                        if (b(i + 1, j) == 0)
                            possibleMoves.add(new Move(i, j, i + 1, j));
                        else if (i < 3 && b(i + 1, j) == 1 && b(i + 2, j) == 0)
                            possibleMoves.add(new Move(i, j, i + 2, j));
                    if (i > 0 && j < 4 && board.canMoveDownLeft(i, j))
                        if (b(i - 1, j + 1) == 0)
                            possibleMoves.add(new Move(i, j, i - 1, j + 1));
                        else if (i > 1 && j < 3 && b(i - 1, j + 1) == 1 && b(i - 2, j + 2) == 0)
                            possibleMoves.add(new Move(i, j, i - 2, j + 2));
                    if (i < 4 && j < 4 && board.canMoveDownRight(i, j))
                        if (b(i + 1, j + 1) == 0)
                            possibleMoves.add(new Move(i, j, i + 1, j + 1));
                        else if (i < 3 && j < 3 && b(i + 1, j + 1) == 1 && b(i + 2, j + 2) == 0)
                            possibleMoves.add(new Move(i, j, i + 2, j + 2));
                    if (i > 0 && j > 0 && board.canMoveUpLeft(i, j))
                        if (b(i - 1, j - 1) == 0)
                            possibleMoves.add(new Move(i, j, i - 1, j - 1));
                        else if (i > 1 && j > 1 && b(i - 1, j - 1) == 1 && b(i - 2, j - 2) == 0)
                            possibleMoves.add(new Move(i, j, i - 2, j - 2));
                    if (i < 4 && j > 0 && board.canMoveUpRight(i, j))
                        if (b(i + 1, j - 1) == 0)
                            possibleMoves.add(new Move(i, j, i + 1, j - 1));
                        else if (i < 3 && j > 1 && b(i + 1, j - 1) == 1 && b(i + 2, j - 2) == 0)
                            possibleMoves.add(new Move(i, j, i + 2, j - 2));
                }
        }
    }

    public boolean isOver() {
        return possibleMoves.size() == 0;
    }


}
