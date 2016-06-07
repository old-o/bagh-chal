package net.doepner.baghchal;

import javax.swing.JComponent;
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

public class UI extends JComponent {

    private final PreyManager preyManager;
    private final Board board;
    private final Images images;
    private final Phases phases;

    private final Image congrats;

    private final BasicStroke stroke = new BasicStroke(2);
    private final RenderingHints renderingHints;

    private Paint paint;

    private Color gridColor = Color.blue;
    private Color diagonalColor = Color.darkGray;

    public UI(Board board, PreyManager preyManager, Images images, Phases phases) {
        this.board = board;
        this.preyManager = preyManager;
        this.images = images;
        this.phases = phases;

        congrats = images.getImageResource("congrats.gif");

        final Map<RenderingHints.Key, Object> map = new HashMap<>();
        map.put(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON);
        map.put(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
        map.put(KEY_STROKE_CONTROL,VALUE_STROKE_NORMALIZE);
        renderingHints = new RenderingHints(map);

        addMouseMotionListener(preyManager);
        addMouseListener(preyManager);
    }

    public void start() {
        phases.firstLevel();
        startLevel();
    }

    public void nextLevel() {
        phases.nextLevel();
        startLevel();
    }

    public void startLevel() {
        board.reset();
        preyManager.reset();
        final BufferedImage bgImage = images.getImage("background.jpg");
        paint = new TexturePaint(bgImage, new Rectangle(0, 0, bgImage.getWidth(), bgImage.getHeight()));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        final int width = getWidth();
        final int height = getHeight();

        final Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHints(renderingHints);

        g2.setPaint(paint);
        g.fillRect(0, 0, width, height);

        g.setColor(getForeground());

        if (phases.isEnd()) {
            g2.drawImage(congrats, 70, 80, this);
            g2.setFont(new Font("SansSerif", 0, 34));
            final String s = phases.getLevelEndMessage();
            g2.drawString(s, width / 2 - (g2.getFontMetrics().stringWidth(s) >> 1), height / 2 + 100);
        } else {
            drawBoard(g2, width, height);
            preyManager.drawRemainingPrey(g2, width, height);
            preyManager.drawDraggedPrey(g2);
        }
    }

    void drawBoard(Graphics2D g2, int w, int h) {
        g2.setStroke(stroke);

        final int width = w - 2 * 50;
        final int height = h - 2 * 50;

        final int xStart = 30;
        final int yStart = 30;

        final int yEnd = yStart + height;
        final int xEnd = xStart + width;

        final int xStep = width / (board.getXSize() - 1);
        final int yStep = height / (board.getYSize() - 1);

        g2.setColor(gridColor);

        for (int x = xStart; x <= xEnd; x += xStep) {
            g2.drawLine(x, yStart, x, yEnd);
        }
        for (int y = yStart; y <= yEnd; y += yStep) {
            g2.drawLine(xStart, y, xEnd, y);
        }

        final int xMid = xStart + width / 2;
        final int yMid = yStart + height / 2;

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

                    final int xImgOffset = xStart - (imgWidth / 2);
                    final int yImgOffset = yStart - (imgHeight / 2);

                    g2.drawImage(im, xImgOffset + i * xStep, yImgOffset + j * yStep, null);
                }
            }
        }
    }
}
