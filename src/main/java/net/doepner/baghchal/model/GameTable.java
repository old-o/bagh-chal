package net.doepner.baghchal.model;

import net.doepner.baghchal.Listener;
import net.doepner.baghchal.Setup;
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
public final class GameTable {

    private final LogProvider logProvider;

    private final int xSize;
    private final int ySize;

    private final Piece[][] grid;

    private final Setup setup;
    private final Listener listener;
    private final TablePositions positions;

    public GameTable(LogProvider logProvider, int xSize, int ySize, Setup setup, Listener listener) {
        this.logProvider = logProvider;
        this.setup = setup;
        this.listener = listener;
        this.xSize = xSize;
        this.ySize = ySize;
        grid = new Piece[xSize + 2][ySize + 2];
        positions = getTablePositions(xSize, ySize, grid);
    }

    private static TablePositions getTablePositions(int xSize, int ySize, Piece[][] grid) {
        final TablePositions tps = new TablePositions(new Position(1, 1), new Position(xSize, ySize));
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[x].length; y++) {
                final Position p = new Position(x, y);
                tps.add(p);
            }
        }
        return tps;
    }

    public TablePositions getPositions() {
        return positions;
    }

    /**
     * Copy constructor that will copy the grid array of the provided GameTable instance.
     * The resulting board will support no listener functionality.
     *
     * @param gameTable An existing GameTable instance
     */
    private GameTable(GameTable gameTable) {
        this(gameTable.logProvider, gameTable.xSize, gameTable.ySize, gameTable.setup, Listener.NONE);
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
            if (!positions.isBorderToBoard(move) && move.isJump()) {
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

    public void setHiddenBorderPieceCount(int count) {
        this.hiddenBorderPieceCount = count;
    }

    private int hiddenBorderPieceCount;

    public Position pick(Position p, Piece piece) {
        if (get(p) == piece) {
            if (positions.isBoard(p) || hiddenBorderPieceCount <= 0) {
                clear(p);
            }
            if (positions.isBorder(p) && hiddenBorderPieceCount > 0) {
                hiddenBorderPieceCount--;
            }
            listener.afterPicked(piece);
            return p;
        } else {
            return null;
        }
    }

    public List<Move> getStepsWhereAdjacent(Piece movingPiece, Piece requiredPiece) {
        final List<Move> steps = new ArrayList<>();
        for (Position p : positions.getBoard()) {
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

    public void addPossibleJump(Collection<Move> list, Move step1) {
        final Move step2 = step1.repeat();
        if (isStepAlongLine(step2) && isEmptyAt(step2.p2())) {
            list.add(new Move(step1.p1(), step2.p2()));
        }
    }

    public boolean isStepAlongLine(Move move) {
        return move.isStep() && positions.isBoardMove(move)
                && (move.p1().hasEvenCoordSum() || move.isOneDimensional());
    }

    public void reset() {
        for (Piece[] pieces : grid) {
            Arrays.fill(pieces, null);
        }
        setup.prepare(this);
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

    public boolean isValid(Move move, MoveConstraints piece) {
        return !move.isStationary() && isEmptyAt(move.p2()) && piece.isValid(move, this);
    }

    boolean isBorderEmpty() {
        for (Position p : positions.getBorder()) {
            if (get(p) != null) {
                return false;
            }
        }
        return true;
    }

    public Position getBorderPosition(Piece piece) {
        for (Position p : positions.getBorder()) {
            if (get(p) == piece) {
                return p;
            }
        }
        return null;
    }

    public int getBoardXSize() {
        return xSize;
    }

    public int getBoardYSize() {
        return ySize;
    }

    public int getXSize() {
        return grid.length;
    }

    public int getYSize() {
        return grid[0].length;
    }

    private String toString(Move move) {
        final String nl = System.lineSeparator();
        final StringBuilder sb = new StringBuilder(nl);
        sb.append(move).append(nl);
        for (Piece[] aGrid : grid) {
            for (Piece piece : aGrid) {
                sb.append(piece == null ? "    " : piece.toString().substring(0, 4));
                sb.append('|');
            }
            sb.append(nl);
        }
        return sb.toString();
    }
}
