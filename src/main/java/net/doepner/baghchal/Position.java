package net.doepner.baghchal;

/**
 * Position on a 2-dimensional plane
 */
final class Position {

    private final int x;
    private final int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    int x() {
        return x;
    }

    int y() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Position position = (Position) o;
        return x == position.x && y == position.y;
    }

    @Override
    public int hashCode() {
        return 31 * x + y;
    }

    Position add(int xStep, int yStep) {
        return new Position(x + xStep, y + yStep);
    }

    boolean isLessOrEqualTo(Position p) {
        return x <= p.x && y <= p.y;
    }

    boolean isGreaterOrEqualTo(Position p) {
        return p.x <= x && p.y <= y;
    }

    boolean hasEvenCoordSum() {
        return (x+y) % 2 == 0;
    }

    @Override
    public String toString() {
        return String.format("Position{x=%d, y=%d}", x, y);
    }
}
