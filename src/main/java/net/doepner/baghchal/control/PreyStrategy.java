package net.doepner.baghchal.control;

import net.doepner.baghchal.model.Direction;
import net.doepner.baghchal.model.GameTable;
import net.doepner.baghchal.model.Move;
import net.doepner.baghchal.model.Piece;
import net.doepner.baghchal.model.Position;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static net.doepner.baghchal.model.Piece.PREDATOR;
import static net.doepner.baghchal.model.Piece.PREY;
import static org.guppy4j.Lists.getRandomFrom;

/**
 * Computer player for prey pieces
 */
public final class PreyStrategy implements Player {

    @Override
    public Move play(GameTable gameTable) {
        final Move defensiveMove = getRandomFrom(getDefensiveMoves(gameTable));
        if (defensiveMove != null) {
            return defensiveMove;
        }
        final Position borderPosition = gameTable.getBorderPosition(PREY);
        if (borderPosition != null) {
            final List<Position> safeBoardPositions = getBoardPositions(gameTable, PreyStrategy::rejectUnsafe);
            final Move move = getMove(borderPosition, safeBoardPositions.isEmpty()
                    ? getBoardPositions(gameTable, PreyStrategy::rejectDeadly) : safeBoardPositions);
            if (move != null) {
                return move;
            }
        }


        // 2) place on the board edge next to a prey if possible
        // 3) place in a position that cannot be jumped over, preferably next to other prey that can also not be jumped over
        // 3a) if there is a choice to block off an empty position (make it unreachable for predator, do it
        // 3b) if a predator can be safely blocked, then do it

        return null;
    }

    private interface Rejector {
        boolean rejects(Position p, GameTable table, Position fore, Position back);
    }

    private Move getMove(Position borderPosition, List<Position> safeBoardPositions) {
        final Position boardPosition = getRandomFrom(safeBoardPositions);
        return new Move(borderPosition, boardPosition);
    }

    @Override
    public boolean isComputer() {
        return true;
    }

    private static List<Position> getBoardPositions(GameTable gameTable, Rejector rejector) {
        final Set<Position> positions = new HashSet<>();
        for (Position p : gameTable.getPositions().getBoard()) {
            if (gameTable.isEmptyAt(p) && isOkPosition(p, gameTable, rejector)) {
                positions.add(p);
            }
        }
        return new ArrayList<>(positions);
    }

    private static boolean isOkPosition(Position p, GameTable table, Rejector rejector) {
        for (Direction d : Direction.values()) {
            final Position fore = d.addTo(p);
            final Position back = d.subtractFrom(p);

            if (rejector.rejects(p, table, fore, back)) {
                // not safe because both neighboring fields are empty or occupied by predator
                // which means on this position we could be jumped over and killed
                return false;
            }
        }
        return true;
    }

    private static boolean rejectUnsafe(Position p, GameTable table, Position fore, Position back) {
        return table.isStepAlongLine(new Move(p, fore)) && table.getPositions().isBoard(back)
                && isEmptyOrPredator(table.get(fore)) && isEmptyOrPredator(table.get(back));
    }

    private static boolean rejectDeadly(Position p, GameTable table, Position fore, Position back) {
        return table.isStepAlongLine(new Move(p, fore)) && table.getPositions().isBoard(back)
                && (isPredator(table.get(fore)) || isPredator(table.get(back)));
    }

    private static boolean isPredator(Piece piece) {
        return piece == PREDATOR;
    }

    private static boolean isEmptyOrPredator(Piece piece) {
        return (piece == null) || (piece == PREDATOR);
    }

    private static List<Move> getDefensiveMoves(GameTable gameTable) {
        final List<Move> defenseMoves = new ArrayList<>();
        for (Move possibleJump : gameTable.getPossibleJumps(PREDATOR, PREY)) {
            final Position p2 = possibleJump.p2();
            for (Position p1 : gameTable.getPositions().getAll()) {
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
