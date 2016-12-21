package net.doepner.baghchal.control;

import net.doepner.baghchal.model.Levels;
import net.doepner.baghchal.model.Move;
import net.doepner.baghchal.resources.AudioPlayer;
import net.doepner.baghchal.theming.Theme;
import net.doepner.baghchal.view.GameFrame;
import net.doepner.baghchal.view.GamePanel;
import org.guppy4j.WaitClock;

import java.util.Arrays;

import static net.doepner.baghchal.theming.Theme.SoundResourceId.CONGRATS;

/**
 * Loop for the turn-taking of players
 */
public final class GameLoop {

    private final GameFrame gameFrame;
    private final GamePanel gamePanel;

    private final AudioPlayer audioPlayer;
    private final Levels levels;

    private final Theme theme;
    private final Iterable<Player> players;

    private final WaitClock waitClock = new WaitClock(100);

    public GameLoop(GameFrame gameFrame, GamePanel gamePanel, Levels levels,
                    AudioPlayer audioPlayer, Theme theme, Player... players) {
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
                final Move move = player.play(gamePanel.getGameTable());
                if (move == null && player instanceof UserPlayer) {
                    break;
                }
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
            }
        }
    }
}
