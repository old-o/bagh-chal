package net.doepner.baghchal;

import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import static net.doepner.baghchal.Piece.TIGER;

public class UI extends JComponent {

    private final GoatsManager goatsManager;
    private final Board board;
    private final Images images;
    private final Phases phases;

    private final Image congrats;

    private Image tiger;

    public UI(Board board, GoatsManager goatsManager, Images images, Phases phases) {
        this.board = board;
        this.goatsManager = goatsManager;
        this.images = images;
        this.phases = phases;

        congrats = images.getImage("congrats.gif");

        setOpaque(true);
        setBackground(Color.white);

        addMouseMotionListener(goatsManager);
        addMouseListener(goatsManager);
    }

    public void start() {
        startLevel(phases.firstLevel());
    }

    public void nextLevel() {
        startLevel(phases.nextLevel());
    }

    private void startLevel(int level) {
        board.reset();
        goatsManager.reset();
        tiger = images.getTigerImage(level);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        final int width = getWidth();
        final int height = getHeight();

        g.setColor(getBackground());
        g.fillRect(0, 0, width, height);
        g.setColor(getForeground());

        final Graphics2D g2 = (Graphics2D) g;

        if (phases.isEnd()) {
            g2.drawImage(congrats, 70, 80, this);
            final String s;
            if (phases.isOver()) {
                s = "You have completed Bagh-Chal";
            } else {
                s = "Now try level " + phases.getLevel();
            }
            g2.setFont(new Font("SansSerif", 0, 34));
            g2.drawString(s, width / 2 - (g2.getFontMetrics().stringWidth(s) >> 1), height / 2 + 100);
        }  else {
            drawBoard(g2, width,height);
            goatsManager.drawRemainingGoats(g2, width, height);
            goatsManager.drawDraggedGoat(g2);
        }
    }

    void drawBoard(Graphics2D g2, int w, int h) {
        g2.setColor(Color.black);

        final int width = w - 2 * 50;
        final int height = h - 2 * 50;

        final int xStart = 30;
        final int yStart = 30;

        final int yEnd = yStart + height;
        final int xEnd = xStart + width;

        final int xStep = width / (board.getXSize() - 1);
        final int yStep = height / (board.getYSize() - 1);

        for (int x = xStart; x <= xEnd; x += xStep) {
            g2.drawLine(x, yStart, x, yEnd);
        }
        for (int y = yStart; y <= yEnd; y += yStep) {
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

        final int xImgOffset = xStart - (imgWidth / 2);
        final int yImgOffset = yStart - (imgHeight / 2);

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
