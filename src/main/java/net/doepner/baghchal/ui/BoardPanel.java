package net.doepner.baghchal.ui;

import net.doepner.baghchal.BoardSetup;
import net.doepner.baghchal.model.Board;
import net.doepner.baghchal.model.Levels;
import net.doepner.baghchal.model.Piece;
import net.doepner.baghchal.resources.Images;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Point;
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

public class BoardPanel extends JPanel {

    private final Board board;
    private final BoardSetup boardSetup;

    private final Images images;
    private final Levels levels;

    private final BufferedImage congrats;

    private final BasicStroke stroke = new BasicStroke(1.5f);
    private final RenderingHints renderingHints = createRenderingHints();

    private static RenderingHints createRenderingHints() {
        final Map<RenderingHints.Key, Object> map = new HashMap<>();
        map.put(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON);
        map.put(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
        map.put(KEY_STROKE_CONTROL, VALUE_STROKE_NORMALIZE);
        return new RenderingHints(map);
    }

    private Paint boardPaint;

    private Color gridColor;
    private Color diagonalColor;
    private Color backgroundColor;
    private Color boardEdgeColor;

    public BoardPanel(Board board, BoardSetup boardSetup, Images images, Levels levels) {
        this.board = board;
        this.boardSetup = boardSetup;
        this.images = images;
        this.levels = levels;
        congrats = images.getImageResource(getClass().getResource("/net/doepner/baghchal/congrats.gif"));
    }

    void start() {
        levels.firstLevel();
        startLevel();
    }

    void nextLevel() {
        levels.nextLevel();
        startLevel();
    }

    private void startLevel() {
        board.reset();
        boardSetup.setup(board);
        final BufferedImage bgImage = images.getImage("background.jpg");
        boardPaint = new TexturePaint(bgImage, new Rectangle(0, 0, bgImage.getWidth(), bgImage.getHeight()));
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
            g2.drawImage(congrats, width / 2 - congrats.getWidth() / 2, height / 2 - congrats.getHeight(), null);
            g2.setFont(new Font("SansSerif", 0, 34));
            final String s = levels.getLevelEndMessage();
            g2.drawString(s, width / 2 - (g2.getFontMetrics().stringWidth(s) / 2), height / 2 + 20);
        } else {
            drawBoard(g2, width, height);
            drawDraggedImage(g2);
        }
    }

    private void drawBoard(Graphics2D g2, int width, int height) {
        setBackground(backgroundColor);
        setOpaque(true);

        final int xStep = width / board.getXSize();
        final int yStep = height / board.getYSize();
        final int xStart = xStep + xStep / 2;
        final int xBoardCentreEnd = xStep * board.getCentreXSize();
        final int xEnd = xBoardCentreEnd + xStep / 2;
        final int yStart = yStep + yStep / 2;
        final int yBoardCentreEnd = yStep * board.getCentreYSize();
        final int yEnd = yBoardCentreEnd + yStep / 2;

        drawBoardCentreArea(g2, xStep, yStep, xBoardCentreEnd, yBoardCentreEnd);
        drawGridLines(g2, xStep, yStep, xStart, xEnd, yStart, yEnd);
        drawDiagonalLines(g2, xStart, xEnd, yStart, yEnd);
        drawPieces(g2, xStep, yStep, xStart - xStep, yStart - yStep);
    }

    private void drawGridLines(Graphics2D g2, int xStep, int yStep, int xStart, int xEnd, int yStart, int yEnd) {
        g2.setColor(gridColor);
        for (int x = xStart; x <= xEnd; x += xStep) {
            g2.drawLine(x, yStart, x, yEnd);
        }
        for (int y = yStart; y <= yEnd; y += yStep) {
            g2.drawLine(xStart, y, xEnd, y);
        }
    }

    private void drawBoardCentreArea(Graphics2D g2, int xStep, int yStep, int xEnd, int yEnd) {
        g2.setPaint(boardPaint);
        g2.fillRect(xStep, yStep, xEnd, yEnd);
        g2.setStroke(stroke);
        g2.setColor(boardEdgeColor);
        g2.drawRect(xStep, yStep, xEnd, yEnd);
    }

    private void drawDiagonalLines(Graphics2D g2, int xStart, int xEnd, int yStart, int yEnd) {
        final int xMid = (xStart + xEnd) / 2;
        final int yMid = (yStart + yEnd) / 2;

        g2.setColor(diagonalColor);

        g2.drawLine(xStart, yMid, xMid, yEnd);
        g2.drawLine(xStart, yMid, xMid, yStart);
        g2.drawLine(xMid, yStart, xEnd, yMid);
        g2.drawLine(xMid, yEnd, xEnd, yMid);
        g2.drawLine(xEnd, yEnd, xStart, yStart);
        g2.drawLine(xEnd, yStart, xStart, yEnd);
    }

    private void drawPieces(Graphics2D g2, int xStep, int yStep, int xStart, int yStart) {
        board.forAllPositions(p -> {
            final Piece piece = board.get(p);
            if (piece != null) {
                final BufferedImage img = images.getImage(piece);
                g2.drawImage(img,
                        xStart + p.x() * xStep - img.getWidth() / 2,
                        yStart + p.y() * yStep - img.getHeight() / 2,
                        null);
            }
        });
    }

    private Point lastDragPoint;
    private Image draggedImage;

    public void setLastDragPoint(Point lastDragPoint) {
        this.lastDragPoint = lastDragPoint;
    }

    public Point getLastDragPoint() {
        return lastDragPoint;
    }

    void repaintForDrag(Rectangle rectangle, BufferedImage image) {
        this.draggedImage = image;
        repaint(rectangle);
    }

    private void drawDraggedImage(Graphics2D g2) {
        if (lastDragPoint != null) {
            g2.drawImage(draggedImage, lastDragPoint.x, lastDragPoint.y, null);
        }
    }
}
