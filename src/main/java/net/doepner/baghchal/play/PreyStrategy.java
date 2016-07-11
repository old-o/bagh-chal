package net.doepner.baghchal.play;

import static net.doepner.baghchal.model.Piece.PREDATOR;
import static net.doepner.baghchal.model.Piece.PREY;
import static net.doepner.baghchal.util.ListUtil.getRandomFrom;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.doepner.baghchal.model.GameTable;
import net.doepner.baghchal.model.Move;
import net.doepner.baghchal.model.Piece;
import net.doepner.baghchal.model.Position;

/**
 * Computer player for prey pieces
 */
public final class PreyStrategy implements Player {

    @Override
    public String getName() {
        return "Prey AI";
    }

    @Override
    public Move play(GameTable gameTable) {
        final Move defensiveMove = gameTable.tryMoveFrom(getDefensiveMoves(gameTable));
        if (defensiveMove != null) {
            return defensiveMove;
        }
        final Position borderPosition = gameTable.getBorderPosition(PREY);
        if (borderPosition != null) {
            final Position boardPosition = getRandomFrom(getSafeBoardPositions(gameTable));
            if (boardPosition != null) {
                return getMove(gameTable, borderPosition, boardPosition);
            }
        }

        // 2) place on the board edge next to a prey if possible
        // 3) place in a position that cannot be jumped over, preferably next to other prey that can also not be jumped over
        // 3a) if there is a choice to block off an empty position (make it unreachable for predator, do it
        // 3b) if a predator can be safely blocked, then do it

        return null;
    }

    private Move getMove(GameTable gameTable, Position borderPosition, Position boardPosition) {
        final Move m = new Move(borderPosition, boardPosition);
        gameTable.movePiece(m);
        return m;
    }

    private List<Position> getSafeBoardPositions(GameTable gameTable) {
        final Set<Position> positions = new HashSet<>();
        for (Position p : gameTable.getBoardPositions()) {
            if (isSafePosition(p, gameTable)) {
                positions.add(p);
            }
        }
        return new ArrayList<>(positions);
    }

    private boolean isSafePosition(Position p, GameTable gameTable) {
        if (!gameTable.isEmptyAt(p)) {
            return false;
        }
        for (Position d : gameTable.getDirections()) {
            final Move m = new Move(p, p.add(d));
            if (gameTable.isStepAlongLine(m)) {
                final Position p3 = p.add(-m.xStep(), -m.yStep());
                if (gameTable.isBoardPosition(p3)) {
                    final Piece piece2 = gameTable.get(m.p2());
                    final Piece piece3 = gameTable.get(p3);
                    if ((piece2 == null || piece2 == PREDATOR)
                            && (piece3 == null || piece3 == PREDATOR)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private List<Move> getDefensiveMoves(GameTable gameTable) {
        final List<Move> defenseMoves = new ArrayList<>();
        for (Move possibleJump : gameTable.getPossibleJumps(PREDATOR, PREY)) {
            final Position p2 = possibleJump.p2();
            for (Position p1 : gameTable.getAllPositions()) {
                if (gameTable.get(p1) == PREY) {
                    final Move move = new Move(p1, p2);
                    if (gameTable.isValid(move, PREY)) {
                        defenseMoves.add(move);
                    }
                }
            }
        }
        return defenseMoves;
    }
}
