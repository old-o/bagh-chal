package net.doepner.baghchal;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.KEY_STROKE_CONTROL;
import static java.awt.RenderingHints.KEY_TEXT_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
import static java.awt.RenderingHints.VALUE_STROKE_NORMALIZE;
import static java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON;

public class UI extends JPanel {

    private final PreyManager preyManager;
    private final Board board;
    private final Images images;
    private final Levels levels;

    private final Image congrats;

    private final BasicStroke stroke = new BasicStroke(1.5f);
    private final RenderingHints renderingHints;

    private Paint paint;

    private Color gridColor;
    private Color diagonalColor;
    private Color backgroundColor;
    private Color boardEdgeColor;

    public UI(Board board, PreyManager preyManager, Images images, Levels levels) {
        this.board = board;
        this.preyManager = preyManager;
        this.images = images;
        this.levels = levels;

        congrats = images.getImageResource("congrats.gif");

        final Map<RenderingHints.Key, Object> map = new HashMap<>();
        map.put(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON);
        map.put(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
        map.put(KEY_STROKE_CONTROL, VALUE_STROKE_NORMALIZE);
        renderingHints = new RenderingHints(map);

        addMouseMotionListener(preyManager);
        addMouseListener(preyManager);
    }

    public void start() {
        levels.firstLevel();
        startLevel();
    }

    public void nextLevel() {
        levels.nextLevel();
        startLevel();
    }

    public void startLevel() {
        board.reset();
        preyManager.reset();
        final BufferedImage bgImage = images.getImage("background.jpg");
        paint = new TexturePaint(bgImage, new Rectangle(0, 0, bgImage.getWidth(), bgImage.getHeight()));
        backgroundColor = getLevelColor("backgroundColor");
        diagonalColor = getLevelColor("diagonalColor");
        gridColor = getLevelColor("gridColor");
        boardEdgeColor = getLevelColor("boardEdgeColor");

        repaint();
    }

    private Color getLevelColor(String name) {
        return Color.decode(levels.getLevelProperty(name));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        final int width = getWidth();
        final int height = getHeight();

        final Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHints(renderingHints);

        g2.setColor(getForeground());

        if (levels.isLevelDone()) {
            g2.drawImage(congrats, 70, 80, null);
            g2.setFont(new Font("SansSerif", 0, 34));
            final String s = levels.getLevelEndMessage();
            g2.drawString(s, width / 2 - (g2.getFontMetrics().stringWidth(s) >> 1), height / 2 + 100);
        } else {
            drawBoard(g2, width, height);
            preyManager.drawDraggedPrey(g2);
        }
    }

    void drawBoard(Graphics2D g2, int width, int height) {

        final int xStep = width / board.getXSize();
        final int yStep = height / board.getYSize();

        setBackground(backgroundColor);
        setOpaque(true);

        g2.translate(xStep / 2, yStep / 2);

        final int xStart = xStep;
        final int xEnd = xStep * board.getCentreXSize();

        final int yStart = yStep;
        final int yEnd = yStep * board.getCentreYSize();

        g2.setPaint(paint);
        g2.fillRect(xStart - xStep /2, yStart - yStep/2, xEnd - xStart + xStep, yEnd - yStart + yStep);

        g2.setStroke(stroke);
        g2.setColor(boardEdgeColor);
        g2.drawRect(xStart - xStep /2, yStart - yStep/2, xEnd - xStart + xStep, yEnd - yStart + yStep);

        g2.setColor(gridColor);

        for (int x = xStart; x <= xEnd; x += xStep) {
            g2.drawLine(x, yStart, x, yEnd);
        }
        for (int y = yStart; y <= yEnd; y += yStep) {
            g2.drawLine(xStart, y, xEnd, y);
        }

        final int xMid = (xStart + xEnd) / 2;
        final int yMid = (yStart + yEnd) / 2;

        g2.setColor(diagonalColor);

        g2.drawLine(xStart, yStart, xEnd, yEnd);
        g2.drawLine(xStart, yEnd, xEnd, yStart);
        g2.drawLine(xStart, yMid, xMid, yEnd);
        g2.drawLine(xStart, yMid, xMid, yStart);
        g2.drawLine(xMid, yStart, xEnd, yMid);
        g2.drawLine(xMid, yEnd, xEnd, yMid);

        for (int i = 0; i < board.getXSize(); i++) {
            for (int j = 0; j < board.getYSize(); j++) {
                final Piece piece = board.get(i, j);
                if (piece != null) {
                    final Image im = images.getImage(piece);

                    final int imgWidth = im.getWidth(null);
                    final int imgHeight = im.getHeight(null);

                    final int xImgOffset = -(imgWidth / 2);
                    final int yImgOffset = -(imgHeight / 2);

                    g2.drawImage(im, xImgOffset + i * xStep, yImgOffset + j * yStep, null);
                }
            }
        }

        g2.translate(-xStep / 2, -yStep / 2);
    }
}
