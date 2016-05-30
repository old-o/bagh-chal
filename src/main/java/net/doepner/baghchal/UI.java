package net.doepner.baghchal;

import javax.swing.*;
import java.awt.*;

import static net.doepner.baghchal.Piece.TIGER;

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
        super(new BorderLayout());

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

        // TODO : should be relative to UI width and height
        final int width = 400;
        final int height = 400;

        final int xStart = 30;
        final int yStart = 30;

        final int yEnd = yStart + height;
        final int xEnd = xStart + width;

        final int xStep = width / (board.getXSize() - 1);
        final int yStep = height / (board.getYSize() - 1);

        for (int x = xStart; x <= xEnd; x+= xStep) {
            g2.drawLine(x, yStart, x, yEnd);
        }
        for (int y = yStart; y <= yEnd; y+= yStep) {
            g2.drawLine(xStart, y, xEnd, y);
        }

        final int xMid = xStart + width / 2;
        final int yMid = yStart + height / 2;

        g2.drawLine(xStart, yStart, xEnd, yEnd);
        g2.drawLine(xStart, yMid, xMid, yEnd);
        g2.drawLine(xStart, yMid, xMid, yStart);
        g2.drawLine(xStart, yEnd, xEnd, yStart);
        g2.drawLine(xMid, yStart, xEnd, yMid);
        g2.drawLine(xMid, yStart, xEnd, yMid);
        g2.drawLine(xMid, yEnd, xEnd, yMid);


        final int imgWidth = 32;
        final int imgHeight = 32;

        final int xImgOffset = xStart - (imgWidth /2);
        final int yImgOffset = yStart - (imgHeight /2);

        for (int i = 0; i < board.getXSize(); i++) {
            for (int j = 0; j < board.getYSize(); j++) {
                if (board.get(i, j) != null) {
                    Image im = board.get(i, j) == TIGER ? tiger : images.getGoatImage();
                    g2.drawImage(im, xImgOffset + i * xStep, yImgOffset + j * yStep, null);
                }
            }
        }
    }

}
