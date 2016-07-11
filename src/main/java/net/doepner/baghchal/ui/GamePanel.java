package net.doepner.baghchal.ui;

import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.KEY_STROKE_CONTROL;
import static java.awt.RenderingHints.KEY_TEXT_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
import static java.awt.RenderingHints.VALUE_STROKE_NORMALIZE;
import static java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.JPanel;

import net.doepner.baghchal.GameSetup;
import net.doepner.baghchal.model.GameTable;
import net.doepner.baghchal.model.Levels;
import net.doepner.baghchal.model.Move;
import net.doepner.baghchal.model.Piece;
import net.doepner.baghchal.model.Position;
import net.doepner.baghchal.resources.Images;

public class GamePanel extends JPanel {

    private final GameTable gameTable;
    private final GameSetup gameSetup;

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
    private Colors colors;

    public GamePanel(GameTable gameTable, GameSetup gameSetup, Images images, Levels levels) {
        this.gameTable = gameTable;
        this.gameSetup = gameSetup;
        this.images = images;
        this.levels = levels;
        congrats = images.getImageResource(getClass().getResource("/net/doepner/baghchal/congrats.gif"));
    }

    public void start() {
        levels.firstLevel();
        startLevel();
    }

    void nextLevel() {
        levels.nextLevel();
        startLevel();
    }

    private void startLevel() {
        gameTable.reset();
        gameSetup.setup(gameTable);
        final BufferedImage bgImage = images.getImage("background.jpg");
        boardPaint = new TexturePaint(bgImage, new Rectangle(0, 0, bgImage.getWidth(), bgImage.getHeight()));
        colors = new Colors(levels);
        repaint();
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
            drawGameTable(g2, width, height);
            drawDraggedImage(g2);
        }
    }

    private void drawGameTable(Graphics2D g2, int width, int height) {
        setBackground(colors.backgroundColor());
        setOpaque(true);

        final int xStep = width / gameTable.getXSize();
        final int yStep = height / gameTable.getYSize();
        final int xStart = xStep + xStep / 2;
        final int xBoardEnd = xStep * gameTable.getCentreXSize();
        final int yStart = yStep + yStep / 2;
        final int yBoardEnd = yStep * gameTable.getCentreYSize();

        drawBoard(g2, xStep, yStep, xBoardEnd, yBoardEnd);
        drawPieces(g2, xStep, yStep, xStart - xStep, yStart - yStep);
    }

    private void drawBoard(Graphics2D g2, int xStep, int yStep, int xEnd, int yEnd) {
        g2.setPaint(boardPaint);
        g2.fillRect(xStep, yStep, xEnd, yEnd);
        g2.setStroke(stroke);
        g2.setColor(colors.boardEdgeColor());
        g2.drawRect(xStep, yStep, xEnd, yEnd);
    }

    private void drawPieces(Graphics2D g2, int xStep, int yStep, int xStart, int yStart) {
        for (Position p : gameTable.getAllPositions()) {
            final int x = xStart + p.x() * xStep;
            final int y = yStart + p.y() * yStep;

            tryForwardDirections(gameTable, p, m -> {
                g2.setColor(m.isOneDimensional() ? colors.gridColor() : colors.diagonalColor());
                g2.drawLine(x, y, x + m.xStep() * xStep, y + m.yStep() * yStep);
            });
            final Piece piece = gameTable.get(p);
            if (piece != null) {
                final BufferedImage img = images.getImage(piece);
                g2.drawImage(img, x - img.getWidth() / 2, y - img.getHeight() / 2, null);
            }
        }
    }

    private static void tryForwardDirections(GameTable gameTable, Position p, Consumer<Move> moveProcessor) {
        for (int yStep = -1; yStep <= 1; yStep++) {
            gameTable.processStepAlongLine(p, p.add(1, yStep), moveProcessor);
        }
        gameTable.processStepAlongLine(p, p.add(0, 1), moveProcessor);
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

    public void addMouseAdapter(MouseAdapter mouseAdapter) {
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
        addMouseWheelListener(mouseAdapter);
    }

    public void removeMouseAdapter(MouseAdapter mouseAdapter) {
        removeMouseListener(mouseAdapter);
        removeMouseMotionListener(mouseAdapter);
        removeMouseWheelListener(mouseAdapter);
    }
}
