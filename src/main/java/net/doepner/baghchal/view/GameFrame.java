package net.doepner.baghchal.view;

import net.doepner.baghchal.theming.ThemeSelector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;

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
        toolBar.add(newGameBtn);
        toolBar.add(nextLevelBtn);
        toolBar.add(themeChooser);
        toolBar.add(xSizeSpinner);
        toolBar.add(ySizeSpinner);

        frame.add(toolBar, BorderLayout.PAGE_START);
        frame.add(new JScrollPane(gamePanel), BorderLayout.CENTER);

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
