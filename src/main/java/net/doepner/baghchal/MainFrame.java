package net.doepner.baghchal;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;

/**
 * Main frame
 */
public class MainFrame {

    private final JFrame frame;
    private final BoardPanel boardPanel;
    private final JButton nextLevelBtn;

    MainFrame(BoardPanel boardPanel) {
        frame = new JFrame("Bagh-Chal");
        this.boardPanel = boardPanel;

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        final JButton newGameBtn = new JButton("New Game");
        newGameBtn.addActionListener(e -> boardPanel.start());

        nextLevelBtn = new JButton("Next Level");
        nextLevelBtn.addActionListener(e -> boardPanel.nextLevel());
        nextLevelBtn.setEnabled(false);

        final JToolBar toolBar = new JToolBar();
        toolBar.add(newGameBtn);
        toolBar.add(nextLevelBtn);

        frame.add(toolBar, BorderLayout.PAGE_START);
        frame.add(boardPanel, BorderLayout.CENTER);
    }

    void show(Dimension preferredSize) {
        boardPanel.setPreferredSize(preferredSize);
        boardPanel.start();
        frame.pack();
        frame.setVisible(true);
    }


    public void enableNextLevel(boolean enable) {
        nextLevelBtn.setEnabled(enable);
    }
}
