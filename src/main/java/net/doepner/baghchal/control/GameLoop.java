package net.doepner.baghchal.control;

import net.doepner.baghchal.model.GameTable;
import net.doepner.baghchal.model.Levels;
import net.doepner.baghchal.model.Move;
import net.doepner.baghchal.model.Piece;
import net.doepner.baghchal.model.Players;
import net.doepner.baghchal.view.GameFrame;
import net.doepner.baghchal.view.GameView;
import org.guppy4j.log.Log;
import org.guppy4j.log.LogProvider;
import org.guppy4j.run.Executable;
import org.guppy4j.run.Startable;

import static java.lang.System.lineSeparator;
import static org.guppy4j.log.Log.Level.debug;

/**
 * Loop for the turn-taking of players
 */
public final class GameLoop implements Startable {

    private final Log log;

    private final GameFrame gameFrame;
    private final GameView gamePanel;

    private final Levels levels;
    private final Executable congrats;
    private final Players players;

    public GameLoop(LogProvider logProvider, GameFrame gameFrame,
                    Levels levels, Executable congrats, Players players) {
        log = logProvider.getLog(getClass());
        this.gameFrame = gameFrame;
        gamePanel = gameFrame.getGamePanel();
        this.levels = levels;
        this.congrats = congrats;
        this.players = players;
    }

    public void start() {
        gamePanel.start();
        gameFrame.show();

        while (!levels.isGameOver()) {
            for (Piece piece : Piece.values()) {
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
        final GameTable gameTable = gamePanel.getGameTable();
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
        gamePanel.repaint();
        if (playerGaveUp) {
            gameTable.reset();
        }
        gameFrame.enableNextLevel(playerGaveUp && !levels.isGameOver());
    }

}
