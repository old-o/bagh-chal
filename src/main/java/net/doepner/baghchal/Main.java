package net.doepner.baghchal;

import net.doepner.baghchal.control.GameLoop;
import net.doepner.baghchal.control.Player;
import net.doepner.baghchal.control.PredatorStrategy;
import net.doepner.baghchal.control.UserPlayer;
import net.doepner.baghchal.model.GameTable;
import net.doepner.baghchal.model.Levels;
import net.doepner.baghchal.resources.Images;
import net.doepner.baghchal.resources.LevelProperties;
import net.doepner.baghchal.resources.LevelResources;
import net.doepner.baghchal.resources.Sound;
import net.doepner.baghchal.view.GameFrame;
import net.doepner.baghchal.view.GamePanel;
import org.guppy4j.log.LogProvider;
import org.guppy4j.log.Slf4jLogProvider;

import java.awt.Dimension;

import static net.doepner.baghchal.model.Piece.PREY;

/**
 * Entry point of the game
 */
public final class Main {

    public static void main(String... args) {

        System.setProperty("sun.java2d.opengl", "true");

        final LogProvider logProvider = new Slf4jLogProvider();

        final int maxLevel = 2;
        final Dimension boardSize = new Dimension(10, 3);
        final Dimension preferredSize = new Dimension(500, 500);

        final LevelResources levelResources = new LevelResources("/net/doepner/baghchal/levels/%d/%s");
        final LevelProperties levelProperties = new LevelProperties(levelResources, "level.properties");
        final Levels levels = new Levels(levelProperties, levelResources, maxLevel);

        final Sound sound = new Sound(levels);
        final Images images = new Images(levels);

        final GameTable gameTable = new GameTable(logProvider, boardSize.width, boardSize.height, new Setup(), new Sounds(sound));
        final GamePanel gamePanel = new GamePanel(gameTable, images, levels);

//        final Player preyPlayer = new PreyStrategy();
        final Player preyPlayer = new UserPlayer(PREY, gamePanel, images);
        final Player predatorPlayer = new PredatorStrategy(levels);
//        final Player predatorPlayer = new UserPlayer(PREDATOR, gamePanel, images);

        final GameFrame gameFrame = new GameFrame(gamePanel);
        final GameLoop gameLoop = new GameLoop(gameFrame, gameTable, gamePanel, levels, sound, preyPlayer, predatorPlayer);

        gamePanel.start();
        gameFrame.show(preferredSize);
        gameLoop.start();
    }

}
