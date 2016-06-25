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

    public GameLoop(GameFrame gameFrame, Board board, BoardPanel boardPanel, Levels levels, Sound sound, Player preyPlayer, Player predatorPlayer) {
        this.gameFrame = gameFrame;
        this.board = board;
        this.boardPanel = boardPanel;
        this.levels = levels;
        this.sound = sound;
        this.preyPlayer = preyPlayer;
        this.predatorPlayer = predatorPlayer;
    }

    public void start() {
        while (!levels.isGameOver()) {
            preyPlayer.play(board);
            final Move predatorMove = predatorPlayer.play(board);
            final boolean predatorsLostLevel = (predatorMove == null);
            if (predatorsLostLevel) {
                sound.playResource("/net/doepner/baghchal/congrats.wav");
            }
            levels.setLevelDone(predatorsLostLevel);
            gameFrame.enableNextLevel(predatorsLostLevel && !levels.isGameOver());
            boardPanel.repaint();
        }
    }
}
