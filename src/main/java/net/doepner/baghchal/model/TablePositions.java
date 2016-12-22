package net.doepner.baghchal.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Classifies table positions
 */
public final class TablePositions {

    private final Collection<Position> all = new ArrayList<>();
    private final Collection<Position> board = new ArrayList<>();
    private final Collection<Position> border = new ArrayList<>();
    private final Collection<Position> corners = new ArrayList<>();
    private final Collection<Position> borderCorners = new ArrayList<>();

    private final Position topLeft;
    private final Position bottomRight;

    TablePositions(Position topLeft, Position bottomRight) {
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
    }

    public void add(Position p) {
        all.add(p);
        if (p.isGreaterOrEqualTo(topLeft) && p.isLessOrEqualTo(bottomRight)) {
            board.add(p);
            if ((p.x() == topLeft.x() || p.x() == bottomRight.x())
                    && (p.y() == topLeft.y() || p.y() == bottomRight.y())) {
                corners.add(p);
            }
        } else {
            if ((p.x() < topLeft.x() || p.x() > bottomRight.x())
                    && (p.y() < topLeft.y() || p.y() > bottomRight.y())) {
                borderCorners.add(p);
            } else {
                border.add(p);
            }
        }
    }

    public Iterable<Position> getAll() {
        return all;
    }

    public Iterable<Position> getBoard() {
        return board;
    }

    public Iterable<Position> getBorder() {
        final Collection<Position> list = new ArrayList<>(border);
        list.addAll(borderCorners);
        return list;
    }

    public Iterable<Position> getCorners() {
        return corners;
    }

    public boolean isBoard(Position p) {
        return board.contains(p);
    }

    public boolean isBorder(Position p) {
        return border.contains(p) || borderCorners.contains(p);
    }

    public boolean isBorderToBoard(Move move) {
        return isBorder(move.p1()) && !isBorder(move.p2());
    }

    public int getBoardSize() {
        return board.size();
    }

    public boolean isBoardMove(Move move) {
        return isBoard(move.p1()) && isBoard(move.p2());
    }
}
