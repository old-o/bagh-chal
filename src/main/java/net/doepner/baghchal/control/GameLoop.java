package net.doepner.baghchal.control;

import net.doepner.baghchal.model.GameTable;
import net.doepner.baghchal.model.Levels;
import net.doepner.baghchal.model.Move;
import net.doepner.baghchal.view.GameFrame;
import net.doepner.baghchal.view.GamePanel;
import org.guppy4j.log.Log;
import org.guppy4j.log.LogProvider;
import org.guppy4j.run.Executable;

import java.util.Arrays;

import static java.lang.System.lineSeparator;
import static org.guppy4j.log.Log.Level.debug;

/**
 * Loop for the turn-taking of players
 */
public final class GameLoop {

    private final Log log;

    private final GameFrame gameFrame;
    private final GamePanel gamePanel;

    private final Levels levels;
    private final Executable congrats;
    private final Iterable<Player> players;

    public GameLoop(LogProvider logProvider, GameFrame gameFrame,
                    Levels levels, Executable congrats, Player... players) {
        log = logProvider.getLog(getClass());
        this.gameFrame = gameFrame;
        gamePanel = gameFrame.getGamePanel();
        this.levels = levels;
        this.congrats = congrats;
        this.players = Arrays.asList(players);
    }

    public void start() {
        gamePanel.start();
        gameFrame.show();

        while (!levels.isGameOver()) {
            for (Player player : players) {
                try {
                    processTurn(player);

                } catch (PlayerInterruptedException e) {
                    log.as(debug, e);
                    break;
                }
            }
        }
    }

    private void processTurn(Player player) {
        if (player.isComputer()) {
            sleepSeconds(1);
        }
        final GameTable gameTable = gamePanel.getGameTable();
        final Move move = player.play(gameTable);
        if (move != null) {
            gameTable.movePiece(move);
            log.as(debug, lineSeparator() + move + lineSeparator() + gameTable);
        }
        final boolean playerGaveUp = (move == null);
        levels.setLevelDone(playerGaveUp);
        if (playerGaveUp && player.isComputer()) {
            congrats.execute();
        }
        gamePanel.repaint();
        if (playerGaveUp) {
            gameTable.reset();
        }
        gameFrame.enableNextLevel(playerGaveUp && !levels.isGameOver());
    }

    private void sleepSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            log.as(debug, e);
        }
    }
}
