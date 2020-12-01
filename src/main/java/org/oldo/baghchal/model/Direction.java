package org.oldo.baghchal.model;

/**
 * Step directions as constans
 */
public enum Direction {

    UP(null, false),
    DOWN(null, true),
    LEFT(false, null),
    RIGHT(true, null),

    LEFT_UP(false, false),
    LEFT_DOWN(false, true),
    RIGHT_UP(true, false),
    RIGHT_DOWN(true, true);

    private final Boolean right, down;
    
    Direction(Boolean right, Boolean down) {
        this.right = right;
        this.down = down;
    }

    public Position addTo(Position p) {
        return addTo(p, right, down);
    }

    public Position subtractFrom(Position p) {
        return addTo(p, left(), up());
    }

    private Position addTo(Position p, Boolean right, Boolean down) {
        return new Position(p.x() + number(right), p.y() + number(down));
    }

    private Boolean left() {
        return opposite(right);
    }

    private Boolean up() {
        return opposite(down);
    }

    private static Boolean opposite(Boolean b) {
        return b == null ? null : !b;
    }

    private static int number(Boolean b) {
        return b == null ? 0 : (b ? 1 : -1);
    }
}
