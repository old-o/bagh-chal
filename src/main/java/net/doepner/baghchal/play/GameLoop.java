package net.doepner.baghchal.play;

import net.doepner.baghchal.model.Board;
import net.doepner.baghchal.model.Levels;
import net.doepner.baghchal.model.Move;
import net.doepner.baghchal.resources.Sound;
import net.doepner.baghchal.ui.BoardPanel;
import net.doepner.baghchal.ui.GameFrame;

/**
 * Loop for the turn-taking of players
 */
public class GameLoop {

    private final GameFrame gameFrame;
    private final Board board;
    private final BoardPanel boardPanel;
    private final Levels levels;
    private final Sound sound;
    private final Player preyPlayer;
    private final Player predatorPlayer;

    public GameLoop(GameFrame gameFrame, Board board, BoardPanel boardPanel, Levels levels,
                    Sound sound, Player preyPlayer, Player predatorPlayer) {
        this.gameFrame = gameFrame;
        this.board = board;
        this.boardPanel = boardPanel;
        this.levels = levels;
        this.sound = sound;
        this.preyPlayer = preyPlayer;
        this.predatorPlayer = predatorPlayer;
    }

    public void start() {
        int moveCount = 0;
        while (!levels.isGameOver()) {
            final Player player = (moveCount % 2 == 0) ? preyPlayer : predatorPlayer;
            final Move move = player.play(board);
            board.processMove(move);
            moveCount++;
            boardPanel.repaint();
            final boolean playerGaveUp = (move == null);
            if (playerGaveUp) {
                // TODO: Make this dependent on whether the other player is the user
                sound.playResource("/net/doepner/baghchal/congrats.wav");
            }
            levels.setLevelDone(playerGaveUp);
            gameFrame.enableNextLevel(playerGaveUp && !levels.isGameOver());
            boardPanel.repaint();
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
