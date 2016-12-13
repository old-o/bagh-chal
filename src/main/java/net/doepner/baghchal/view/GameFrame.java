package net.doepner.baghchal.view;

import net.doepner.baghchal.model.Themes;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;

/**
 * Main frame
 */
public class GameFrame {

    private final JFrame frame;
    private final JButton nextLevelBtn;

    public GameFrame(final GamePanel gamePanel, final Themes themes) {

        frame = new JFrame("Bagh-Chal");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        final JButton newGameBtn = new JButton("New Game");
        newGameBtn.addActionListener(e -> gamePanel.start());

        nextLevelBtn = new JButton("Next Level");
        nextLevelBtn.addActionListener(e -> gamePanel.nextLevel());
        nextLevelBtn.setEnabled(false);

        final JComboBox<String> themeChooser = new JComboBox<>();
        for (String themeName : themes.getNames()) {
            themeChooser.addItem(themeName);
        }
        themeChooser.addItemListener(e -> selectTheme(themes, themeChooser, gamePanel));
        themeChooser.setSelectedItem(themes.getThemeName());


        final JToolBar toolBar = new JToolBar();
        toolBar.add(newGameBtn);
        toolBar.add(nextLevelBtn);
        toolBar.add(themeChooser);

        frame.add(toolBar, BorderLayout.PAGE_START);
        frame.add(gamePanel, BorderLayout.CENTER);
    }

    private void selectTheme(Themes themes, JComboBox<String> themeChooser, GamePanel gamePanel) {
        final String themeName = themeChooser.getItemAt(themeChooser.getSelectedIndex());
        themes.selectTheme(themeName);
        gamePanel.initPreferredSize();
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
}
