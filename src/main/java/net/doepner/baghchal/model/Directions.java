package net.doepner.baghchal.model;

import java.util.Arrays;
import java.util.Collections;

/**
 * Step directions as constans
 */
public final class Directions {

    public static final Position UP = pos(0, -1);
    public static final Position DOWN = pos(0, +1);
    public static final Position LEFT = pos(-1, 0);
    public static final Position RIGHT = pos(+1, 0);

    public static final Position LEFT_UP = pos(-1, -1);
    public static final Position LEFT_DOWN = pos(-1, +1);
    public static final Position RIGHT_UP = pos(+1, -1);
    public static final Position RIGHT_DOWN = pos(+1, +1);

    private static final Iterable<Position> ALL = Collections.unmodifiableList(Arrays.asList(
            RIGHT_UP, RIGHT, RIGHT_DOWN, DOWN, LEFT_DOWN, LEFT, LEFT_UP, UP));

    private static Position pos(int x, int y) {
        return new Position(x, y);
    }

    public static Iterable<Position> getAll() {
        return ALL;
    }
}
