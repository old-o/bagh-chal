package net.doepner.baghchal;

import net.doepner.baghchal.control.GameLoop;
import net.doepner.baghchal.control.Player;
import net.doepner.baghchal.control.PredatorStrategy;
import net.doepner.baghchal.control.PreyStrategy;
import net.doepner.baghchal.control.UserPlayer;
import net.doepner.baghchal.model.GameTable;
import net.doepner.baghchal.model.GameTableFactory;
import net.doepner.baghchal.model.Levels;
import net.doepner.baghchal.resources.AudioUrlPlayer;
import net.doepner.baghchal.theming.Themes;
import net.doepner.baghchal.view.GameFrame;
import net.doepner.baghchal.view.GamePanel;
import org.guppy4j.io.SimpleClassPathScanner;
import org.guppy4j.log.Log;
import org.guppy4j.log.LogProvider;
import org.guppy4j.log.Slf4jLogProvider;
import org.guppy4j.run.Executable;
import org.guppy4j.text.CharCanvasImpl;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.SpinnerNumberModel;
import java.awt.Dimension;
import java.net.URL;
import java.util.function.Consumer;

import static net.doepner.baghchal.model.Piece.PREDATOR;
import static net.doepner.baghchal.model.Piece.PREY;
import static net.doepner.baghchal.theming.Theme.SoundResourceId.CONGRATS;
import static org.guppy4j.log.Log.Level.error;

/**
 * Entry point of the game
 */
public final class Main {

    // Simplest Dependency Injection is done here without a DI framework. See
    // https://odoepner.wordpress.com/2017/01/17/diy-dependency-inject-yourself/
    @SuppressWarnings("OverlyCoupledMethod")
    public static void main(String... args) {

        System.setProperty("sun.java2d.opengl", "true");

        final LogProvider logProvider = new Slf4jLogProvider();
        final Log log = logProvider.getLog(Main.class);

        if (isAudioSystemBroken()) {
            log.as(error, "Buggy audio system detected. Please adjust your Java sound.properties.");
            System.exit(1);
        }

        final int maxLevel = 2;
        final Dimension defaultBoardSize = new Dimension(5, 5);

        final Themes themes = new Themes(new SimpleClassPathScanner(),
                "/net/doepner/baghchal/themes", "%s/%s.%s",
                "goats-and-tigers");

        final Levels levels = new Levels(maxLevel);

        final Consumer<URL> audioPlayMethod = AudioUrlPlayer::play;
        final Consumer<GameTable> tableSetupMethod = GameTableSetup::prepare;

        final GameTableFactory gameTableFactory = (xSize, ySize) -> new GameTable(
                logProvider, xSize, ySize, tableSetupMethod, new EventSounds(audioPlayMethod, themes),
                new CharCanvasImpl()
        );

        final GamePanel gamePanel = new GamePanel(gameTableFactory, defaultBoardSize, themes, levels);

        final Player preyStrategy = new PreyStrategy();
        final Player preyPlayer = new UserPlayer(PREY, gamePanel, themes);
        final Player predatorStrategy = new PredatorStrategy(levels);
        final Player predatorPlayer = new UserPlayer(PREDATOR, gamePanel, themes);

        final GameFrame gameFrame = new GameFrame(logProvider, gamePanel, themes,
                new SpinnerNumberModel(5, 4, 99, 1),
                new SpinnerNumberModel(5, 4, 99, 1));

        final Executable congrats = () -> AudioUrlPlayer.play(themes.getSoundResource(CONGRATS));

        final GameLoop gameLoop = new GameLoop(logProvider, gameFrame, levels, congrats,
                predatorPlayer, preyStrategy);

        gameLoop.start();
    }

    private static boolean isAudioSystemBroken() {
        try {
            final Clip clip = AudioSystem.getClip();
            return clip == null || clip.getClass().getName().contains("PulseAudioClip");
        } catch (LineUnavailableException e) {
            return true;
        }
    }

}
