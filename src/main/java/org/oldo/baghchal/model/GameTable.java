package org.oldo.baghchal.model;

import org.guppy4j.log.LogProvider;
import org.oldo.baghchal.Listener;
import org.oldo.g2d.Size;
import org.oldo.text.CharCanvas;
import org.oldo.text.CharDrawing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import static org.oldo.baghchal.model.Direction.*;

/**
 * The game board model
 */
public final class GameTable {

    private static final Direction[] directions = {RIGHT_UP, RIGHT, RIGHT_DOWN, DOWN};

    private final LogProvider logProvider;

    private final Size boardSize;

    private final CharCanvas charCanvas;

    private final Piece[][] grid;

    private final Consumer<GameTable> setupMethod;
    private final Listener listener;
    private final TablePositions positions;

    public GameTable(LogProvider logProvider, Size boardSize,
                     Consumer<GameTable> setupMethod, Listener listener,
                     CharCanvas charCanvas) {
        this.logProvider = logProvider;
        this.setupMethod = setupMethod;
        this.listener = listener;
        this.boardSize = boardSize;
        this.charCanvas = charCanvas;
        grid = new Piece[boardSize.getX() + 2][boardSize.getY() + 2];
        final Position topLeft = new Position(1, 1);
        final Position bottomRight = new Position(boardSize.getX(), boardSize.getY());
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
        this(gt.logProvider, gt.boardSize, gt.setupMethod, Listener.NONE, gt.charCanvas);
        for (int x = 0; x < grid.length; x++) {
            System.arraycopy(gt.grid[x], 0, grid[x], 0, grid[x].length);
        }
    }

    public GameTable copy() {
        return new GameTable(this);
    }

    public void movePiece(Move move) {
        final Piece piece = get(move.p1());
        if (piece == null) {
            throw new IllegalStateException("Cannot move piece from empty position:" + move.p1());
        } else {
            clear(move.p1());
            set(move.p2(), piece);
        }
        if (move.isJump() && positions.isBoardMove(move)) {
            clear(move.middle());
            listener.afterJump(piece);
        } else {
            listener.afterStep(piece);
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
        return move.isStep() && (move.p1().hasEvenCoordSum() || move.isOneDimensional())
                && positions.isBoardMove(move);
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
        try {
            return !move.isStationary() && isEmptyAt(move.p2()) && piece.isValid(move, this);
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
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
        return boardSize.getX();
    }

    public int getBoardYSize() {
        return boardSize.getY();
    }

    public int getXSize() {
        return grid.length;
    }

    public int getYSize() {
        return grid[0].length;
    }

    public String toString() {
        final int xStep = 2;
        final int yStep = 2;

        final CharDrawing drawing = charCanvas.newDrawing(getXSize() * xStep, getYSize() * yStep);

        for (Position p : positions.getAll()) {
            final int x = p.x() * xStep;
            final int y = p.y() * yStep;

            for (Move step : getStepsAlongLineFrom(p)) {
                drawing.addLine(x, y, xStep, yStep, step);
            }
            final Piece piece = get(p);
            if (piece != null){
                drawing.addChar(x, y, piece.asChar());
            } else if (positions.isBoard(p)) {
                drawing.addChar(x, y, getBoardChar(p));
            }
        }
        return drawing.toString();
    }

    private char getBoardChar(Position p) {
        if (isBorder(p, UP)) {
            return isBorder(p, LEFT) ? '┌' : isBorder(p, RIGHT) ? '┐' : '┬';
        }
        if (isBorder(p, DOWN)) {
            return isBorder(p, LEFT) ? '└' : isBorder(p, RIGHT) ? '┘' : '┴';
        }
        // otherwise
        return isBorder(p, LEFT) ? '├' : isBorder(p, RIGHT) ? '┤' : '┼';
    }

    private boolean isBorder(Position p, Direction up) {
        return positions.isBorder(up.addTo(p));
    }

    private final Collection<Runnable> discardListeners = new ArrayList<>();

    public void addDiscardListener(Runnable listener) {
        discardListeners.add(listener);
    }

    public void discard() {
        discardListeners.forEach(Runnable::run);
    }

    public boolean isBoardSize(Size size) {
        return boardSize != null && boardSize.isSameAs(size);
    }

    public Iterable<Move> getStepsAlongLineFrom(Position p) {
        final Collection<Move> steps = new ArrayList<>();
        for (Direction d : directions) {
            final Move m = new Move(p, d.addTo(p));
            if (isStepAlongLine(m)) {
                steps.add(m);
            }
        }
        return steps;
    }

    public int getMaxStepFromCorner() {
        return (boardSize.getMaxDimension() / 2) - 1;
    }
}
