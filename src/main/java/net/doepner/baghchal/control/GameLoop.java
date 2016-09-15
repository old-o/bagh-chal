package net.doepner.baghchal.control;

import net.doepner.baghchal.model.GameTable;
import net.doepner.baghchal.model.Levels;
import net.doepner.baghchal.model.Move;
import net.doepner.baghchal.resources.Sound;
import net.doepner.baghchal.view.GamePanel;
import net.doepner.baghchal.view.GameFrame;

/**
 * Loop for the turn-taking of players
 */
public class GameLoop {

    private final GameFrame gameFrame;
    private final GamePanel gamePanel;
    private final GameTable gameTable;

    private final Sound sound;
    private final Levels levels;

    private final Player preyPlayer;
    private final Player predatorPlayer;

    public GameLoop(GameFrame gameFrame, GameTable gameTable, GamePanel gamePanel, Levels levels,
                    Sound sound, Player preyPlayer, Player predatorPlayer) {
        this.gameFrame = gameFrame;
        this.gameTable = gameTable;
        this.gamePanel = gamePanel;
        this.levels = levels;
        this.sound = sound;
        this.preyPlayer = preyPlayer;
        this.predatorPlayer = predatorPlayer;
    }

    public void start() {
        int moveCount = 0;
        while (!levels.isGameOver()) {
            final Player player = (moveCount % 2 == 0) ? preyPlayer : predatorPlayer;
            final Move move = player.play(gameTable);
            gameTable.processMove(move);
            moveCount++;
            gamePanel.repaint();
            final boolean playerGaveUp = (move == null);
            if (playerGaveUp) {
                // TODO: Make this dependent on whether the other player is the user
                sound.playResource("/net/doepner/baghchal/congrats.wav");
            }
            levels.setLevelDone(playerGaveUp);
            gameFrame.enableNextLevel(playerGaveUp && !levels.isGameOver());
            gamePanel.repaint();
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
