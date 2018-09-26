package net.doepner.baghchal;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;
import static net.doepner.baghchal.model.Piece.PREDATOR;
import static net.doepner.baghchal.model.Piece.PREY;
import static net.doepner.baghchal.theming.Theme.SoundResourceId.CONGRATS;
import static org.guppy4j.log.Log.Level.error;

import java.awt.Dimension;
import java.net.URL;
import java.util.function.Consumer;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.SpinnerNumberModel;

import org.guppy4j.io.SimpleClassPathScanner;
import org.guppy4j.log.Log;
import org.guppy4j.log.LogProvider;
import org.guppy4j.log.Slf4jLogProvider;
import org.guppy4j.run.Executable;
import org.guppy4j.text.CharCanvasImpl;

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

/**
 * Entry point of the game
 */
public final class Main {

    private static final String PULSEAUDIO_BUG_REFERENCE =
            "https://stackoverflow.com/questions/45847635/java-audio-clip-cannot-be-closed-when-using-linux-pulseaudio";

    // Simplest Dependency Injection is done here without a DI framework. See
    // https://odoepner.wordpress.com/2017/01/17/diy-dependency-inject-yourself/
    @SuppressWarnings("OverlyCoupledMethod")
    public static void main(String... args) {

        System.setProperty("sun.java2d.opengl", "true");

        final LogProvider logProvider = new Slf4jLogProvider();
        final Log log = logProvider.getLog(Main.class);

        if (audioClipClassNameContains("PulseAudioClip")) {
            errorMessage(log, "Buggy AudioSystem detected (PulseAudio/Java bindings). "
                    + "Please adjust your Java sound.properties. See " + PULSEAUDIO_BUG_REFERENCE + " for details.");
            return;
        }

        final Dimension defaultBoardSize = new Dimension(5, 5);

        final String resourceBasePath = "/net/doepner/baghchal/themes";
        final String defaultThemeName = "goats-and-tigers";
        final Themes themes = new Themes(new SimpleClassPathScanner(), resourceBasePath, "%s/%s.%s", defaultThemeName);

        if (!themes.getAvailableThemeNames().iterator().hasNext()) {
            errorMessage(log, "No themes found in classpath under " + resourceBasePath);
        }

        final int maxLevel = 2;
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

    private static boolean audioClipClassNameContains(String s) {
        try (final Clip clip = AudioSystem.getClip()) {
            return clip == null || clip.getClass().getName().contains(s);
        } catch (LineUnavailableException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void errorMessage(Log log, String message) {
        log.as(error, message);
        showMessageDialog(null, message, "Cannot start", ERROR_MESSAGE);
    }
}
