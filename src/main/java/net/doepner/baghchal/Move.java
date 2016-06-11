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

    public boolean isJump() {
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

    public boolean isOneDimensional() {
        return (xStep() == 0) != (yStep() == 0);
    }

    @Override
    public String toString() {
        return String.format("Move{p1=%s, p2=%s}", p1, p2);
    }

    public boolean isStep() {
        final int xDelta = abs(xStep());
        final int yDelta = abs(yStep());
        return xDelta == 0 ? yDelta == 1 : xDelta == 1 && yDelta <= 1;
    }
}
