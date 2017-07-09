package net.doepner.baghchal.control;

import net.doepner.baghchal.model.Levels;
import net.doepner.baghchal.model.Move;
import net.doepner.baghchal.view.GameFrame;
import net.doepner.baghchal.view.GamePanel;
import org.guppy4j.log.Log;
import org.guppy4j.log.LogProvider;
import org.guppy4j.run.Executable;

import java.util.Arrays;

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
                    applyMove(player.play(gamePanel.getGameTable()));
                } catch (PlayerInterruptedException e) {
                    log.as(debug, e);
                    break;
                }
            }
        }
    }

    private void applyMove(Move move) {
        final boolean playerGaveUp = move == null;
        if (playerGaveUp) {
            // TODO: Make this dependent on whether the other player is the user
            congrats.execute();
            gamePanel.getGameTable().reset();
        } else {
            gamePanel.getGameTable().processMove(move);
        }
        gamePanel.repaint();
        levels.setLevelDone(playerGaveUp);
        gameFrame.enableNextLevel(playerGaveUp && !levels.isGameOver());
    }
}
