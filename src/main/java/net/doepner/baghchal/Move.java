package net.doepner.baghchal;

import static java.lang.Math.abs;

/**
 * From / to coordinates of a move on the game board
 */
public class Move {

    private final Position p1;
    private final Position p2;

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

    public boolean isJump() {
        return xDiff() == 2 || yDiff() == 2 ;
    }

    public Position middle() {
        return new Position((p1.x() + p2.x()) / 2, (p1.y() + p2.y()) / 2);
    }

    public Move repeat() {
        return new Move(p2, new Position(p2.x() + xStep(), p2.y() + yStep()));
    }

    public boolean isOneDimensional() {
        return (xStep() == 0) != (yStep() == 0);
    }

    public boolean isStep() {
        final int xd = xDiff();
        final int yd = yDiff();
        return xd == 0 ? yd == 1 : xd == 1 && yd <= 1;
    }

    private int yStep() {
        return p2.y() - p1.y();
    }

    private int xStep() {
        return p2.x() - p1.x();
    }

    private int yDiff() {
        return abs(yStep());
    }

    private int xDiff() {
        return abs(xStep());
    }

    @Override
    public String toString() {
        return String.format("Move{p1=%s, p2=%s}", p1, p2);
    }
}
