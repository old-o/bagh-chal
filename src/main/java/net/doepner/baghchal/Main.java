package net.doepner.baghchal;

import net.doepner.baghchal.control.GameLoop;
import net.doepner.baghchal.control.Player;
import net.doepner.baghchal.control.PredatorStrategy;
import net.doepner.baghchal.control.PreyStrategy;
import net.doepner.baghchal.control.UserPlayer;
import net.doepner.baghchal.model.GameTable;
import net.doepner.baghchal.model.GameTableFactory;
import net.doepner.baghchal.model.Levels;
import net.doepner.baghchal.resources.AudioPlayer;
import net.doepner.baghchal.theming.Themes;
import net.doepner.baghchal.view.GameFrame;
import net.doepner.baghchal.view.GamePanel;
import org.guppy4j.Executable;
import org.guppy4j.SimpleClassPathScanner;
import org.guppy4j.log.LogProvider;
import org.guppy4j.log.Slf4jLogProvider;

import javax.swing.SpinnerNumberModel;
import java.awt.Dimension;

import static net.doepner.baghchal.model.Piece.PREDATOR;
import static net.doepner.baghchal.model.Piece.PREY;
import static net.doepner.baghchal.theming.Theme.SoundResourceId.CONGRATS;

/**
 * Entry point of the game
 */
public final class Main {

    @SuppressWarnings("OverlyCoupledMethod") //
    public static void main(String... args) {

        System.setProperty("sun.java2d.opengl", "true");

        final LogProvider logProvider = new Slf4jLogProvider();

        final int maxLevel = 2;
        final Dimension defaultBoardSize = new Dimension(5, 5);

        final Themes themes = new Themes(new SimpleClassPathScanner(),
                "/net/doepner/baghchal/themes", "%s/%s.%s",
                "goats-and-tigers");

        final Levels levels = new Levels(maxLevel);

        final AudioPlayer audioPlayer = new AudioPlayer();

        final GameTableFactory gameTableFactory = (xSize, ySize) -> new GameTable(
                logProvider, xSize, ySize, new Setup(), new EventSounds(audioPlayer, themes));

        final GamePanel gamePanel = new GamePanel(gameTableFactory, defaultBoardSize, themes, levels);

        final Player preyStrategy = new PreyStrategy();
        final Player preyPlayer = new UserPlayer(PREY, gamePanel, themes);
        final Player predatorStrategy = new PredatorStrategy(levels);
        final Player predatorPlayer = new UserPlayer(PREDATOR, gamePanel, themes);

        final GameFrame gameFrame = new GameFrame(logProvider, gamePanel, themes,
                new SpinnerNumberModel(5, 4, 99, 1),
                new SpinnerNumberModel(5, 4, 99, 1));

        final Executable congrats = () -> audioPlayer.play(themes.getSoundResource(CONGRATS));

        final GameLoop gameLoop = new GameLoop(logProvider, gameFrame, levels, congrats,
                preyPlayer, predatorStrategy);

        gameLoop.start();
    }

}
