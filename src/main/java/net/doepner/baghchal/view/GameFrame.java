package net.doepner.baghchal.view;

import net.doepner.baghchal.theming.ThemeSelector;

import javax.swing.*;
import javax.swing.JToolBar.Separator;
import java.awt.*;

/**
 * Main frame
 */
public final class GameFrame {

    private final JFrame frame;
    private final JButton nextLevelBtn;

    private final GamePanel gamePanel;

    private final SpinnerNumberModel boardXSizeModel;
    private final SpinnerNumberModel boardYSizeModel;

    public GameFrame(final GamePanel gamePanel, final ThemeSelector themeSelector,
                     SpinnerNumberModel boardXSizeModel,
                     SpinnerNumberModel boardYSizeModel) {
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
            final Dimension size = component.getPreferredSize();
            component.setMaximumSize(size);
            component.setMinimumSize(size);
            component.setPreferredSize(size);
            toolBar.add(component);
        }
        toolBar.add(new Separator(new Dimension(10, 10)));
    }

    private void selectTheme(ThemeSelector themeSelector, JComboBox<String> themeChooser) {
        final String themeName = themeChooser.getItemAt(themeChooser.getSelectedIndex());
        themeSelector.selectTheme(themeName);
        gamePanel.setSize();
        frame.pack();
        gamePanel.repaint();
    }

    public void show() {
        frame.pack();
        frame.setVisible(true);
    }

    public void enableNextLevel(boolean enable) {
        nextLevelBtn.setEnabled(enable);
    }

    private void updateBoardSize() {
        final Number xSize = boardXSizeModel.getNumber();
        final Number ySize = boardYSizeModel.getNumber();
        if (xSize != null && ySize != null) {
            gamePanel.setBoardSize(new Dimension(xSize.intValue(), ySize.intValue()));
        }
        gamePanel.start();
        frame.pack();
        gamePanel.repaint();
    }

    public GamePanel getGamePanel() {
        return gamePanel;
    }
}
