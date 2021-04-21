package org.oldo.baghchal.control;

import org.guppy4j.log.Log;
import org.guppy4j.log.LogProvider;
import org.guppy4j.run.Executable;
import org.guppy4j.run.Startable;
import org.oldo.baghchal.model.GameTable;
import org.oldo.baghchal.model.Levels;
import org.oldo.baghchal.model.Move;
import org.oldo.baghchal.model.Piece;
import org.oldo.baghchal.model.Players;
import org.oldo.baghchal.view.GameFrame;

import static java.lang.System.lineSeparator;
import static org.guppy4j.Booleans.not;
import static org.guppy4j.log.Log.Level.debug;

/**
 * Loop for the turn-taking of players
 */
public final class GameLoop implements Startable {

    private final Log log;

    private final GameFrame gameFrame;

    private final Levels levels;
    private final Executable congrats;
    private final Players players;

    public GameLoop(LogProvider logProvider, GameFrame gameFrame,
                    Levels levels, Executable congrats, Players players) {
        log = logProvider.getLog(getClass());
        this.gameFrame = gameFrame;
        this.levels = levels;
        this.congrats = congrats;
        this.players = players;
    }

    public void start() {
        gameFrame.start();

        while (not(levels.isGameOver())) {
            for (Piece piece : players.getPieces()) {
                try {
                    processTurn(piece);

                } catch (PlayerInterruptedException e) {
                    log.as(debug, e);
                    break;
                }
            }
        }
    }

    private void processTurn(Piece piece) {
        final GameTable gameTable = gameFrame.getGameTable();
        final Move move = players.play(gameTable, piece);
        if (move != null) {
            gameTable.movePiece(move);
            log.as(debug, lineSeparator() + move + lineSeparator() + gameTable);
        }
        final boolean playerGaveUp = (move == null);
        levels.setLevelDone(playerGaveUp);
        if (playerGaveUp && players.isPlayedByComputer(piece)) {
            congrats.execute();
        }
        gameFrame.repaintView();
        if (playerGaveUp) {
            gameTable.reset();
        }
        final boolean nextLevel = playerGaveUp && not(levels.isGameOver());
        gameFrame.enableNextLevel(nextLevel);
    }

}
