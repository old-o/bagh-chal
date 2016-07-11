package net.doepner.baghchal.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

/**
 * Main frame
 */
public class GameFrame {

    private final JFrame frame;
    private final GamePanel gamePanel;
    private final JButton nextLevelBtn;

    public GameFrame(GamePanel gamePanel) {
        frame = new JFrame("Bagh-Chal");
        this.gamePanel = gamePanel;

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        final JButton newGameBtn = new JButton("New Game");
        newGameBtn.addActionListener(e -> gamePanel.start());

        nextLevelBtn = new JButton("Next Level");
        nextLevelBtn.addActionListener(e -> gamePanel.nextLevel());
        nextLevelBtn.setEnabled(false);

        final JToolBar toolBar = new JToolBar();
        toolBar.add(newGameBtn);
        toolBar.add(nextLevelBtn);

        frame.add(toolBar, BorderLayout.PAGE_START);
        frame.add(gamePanel, BorderLayout.CENTER);
    }

    public void show(Dimension preferredSize) {
        gamePanel.setPreferredSize(preferredSize);
        frame.pack();
        frame.setVisible(true);
    }


    public void enableNextLevel(boolean enable) {
        nextLevelBtn.setEnabled(enable);
    }
}
