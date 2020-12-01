package org.oldo.baghchal.control;

import org.oldo.baghchal.model.GameTable;
import org.oldo.baghchal.model.Levels;
import org.oldo.baghchal.model.Move;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.guppy4j.Lists.getRandomFrom;
import static org.oldo.baghchal.model.Piece.PREDATOR;
import static org.oldo.baghchal.model.Piece.PREY;

/**
 * The computer player's strategy (playing the tigers)
 */
public final class PredatorStrategy implements Player {

    private final Levels levels;

    public PredatorStrategy(Levels levels) {
        this.levels = levels;
    }

    @Override
    public Move play(GameTable gameTable) {
        final Move take = getRandomFrom(gameTable.getPossibleJumps(PREDATOR, PREY));
        if (take != null) {
            return take;
        }
        final List<Move> possibleSteps = gameTable.getStepsWhereAdjacent(PREDATOR, null);
        if (possibleSteps.isEmpty()) {
            return null;
        }
        final Move threateningMove = tryThreateningMove(levels.getLevel(), possibleSteps, gameTable);
        return threateningMove != null ? threateningMove : getRandomFrom(possibleSteps);
    }

    @Override
    public boolean isComputer() {
        return true;
    }

    private static Move tryThreateningMove(int level, Iterable<Move> possibleMoves, GameTable gameTable) {
        final Map<Integer, List<Move>> threateningMoves = getThreateningMoves(possibleMoves, gameTable);
        for (int i = level; i > 0; i--) {
            final Move move = getRandomFrom(threateningMoves.get(i));
            if (move != null) {
                return move;
            }
        }
        return null;
    }

    private static Map<Integer, List<Move>> getThreateningMoves(Iterable<Move> possibleMoves, GameTable gameTable) {
        final Map<Integer, List<Move>> threateningMoves = new HashMap<>();
        for (int i = 1; i <= 8; i++) {
            threateningMoves.put(i, new ArrayList<>());
        }
        for (Move m : possibleMoves) {
            final int npt = numberOfPiecesThreatened(m, gameTable);
            if (npt > 0) {
                threateningMoves.get(npt).add(m);
            }
        }
        return threateningMoves;
    }

    private static int numberOfPiecesThreatened(Move m, GameTable gameTable) {
        final GameTable b = gameTable.copy();
        b.movePiece(m);
        final Collection<Move> jumps = new ArrayList<>();
        for (Move step : b.getStepsWhereAdjacent(PREDATOR, PREY)) {
            b.addPossibleJump(jumps, step);
        }
        return jumps.size();
    }
}