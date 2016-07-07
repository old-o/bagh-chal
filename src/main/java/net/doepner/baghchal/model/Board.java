package net.doepner.baghchal.model;

import net.doepner.baghchal.BoardListener;
import net.doepner.baghchal.util.ListUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static net.doepner.baghchal.model.Piece.INVALID;
import static net.doepner.baghchal.model.Position.pos;

/**
 * The game board model
 */
public class Board {

    private final int xSize;
    private final int ySize;

    private final Position topLeft;
    private final Position bottomRight;

    private final Piece[][] b;

    private final BoardListener listener;

    private final List<Position> allPositions = new ArrayList<>();
    private final List<Position> boardPositions = new ArrayList<>();
    private final List<Position> borderPositions = new ArrayList<>();
    private final List<Position> cornerPositions = new ArrayList<>();

    private static Position[] directions = new Position[]{
            pos(0, +1), pos(+1, +1), pos(+1, 0), pos(+1, -1),
            pos(0, -1), pos(-1, -1), pos(-1, 0), pos(-1, +1)
    };

    public Board(int xSize, int ySize, BoardListener listener) {
        this.listener = listener;
        this.xSize = xSize;
        this.ySize = ySize;
        b = new Piece[xSize + 2][ySize + 2];
        topLeft = new Position(1, 1);
        bottomRight = new Position(xSize, ySize);
        initPositions(topLeft, bottomRight);
    }

    public Iterable<Position> getDirections() {
        return Arrays.asList(directions);
    }

    private void initPositions(Position topLeft, Position bottomRight) {
        for (int x = 0; x < b.length; x++) {
            for (int y = 0; y < b[x].length; y++) {
                final Position p = new Position(x, y);
                allPositions.add(p);
                if (topLeft.x() <= x && topLeft.y() <= y && x <= bottomRight.x() && y <= bottomRight.y()) {
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
     * Copy constructor that will copy the grid array of the provide board instance.
     * The resulting board will support no BoardListener functionality.
     *
     * @param board An existing board instance
     */
    private Board(Board board) {
        this(board.xSize, board.ySize, BoardListener.NONE);
        for (int x = 0; x < b.length; x++) {
            System.arraycopy(board.b[x], 0, b[x], 0, b[x].length);
        }
    }

    public Board copyBoard() {
        return new Board(this);
    }

    public void processMove(Move move) {
        if (move != null) {
            final Piece piece = get(move.p2());
            if (move.isJump()) {
                clear(move.middle());
                listener.afterJump(piece);
            } else {
                listener.afterStep(piece);
            }
        }
    }

    public Piece movePiece(Move move) {
        final Piece piece = get(move.p1());
        clear(move.p1());
        set(move.p2(), piece);
        return piece;
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

    public List<Move> getStepsWhere(Piece movingPiece, Piece requiredPiece) {
        final List<Move> steps = new ArrayList<>();
        for (Position p : boardPositions) {
            if (get(p) == movingPiece) {
                for (Position d : directions) {
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
        final Move move = ListUtil.getRandomFrom(moves);
        if (move != null) {
            movePiece(move);
        }
        return move;
    }

    public Iterable<Position> getAllPositions() {
        return allPositions;
    }

    public Iterable<Position> getBoardPositions() {
        return boardPositions;
    }

    public Iterable<Position> getCornerPositions() {
        return cornerPositions;
    }

    public void processStepAlongLine(Position p1, Position p2, Consumer<Move> moveProcessor) {
        final Move step = new Move(p1, p2);
        if (isStepAlongLine(step)) {
            moveProcessor.accept(step);
        }
    }

    public List<Move> getPossibleSteps(Piece movingPiece) {
        return getStepsWhere(movingPiece, null);
    }

    public List<Move> getPossibleJumps(Piece movingPiece, Piece requiredPiece) {
        final List<Move> jumps = new ArrayList<>();
        for (Move step : getStepsWhere(movingPiece, requiredPiece)) {
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
        return isValidOnBoardPosition(move.p1()) && isValidOnBoardPosition(move.p2()) && move.isStep()
                && (move.p1().hasEvenCoordSum() || move.isOneDimensional());
    }

    public boolean isValidOnBoardPosition(Position pos) {
        return pos.isGreaterOrEqualTo(topLeft) && pos.isLessOrEqualTo(bottomRight);
    }

    public void reset() {
        for (Piece[] pieces : b) {
            Arrays.fill(pieces, null);
        }
        listener.afterReset();
    }

    public Piece get(Position p) {
        return get(p.x(), p.y());
    }

    public void set(Position p, Piece piece) {
        set(p.x(), p.y(), piece);
    }

    private void clear(Position p) {
        set(p, null);
    }

    public boolean isEmptyAt(Position p) {
        return get(p) == null;
    }

    public Piece get(int x, int y) {
        try {
            return b[x][y];
        } catch (ArrayIndexOutOfBoundsException e) {
            return INVALID;
        }
    }

    public void set(int x, int y, Piece piece) {
        b[x][y] = piece;
    }

    public int getCentreXSize() {
        return xSize;
    }

    public int getCentreYSize() {
        return ySize;
    }

    public int getXSize() {
        return b.length;
    }

    public int getYSize() {
        return b[0].length;
    }

    public boolean isValid(Move move, Piece piece) {
        if (move.isStationary() || !isEmptyAt(move.p2())) {
            return false;
        }
        // TODO: Factor out the rules of the game (in a flexible way that also work for other games like Alquerque)
        switch (piece) {
            case PREY:
                return isBorderToBoard(move) || isValidOnBoardStep(move);
            case PREDATOR:
                return isStepAlongLine(move) || (move.isJump() && get(move.middle()) == Piece.PREY);
            default:
                return false;
        }
    }

    private boolean isValidOnBoardStep(Move move) {
        return isBorderEmpty() && isStepAlongLine(move);
    }

    private boolean isBorderToBoard(Move move) {
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

    public Position getTopLeft() {
        return topLeft;
    }

    public Position getBottomRight() {
        return bottomRight;
    }

    public Position getBorderPosition(Piece piece) {
        for (Position p : borderPositions) {
            if (get(p) == piece) {
                return p;
            }
        }
        return null;
    }
}
