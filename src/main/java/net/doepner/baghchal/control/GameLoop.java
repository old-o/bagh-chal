package net.doepner.baghchal.control;

import net.doepner.baghchal.model.GameTable;
import net.doepner.baghchal.model.Levels;
import net.doepner.baghchal.model.Move;
import net.doepner.baghchal.resources.AudioPlayer;
import net.doepner.baghchal.view.GameFrame;
import net.doepner.baghchal.view.GamePanel;

import java.util.Arrays;

/**
 * Loop for the turn-taking of players
 */
public class GameLoop {

    private final GameFrame gameFrame;
    private final GamePanel gamePanel;
    private final GameTable gameTable;

    private final AudioPlayer audioPlayer;
    private final Levels levels;

    private final Iterable<Player> players;

    public GameLoop(GameFrame gameFrame, GameTable gameTable, GamePanel gamePanel, Levels levels,
                    AudioPlayer audioPlayer, Player... players) {
        this.gameFrame = gameFrame;
        this.gameTable = gameTable;
        this.gamePanel = gamePanel;
        this.levels = levels;
        this.audioPlayer = audioPlayer;
        this.players = Arrays.asList(players);
    }

    public void start() {
        while (!levels.isGameOver()) {
            for (Player player : players) {
                final Move move = player.play(gameTable);
                gameTable.processMove(move);
                gamePanel.repaint();
                final boolean playerGaveUp = (move == null);
                if (playerGaveUp) {
                    // TODO: Make this dependent on whether the other player is the user
                    audioPlayer.playResource("/net/doepner/baghchal/congrats.wav");
                }
                levels.setLevelDone(playerGaveUp);
                gameFrame.enableNextLevel(playerGaveUp && !levels.isGameOver());
                gamePanel.repaint();
            }
        }
    }

    private void waitSeconds(int secs) {
        try {
            Thread.sleep(secs * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
