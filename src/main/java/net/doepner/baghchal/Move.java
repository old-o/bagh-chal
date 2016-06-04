package net.doepner.baghchal;

import static java.lang.Math.abs;

/**
 * From / to coordinates of a move on the game board
 */
public class Move {

    private final Position p1;
    private final Position p2;

    public Move(int x1, int y1, int x2, int y2) {
        this(new Position(x1, y1), new Position(x2, y2));
    }

    public Move(Position p1, Position p2) {
        this.p1 = p1;
        this.p2 = p2;
    }


    public Position p1() {
        return p1;
    }

    public Position p2() {
        return p2;
    }

    public boolean isTakingMove() {
        return abs(p1.x() - p2.x()) == 2 || abs(p1.y() - p2.y()) == 2 ;
    }

    public Position middle() {
        return new Position((p1.x() + p2.x()) / 2, (p1.y() + p2.y()) / 2);
    }

    public Move repeat() {
        return new Move(p2, new Position(p2.x() + xStep(), p2.y() + yStep()));
    }

    private int yStep() {
        return p2.y() - p1.y();
    }

    private int xStep() {
        return p2.x() - p1.x();
    }
}
