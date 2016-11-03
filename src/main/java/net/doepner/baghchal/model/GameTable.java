package net.doepner.baghchal.model;

import net.doepner.baghchal.Listener;
import org.guppy4j.Lists;
import org.guppy4j.log.Log;
import org.guppy4j.log.LogProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static net.doepner.baghchal.model.Piece.INVALID;
import static org.guppy4j.log.Log.Level.debug;

/**
 * The game board model
 */
public class GameTable {

    private final LogProvider logProvider;

    private final int xSize;
    private final int ySize;

    private final Piece[][] grid;

    private final Listener listener;

    private final Collection<Position> allPositions = new ArrayList<>();
    private final Collection<Position> boardPositions = new ArrayList<>();
    private final Collection<Position> borderPositions = new ArrayList<>();
    private final Collection<Position> cornerPositions = new ArrayList<>();

    public GameTable(LogProvider logProvider, int xSize, int ySize, Listener listener) {
        this.logProvider = logProvider;
        this.listener = listener;
        this.xSize = xSize;
        this.ySize = ySize;
        grid = new Piece[xSize + 2][ySize + 2];
        initPositions(new Position(1, 1), new Position(xSize, ySize));
    }

    private void initPositions(Position topLeft, Position bottomRight) {
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[x].length; y++) {
                final Position p = new Position(x, y);
                allPositions.add(p);
                if (p.isGreaterOrEqualTo(topLeft) && p.isLessOrEqualTo(bottomRight)) {
                    boardPositions.add(p);
                    if ((x == topLeft.x() || x == bottomRight.x()) && (y == topLeft.y() || y == bottomRight.y())) {
                        cornerPositions.add(p);
                    }
                } else {
                    borderPositions.add(p);
                }
            }
        }
    }

    /**
     * Copy constructor that will copy the grid array of the provided GameTable instance.
     * The resulting board will support no listener functionality.
     *
     * @param gameTable An existing GameTable instance
     */
    private GameTable(GameTable gameTable) {
        this(gameTable.logProvider, gameTable.xSize, gameTable.ySize, Listener.NONE);
        for (int x = 0; x < grid.length; x++) {
            System.arraycopy(gameTable.grid[x], 0, grid[x], 0, grid[x].length);
        }
    }

    public GameTable copy() {
        return new GameTable(this);
    }

    public void processMove(Move move) {
        if (move != null) {
            final Piece piece = get(move.p2());
            if (!isBorderToBoard(move) && move.isJump()) {
                clear(move.middle());
                listener.afterJump(piece);
            } else {
                listener.afterStep(piece);
            }
            log().as(debug, toString(move));
        }
    }

    private Log log() {
        return logProvider.getLog(getClass());
    }

    public Piece movePiece(Move move) {
        final Piece piece = get(move.p1());
        if (piece == null || piece == INVALID) {
            throw new IllegalStateException("Cannot move piece from empty or off-board position:" + move.p1());
        } else {
            clear(move.p1());
            set(move.p2(), piece);
            return piece;
        }
    }

    public Position pick(Position p, Piece piece) {
        if (get(p) == piece) {
            clear(p);
            listener.afterPicked(piece);
            return p;
        } else {
            return null;
        }
    }

    public List<Move> getStepsWhereAdjacent(Piece movingPiece, Piece requiredPiece) {
        final List<Move> steps = new ArrayList<>();
        for (Position p : boardPositions) {
            if (get(p) == movingPiece) {
                for (Position d : Directions.getAll()) {
                    final Position p2 = p.add(d);
                    if (get(p2) == requiredPiece) {
                        final Move step = new Move(p, p2);
                        if (isStepAlongLine(step)) {
                            steps.add(step);
                        }
                    }
                }
            }
        }
        return steps;
    }

    public Move tryMoveFrom(List<Move> moves) {
        final Move move = Lists.getRandomFrom(moves);
        if (move != null) {
            movePiece(move);
        }
        return move;
    }

    public List<Move> getPossibleJumps(Piece movingPiece, Piece requiredPiece) {
        final List<Move> jumps = new ArrayList<>();
        for (Move step : getStepsWhereAdjacent(movingPiece, requiredPiece)) {
            addPossibleJump(jumps, step);
        }
        return jumps;
    }

    public void addPossibleJump(List<Move> list, Move step1) {
        final Move step2 = step1.repeat();
        if (isStepAlongLine(step2) && isEmptyAt(step2.p2())) {
            list.add(new Move(step1.p1(), step2.p2()));
        }
    }

    public boolean isStepAlongLine(Move move) {
        return move.isStep() && isBoardPosition(move.p1()) && isBoardPosition(move.p2())
                && (move.p1().hasEvenCoordSum() || move.isOneDimensional());
    }

    public boolean isBoardPosition(Position p) {
        return boardPositions.contains(p);
    }

    public void reset() {
        for (Piece[] pieces : grid) {
            Arrays.fill(pieces, null);
        }
        listener.afterReset();
    }

    public Piece get(Position p) {
        try {
            return grid[p.x()][p.y()];
        } catch (ArrayIndexOutOfBoundsException e) {
            return INVALID;
        }
    }

    public void set(Position p, Piece piece) {
        grid[p.x()][p.y()] = piece;
    }

    private void clear(Position p) {
        set(p, null);
    }

    public boolean isEmptyAt(Position p) {
        return get(p) == null;
    }

    public boolean isValid(Move move, Piece piece) {
        return !move.isStationary() && isEmptyAt(move.p2()) && piece.isValid(move, this);
    }

    public boolean isBorderToBoard(Move move) {
        return isBorderPosition(move.p1()) && !isBorderPosition(move.p2());
    }

    public boolean isBorderEmpty() {
        for (Position p : borderPositions) {
            if (get(p) != null) {
                return false;
            }
        }
        return true;
    }

    private boolean isBorderPosition(Position p) {
        return borderPositions.contains(p);
    }

    public Position getBorderPosition(Piece piece) {
        for (Position p : borderPositions) {
            if (get(p) == piece) {
                return p;
            }
        }
        return null;
    }

    public int getCentreXSize() {
        return xSize;
    }

    public int getCentreYSize() {
        return ySize;
    }

    public int getXSize() {
        return grid.length;
    }

    public int getYSize() {
        return grid[0].length;
    }

    public Iterable<Position> getAllPositions() {
        return allPositions;
    }

    public Iterable<Position> getBoardPositions() {
        return boardPositions;
    }

    public String toString(Move move) {
        final String nl = System.lineSeparator();
        final StringBuilder sb = new StringBuilder(nl);
        sb.append(move).append(nl);
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[x].length; y++) {
                Piece piece = grid[y][x];
                sb.append(piece == null ? "    " : piece.toString().substring(0, 4));
                sb.append('|');
            }
            sb.append(nl);
        }
        return sb.toString();
    }

    public void setCornerPositions(Piece piece) {
        for (Position p : cornerPositions) {
            set(p, piece);
        }
    }

    public void setBorderPositions(Piece piece, int count) {
        final int available = borderPositions.size();
        if (count <= available) {
            for (Position p : borderPositions) {
                if (count-- > 0) {
                    set(p, piece);
                }
            }
        } else {
            throw new IllegalArgumentException(
                    "Cannot place " + count + " pieces on " + available + " border positions");
        }
    }
}
