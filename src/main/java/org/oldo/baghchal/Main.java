package org.oldo.baghchal;

import com.formdev.flatlaf.FlatDarkLaf;
import org.guppy4j.io.SimpleClassPathScanner;
import org.guppy4j.log.Log;
import org.guppy4j.log.LogProvider;
import org.guppy4j.log.Slf4jLogProvider;
import org.guppy4j.run.Executable;
import org.guppy4j.run.Startable;
import org.oldo.baghchal.control.GameLoop;
import org.oldo.baghchal.control.Player;
import org.oldo.baghchal.control.PredatorStrategy;
import org.oldo.baghchal.control.PreyStrategy;
import org.oldo.baghchal.control.UserPlayer;
import org.oldo.baghchal.model.GameTable;
import org.oldo.baghchal.model.GameTableFactory;
import org.oldo.baghchal.model.Levels;
import org.oldo.baghchal.model.Players;
import org.oldo.baghchal.resources.AudioUrlPlayer;
import org.oldo.baghchal.theming.Themes;
import org.oldo.baghchal.view.GameFrame;
import org.oldo.baghchal.view.GamePanel;
import org.oldo.baghchal.view.GameView;
import org.oldo.g2d.IntPair;
import org.oldo.text.CharCanvasImpl;

import javax.swing.SpinnerNumberModel;
import java.net.URL;
import java.util.function.Consumer;

import static java.lang.invoke.MethodHandles.lookup;
import static org.oldo.baghchal.model.Piece.PREDATOR;
import static org.oldo.baghchal.model.Piece.PREY;
import static org.oldo.baghchal.theming.Theme.SoundResourceId.CONGRATS;

/**
 * Entry point of the game
 */
public final class Main {

    // Simplest Dependency Injection is done here without a DI framework. See
    // https://odoepner.wordpress.com/2017/01/17/diy-dependency-inject-yourself/
    @SuppressWarnings("OverlyCoupledMethod")
    public static void main(String... args) {

        final LogProvider logProvider = new Slf4jLogProvider();
        final Log log = logProvider.getLog(lookup().lookupClass());

        Thread.setDefaultUncaughtExceptionHandler(new FailHandler(log));

        new AudioSystemChecker().run();
        FlatDarkLaf.install();

        System.setProperty("sun.java2d.opengl", "true");

        final IntPair defaultBoardSize = new IntPair(5, 5);

        final String resourceBasePath = "/org/oldo/baghchal/themes";
        final Themes themes = new Themes(new SimpleClassPathScanner(), resourceBasePath, "%s/%s.%s");

        final int maxLevel = 2;
        final Levels levels = new Levels(maxLevel);

        final Consumer<URL> audioPlayMethod = url -> {
            if (url != null) {
                AudioUrlPlayer.play(url);
            }
        };

        final Consumer<GameTable> tableSetupMethod = BaghChalSetup::prepare;
//        final Consumer<GameTable> tableSetupMethod = gameTable -> AlquerqueSetup.prepare(gameTable, PREDATOR, PREY);

        final EventSounds listener = new EventSounds(audioPlayMethod, themes);

        final CharCanvasImpl charCanvas = new CharCanvasImpl();

        final GameTableFactory gameTableFactory =
                (size) -> new GameTable(logProvider, size, tableSetupMethod, listener, charCanvas);

        final GameView gamePanel = new GamePanel(gameTableFactory, defaultBoardSize, themes, levels);

        final Player preyStrategy = new PreyStrategy();
//        final Player preyStrategy = new PredatorStrategy(levels, PREY, PREDATOR);
        final Player preyPlayer = new UserPlayer(PREY, gamePanel);
        final Player predatorStrategy = new PredatorStrategy(levels, PREDATOR, PREY);
        final Player predatorPlayer = new UserPlayer(PREDATOR, gamePanel);

        final Players players = new Players(logProvider, preyStrategy, preyPlayer, predatorStrategy, predatorPlayer);

        final GameFrame gameFrame = new GameFrame("Bagh-Chal", logProvider, gamePanel, themes,
                new SpinnerNumberModel(5, 4, 99, 1),
                new SpinnerNumberModel(5, 4, 99, 1),
                players, PREDATOR, "Predators", PREY, "Prey");

        final Executable congrats = () -> AudioUrlPlayer.play(themes.getSoundResource(CONGRATS));

        final Startable gameLoop = new GameLoop(logProvider, gameFrame, levels, congrats, players);

        gameLoop.start();
    }
}
