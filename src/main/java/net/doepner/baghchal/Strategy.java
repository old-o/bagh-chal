package net.doepner.baghchal;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static net.doepner.baghchal.Piece.GOAT;
import static net.doepner.baghchal.Piece.TIGER;

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
        if (!tryToTake()) {
            board.doMove(possibleMoves.get((int) (Math.random() * (double) possibleMoves.size())));
        }
    }


    void doAI_2() {
        if (tryToTake()) {
            return;
        }
        final int s = possibleMoves.size();
        List<Move> threatensOne = new ArrayList<>();
        List<Move> threatensMany = new ArrayList<>();
        for (Move m : possibleMoves) {
            Piece[][] brd = board.copyBoard();
            brd[m.p2().x()][m.p2().y()] = brd[m.p1().x()][m.p1().y()];
            brd[m.p1().x()][m.p1().y()] = null;
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

    int numberOfPiecesThreatened(Piece[][] brd) {
        int r = 0;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++)
                if (brd[i][j] == TIGER) {
                    if (i > 1) {
                        if (j > 1 && brd[i - 1][j - 1] == GOAT && brd[i - 2][j - 2] == null) {
                            r++;
                        }
                        if (brd[i - 1][j] == GOAT && brd[i - 2][j] == null) {
                            r++;
                        }
                        if (j < 3 && brd[i - 1][j + 1] == GOAT && brd[i - 2][j + 2] == null) {
                            r++;
                        }
                    }
                    if (j < 3 && brd[i][j + 1] == GOAT && brd[i][j + 2] == null) {
                        r++;
                    }
                    if (i < 3) {
                        if (j < 3 && brd[i + 1][j + 1] == GOAT && brd[i + 2][j + 2] == null) {
                            r++;
                        }
                        if (brd[i + 1][j] == GOAT && brd[i + 2][j] == null) {
                            r++;
                        }
                        if (j > 1 && brd[i + 1][j - 1] == GOAT && brd[i + 2][j - 2] == null) {
                            r++;
                        }
                    }
                    if (j > 1 && brd[i][j - 1] == GOAT && brd[i][j - 2] == null) {
                        r++;
                    }
                }
        }
        return r;
    }

    void doAI_3() {
        List<Move> takes = possibleMoves.stream().filter(Move::isTakingMove).collect(toList());

        int s2 = takes.size();
        if (s2 > 0) {
            board.doMove(takes.get((int) (Math.random() * (double) s2)));
            return;
        }
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (b(i, j) == TIGER) {
                    addPossibleTakes(takes, i, j);
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

    private Piece b(Position p) {
        return !board.isValidPosition(p) ? Piece.UNDEFINED : board.get(p);
    }

    private Piece b(int i, int j) {
        return b(new Position(i, j));
    }

    boolean tryToTake() {
        final List<Move> takingMoves = new ArrayList<>();
        for (Move m : possibleMoves) {
            if (m.isTakingMove()) {
                takingMoves.add(m);
            }
        }
        final int s2 = takingMoves.size();
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
                if (b(i, j) == TIGER) {
                    addPossibleTakes(possibleMoves, i, j);
                    addPossibleShifts(possibleMoves, i, j);
                }
        }
    }

    private final static int[] STEPS = {-1, 0, +1};

    private void addPossibleTakes(List<Move> takes, int i, int j) {
        for (int xStep : STEPS) {
            for (int yStep : STEPS) {
                final Move m1 = new Move(i, j, i + xStep, j + yStep);
                final Move m2 = new Move(i + xStep, j + yStep, i + 2 * xStep, j + 2 * yStep);
                if (board.validMove(m1) && board.validMove(m2) && b(m1.p1()) == GOAT) {
                    takes.add(new Move(i, j, i + 2 * xStep, j + 2 * yStep));
                }
            }
        }
    }

    private void addPossibleShifts(List<Move> moves, int i, int j) {
        for (int xStep : STEPS) {
            for (int yStep : STEPS) {
                final Move move = new Move(i, j, i + xStep, j + yStep);
                if (board.validMove(move) && b(move.p2()) == null) {
                    moves.add(move);
                }
            }
        }
    }

    public boolean isOver() {
        return possibleMoves.size() == 0;
    }
}
