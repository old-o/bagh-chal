package net.doepner.baghchal.view;

import net.doepner.baghchal.model.Position;
import net.doepner.baghchal.theming.ThemeSelector;
import org.guppy4j.log.Log;
import org.guppy4j.log.LogProvider;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JToolBar;
import javax.swing.JToolBar.Separator;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;

import static org.guppy4j.log.Log.Level.debug;

/**
 * Main frame
 */
public final class GameFrame {

    private final Log log;

    private final JFrame frame;
    private final JButton nextLevelBtn;

    private final GamePanel gamePanel;

    private final SpinnerNumberModel boardXSizeModel;
    private final SpinnerNumberModel boardYSizeModel;

    public GameFrame(LogProvider logProvider,
                     GamePanel gamePanel, ThemeSelector themeSelector,
                     SpinnerNumberModel boardXSizeModel,
                     SpinnerNumberModel boardYSizeModel) {
        log = logProvider.getLog(getClass());
        this.gamePanel = gamePanel;

        this.boardXSizeModel = boardXSizeModel;
        this.boardYSizeModel = boardYSizeModel;

        frame = new JFrame("Bagh-Chal");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        final JButton newGameBtn = new JButton("New Game");
        newGameBtn.addActionListener(e -> gamePanel.start());

        nextLevelBtn = new JButton("Next Level");
        nextLevelBtn.addActionListener(e -> gamePanel.nextLevel());
        nextLevelBtn.setEnabled(false);

        final JComboBox<String> themeChooser = new JComboBox<>();
        for (String themeName : themeSelector.getAvailableThemeNames()) {
            themeChooser.addItem(themeName);
        }
        themeChooser.addActionListener(e -> selectTheme(themeSelector, themeChooser));
        themeChooser.setSelectedItem(themeSelector.getSelectedThemeName());

        final JSpinner xSizeSpinner = new JSpinner(boardXSizeModel);
        xSizeSpinner.addChangeListener(e -> updateBoardSize());

        final JSpinner ySizeSpinner = new JSpinner(boardYSizeModel);
        ySizeSpinner.addChangeListener(e -> updateBoardSize());

        final JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        addTo(toolBar, newGameBtn, nextLevelBtn);
        addTo(toolBar, new JLabel("Theme: "), themeChooser);
        addTo(toolBar, new JLabel("Width: "), xSizeSpinner);
        addTo(toolBar, new JLabel("Height: "), ySizeSpinner);

        frame.add(toolBar, BorderLayout.PAGE_START);
        frame.add(new JScrollPane(gamePanel), BorderLayout.CENTER);
    }

    private static void addTo(JToolBar toolBar, Component... components) {
        for (Component component : components) {
            updateSizing(component);
            toolBar.add(component);
        }
        toolBar.add(new Separator(new Dimension(10, 10)));
    }

    private static void updateSizing(Component component) {
        final Dimension size = component.getPreferredSize();
        component.setMaximumSize(size);
        component.setMinimumSize(size);
        component.setPreferredSize(size);
    }

    private void selectTheme(ThemeSelector themeSelector, JComboBox<String> themeChooser) {
        final String themeName = themeChooser.getItemAt(themeChooser.getSelectedIndex());
        themeSelector.selectTheme(themeName);
        updateBoardSize();
    }

    public void show() {
        frame.pack();
        frame.setVisible(true);
    }

    public void enableNextLevel(boolean enable) {
        nextLevelBtn.setEnabled(enable);
    }

    private void updateBoardSize() {
        final GraphicsConfiguration gc = gamePanel.getGraphicsConfiguration();
        if (gc != null) {
            final Rectangle screenSize = gc.getBounds();
            log.as(debug, "Screen size: {} x {}", screenSize.getWidth(), screenSize.getHeight());

            final Position maxPosition = gamePanel.getMaxPosition(screenSize);
            final int xSize = Math.min(boardXSizeModel.getNumber().intValue(), maxPosition.x());
            final int ySize = Math.min(boardYSizeModel.getNumber().intValue(), maxPosition.y());

            boardXSizeModel.setMaximum(maxPosition.x());
            boardYSizeModel.setMaximum(maxPosition.y());
            boardXSizeModel.setValue(xSize);
            boardYSizeModel.setValue(ySize);

            gamePanel.setBoardSize(new Dimension(xSize, ySize));

            frame.getContentPane().repaint();
            frame.pack();
            gamePanel.repaint();
        }
    }

    public GamePanel getGamePanel() {
        return gamePanel;
    }
}
