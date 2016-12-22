package net.doepner.baghchal.control;

import net.doepner.baghchal.model.Levels;
import net.doepner.baghchal.model.Move;
import net.doepner.baghchal.resources.AudioPlayer;
import net.doepner.baghchal.theming.Theme;
import net.doepner.baghchal.view.GameFrame;
import net.doepner.baghchal.view.GamePanel;
import org.guppy4j.WaitClock;
import org.guppy4j.log.Log;
import org.guppy4j.log.LogProvider;

import java.util.Arrays;

import static net.doepner.baghchal.theming.Theme.SoundResourceId.CONGRATS;
import static org.guppy4j.log.Log.Level.debug;

/**
 * Loop for the turn-taking of players
 */
public final class GameLoop {

    private final Log log;

    private final GameFrame gameFrame;
    private final GamePanel gamePanel;

    private final AudioPlayer audioPlayer;
    private final Levels levels;

    private final Theme theme;
    private final Iterable<Player> players;

    private final WaitClock waitClock = new WaitClock(100);

    public GameLoop(LogProvider logProvider, GameFrame gameFrame, GamePanel gamePanel, Levels levels,
                    AudioPlayer audioPlayer, Theme theme, Player... players) {
        log = logProvider.getLog(getClass());
        this.gameFrame = gameFrame;
        this.gamePanel = gamePanel;
        this.levels = levels;
        this.audioPlayer = audioPlayer;
        this.theme = theme;
        this.players = Arrays.asList(players);
    }

    public void start() {
        gamePanel.start();
        gameFrame.show();
        while (!levels.isGameOver()) {
            for (Player player : players) {
                try {
                    final Move move = player.play(gamePanel.getGameTable());
                    gamePanel.getGameTable().processMove(move);
                    gamePanel.repaint();
                    final boolean playerGaveUp = move == null;
                    if (playerGaveUp) {
                        // TODO: Make this dependent on whether the other player is the user
                        audioPlayer.play(theme.getSoundResource(CONGRATS));
                    }
                    levels.setLevelDone(playerGaveUp);
                    gameFrame.enableNextLevel(playerGaveUp && !levels.isGameOver());
                    gamePanel.repaint();
                } catch (PlayerInterruptedException e) {
                    log.as(debug, e);
                    break;
                }
            }
        }
    }
}
