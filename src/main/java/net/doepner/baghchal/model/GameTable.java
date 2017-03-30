package net.doepner.baghchal.model;

import net.doepner.baghchal.Listener;
import org.guppy4j.Lists;
import org.guppy4j.log.Log;
import org.guppy4j.log.LogProvider;
import org.guppy4j.text.CharCanvas;
import org.guppy4j.text.CharDrawing;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import static java.lang.Math.max;
import static java.lang.System.lineSeparator;
import static net.doepner.baghchal.model.Direction.DOWN;
import static net.doepner.baghchal.model.Direction.RIGHT;
import static net.doepner.baghchal.model.Direction.RIGHT_DOWN;
import static net.doepner.baghchal.model.Direction.RIGHT_UP;
import static org.guppy4j.log.Log.Level.debug;

/**
 * The game board model
 */
public final class GameTable {

    private static final Direction[] directions = {RIGHT_UP, RIGHT, RIGHT_DOWN, DOWN};

    private final LogProvider logProvider;

    private final int boardXSize;
    private final int boardYSize;
    private final CharCanvas charCanvas;

    private final Piece[][] grid;

    private final Consumer<GameTable> setupMethod;
    private final Listener listener;
    private final TablePositions positions;

    public GameTable(LogProvider logProvider, int boardXSize, int boardYSize,
                     Consumer<GameTable> setupMethod, Listener listener,
                     CharCanvas charCanvas) {
        this.logProvider = logProvider;
        this.setupMethod = setupMethod;
        this.listener = listener;
        this.boardXSize = boardXSize;
        this.boardYSize = boardYSize;
        this.charCanvas = charCanvas;
        grid = new Piece[boardXSize + 2][boardYSize + 2];
        final Position topLeft = new Position(1, 1);
        final Position bottomRight = new Position(boardXSize, boardYSize);
        positions = new TablePositions(topLeft, bottomRight, getPositions(grid));
    }

    private static Iterable<Position> getPositions(Piece[][] grid) {
        final Collection<Position> list = new ArrayList<>();
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[x].length; y++) {
                list.add(new Position(x, y));
            }
        }
        return list;
    }

    public TablePositions getPositions() {
        return positions;
    }

    /**
     * Copy constructor that will copy the grid array of the provided GameTable instance.
     * The resulting board will support no listener functionality.
     *
     * @param gt An existing GameTable instance
     */
    private GameTable(GameTable gt) {
        this(gt.logProvider, gt.boardXSize, gt.boardYSize, gt.setupMethod, Listener.NONE, gt.charCanvas);
        for (int x = 0; x < grid.length; x++) {
            System.arraycopy(gt.grid[x], 0, grid[x], 0, grid[x].length);
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
        if (piece == null) {
            throw new IllegalStateException("Cannot move piece from empty position:" + move.p1());
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
                for (Direction d : Direction.values()) {
                    final Position p2 = d.addTo(p);
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
        setupMethod.accept(this);
    }

    public Piece get(Position p) {
        return grid[p.x()][p.y()];
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
        return boardXSize;
    }

    public int getBoardYSize() {
        return boardYSize;
    }

    public int getXSize() {
        return grid.length;
    }

    public int getYSize() {
        return grid[0].length;
    }

    private String toString(Move move) {
        // TODO: Unify this with a drawing strategy thing to also cover the AWT drawing
        // (which is currently done in the GamePanel class)
        final int xStep = 2;
        final int yStep = 2;
        final CharDrawing drawing = charCanvas.newDrawing(getXSize() * xStep, getYSize() * yStep);

        for (Position p : getPositions().getAll()) {
            final int x = p.x() * xStep;
            final int y = p.y() * yStep;

            for (Direction d : directions) {
                final Move step = new Move(p, d.addTo(p));
                if (isStepAlongLine(step)) {
                    drawing.addLine(x, y, xStep, yStep, step);
                }
            }
            final Piece piece = get(p);
            if (piece == null && positions.isBoard(p)) {
                drawing.addChar(x, y, '+');
            }
            if (piece != null){
                drawing.addChar(x, y, piece.asChar());
            }
        }
        return lineSeparator() + move.toString()
                + lineSeparator() + drawing.toString();
    }

    private final List<Runnable> discardListeners = new ArrayList<>();

    public void addDiscardListener(Runnable listener) {
        discardListeners.add(listener);
    }

    public void discard() {
        discardListeners.forEach(Runnable::run);
    }

    public boolean isBoardSize(Dimension boardSize) {
        return boardSize.width == getBoardXSize() && boardSize.height == getBoardYSize();
    }

    public Direction[] getDirections() {
        return directions;
    }

    public int getMaxStep() {
        return (max(boardXSize, boardYSize) / 2) - 1;
    }
}
