package net.doepner.baghchal;

import net.doepner.baghchal.control.GameLoop;
import net.doepner.baghchal.control.Player;
import net.doepner.baghchal.control.PredatorStrategy;
import net.doepner.baghchal.control.PreyStrategy;
import net.doepner.baghchal.control.UserPlayer;
import net.doepner.baghchal.model.GameTable;
import net.doepner.baghchal.model.Levels;
import net.doepner.baghchal.model.Themes;
import net.doepner.baghchal.resources.AudioPlayer;
import net.doepner.baghchal.view.GameFrame;
import net.doepner.baghchal.view.GamePanel;
import org.guppy4j.log.LogProvider;
import org.guppy4j.log.Slf4jLogProvider;

import java.awt.Dimension;

import static net.doepner.baghchal.model.Piece.PREDATOR;
import static net.doepner.baghchal.model.Piece.PREY;

/**
 * Entry point of the game
 */
public final class Main {

    public static void main(String... args) {

        System.setProperty("sun.java2d.opengl", "true");

        final LogProvider logProvider = new Slf4jLogProvider();

        final int maxLevel = 2;
        final Dimension boardSize = new Dimension(5, 5);

        final Themes themes = new Themes("/net/doepner/baghchal/themes", "%s/%s.%s",
                "goats-and-tigers");
        final Levels levels = new Levels(maxLevel);

        final AudioPlayer audioPlayer = new AudioPlayer();

        final GameTable gameTable = new GameTable(logProvider, boardSize.width, boardSize.height, new Setup(),
                new EventSounds(audioPlayer, themes));
        final GamePanel gamePanel = new GamePanel(gameTable, themes, levels);

        final Player preyStrategy = new PreyStrategy();
        final Player preyPlayer = new UserPlayer(PREY, gamePanel, themes);
        final Player predatorStrategy = new PredatorStrategy(levels);
        final Player predatorPlayer = new UserPlayer(PREDATOR, gamePanel, themes);

        final GameFrame gameFrame = new GameFrame(gamePanel, themes);
        final GameLoop gameLoop = new GameLoop(gameFrame, gameTable, gamePanel, levels, audioPlayer, themes,
                preyPlayer, predatorStrategy);

        gameLoop.start();
    }

}
