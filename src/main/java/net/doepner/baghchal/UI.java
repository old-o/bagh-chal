package net.doepner.baghchal;

import javax.swing.*;
import java.awt.*;

public class UI extends JPanel {

    private final Color bgColor = Color.white;

    private final GoatsManager goatsManager;
    private final Board board;
    private final Images images;
    private final Phases phases;

    private final Image congrats;
    private final JButton nextLevelBtn;

    private int level;
    private Image tiger;

    public UI(Board board, GoatsManager goatsManager, Images images, Phases phases) {
        this.board = board;
        this.goatsManager = goatsManager;
        this.images = images;
        this.phases = phases;

        final JButton newGameBtn = new JButton("New Game");
        newGameBtn.addActionListener(e -> start());
        newGameBtn.setBounds(410, 460, 80, 30);
        add(newGameBtn);

        nextLevelBtn = new JButton("Next Level");
        nextLevelBtn.addActionListener(e -> startLevel(level));
        nextLevelBtn.setBounds(200, 225, 100, 50);
        nextLevelBtn.setVisible(false);
        add(nextLevelBtn);

        congrats = images.getImage("congrats.gif");

        addMouseMotionListener(goatsManager);
        addMouseListener(goatsManager);
    }

    public void start() {
        startLevel(1);
    }

    private void startLevel(int level) {
        this.level = level;
        nextLevelBtn.setVisible(false);
        board.reset();
        phases.reset();
        goatsManager.reset();
        tiger = images.getTigerImage(level);
        repaint();
    }

    public void offerNextLevel() {
        level++;
        nextLevelBtn.setVisible(true);
    }

    public int getLevel() {
        return level;
    }

    public void paint(Graphics g) {
        super.paint(g);
        final Graphics2D g2 = (Graphics2D) g;
        g2.setColor(bgColor);
        g2.fillRect(0, 0, 500, 500);
        if (phases.isEnd()) {
            g2.setColor(bgColor);
            g2.fillRect(0, 0, 500, 500);
            g2.setColor(Color.black);
            g2.drawImage(congrats, 70, 80, this);
            String s;
            if (level > 7) {
                s = "You have completed Bagh-Chal";
                nextLevelBtn.setVisible(false);
            } else {
                s = "Now try level " + level;
            }
            g2.setFont(new Font("SansSerif", 0, 34));
            g2.drawString(s, 250 - (g2.getFontMetrics().stringWidth(s) >> 1), 350);
            return;
        }
        if (phases.isBeforeGame()) {
            g2.setColor(bgColor);
            g2.fillRect(0, 0, 500, 500);
            g2.drawString("Downloading images...", 50, 50);
            return;
        }
        drawBoard(g2);
        goatsManager.drawRemainingGoats(g2);
        goatsManager.drawDraggedGoat(g2);
    }

    void drawBoard(Graphics2D g2) {
        g2.setColor(Color.black);

        g2.drawRect(30, 30, 400, 400);

        for (int i = 0; i < 3; i++) {
            int j = 130 + 100 * i;
            g2.drawLine(30, j, 430, j);
            g2.drawLine(j, 30, j, 430);
        }

        g2.drawLine(30, 30, 430, 430);
        g2.drawLine(30, 230, 230, 430);
        g2.drawLine(230, 30, 430, 230);
        g2.drawLine(30, 230, 230, 30);
        g2.drawLine(230, 30, 430, 230);
        g2.drawLine(230, 430, 430, 230);
        g2.drawLine(30, 430, 430, 30);

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (board.get(i, j) != null) {
                    Image im = board.get(i, j) == Piece.TIGER ? tiger : images.getGoatImage();
                    g2.drawImage(im, 14 + i * 100, 14 + j * 100, this);
                }
            }
        }
    }

}
