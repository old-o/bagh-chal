package org.oldo.baghchal.view;

import org.oldo.baghchal.model.GameTable;
import org.oldo.baghchal.model.GameTableFactory;
import org.oldo.baghchal.model.Levels;
import org.oldo.baghchal.model.Move;
import org.oldo.baghchal.model.Piece;
import org.oldo.baghchal.model.Position;
import org.oldo.baghchal.theming.Theme;
import org.oldo.g2d.IntPair;
import org.oldo.g2d.Size;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.KEY_STROKE_CONTROL;
import static java.awt.RenderingHints.KEY_TEXT_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
import static java.awt.RenderingHints.VALUE_STROKE_NORMALIZE;
import static java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON;
import static org.oldo.baghchal.theming.Images.ImageId.CONGRATS;
import static org.oldo.baghchal.theming.Theme.ColorId.BACKGROUND;
import static org.oldo.baghchal.theming.Theme.ColorId.BOARD_EDGE;
import static org.oldo.baghchal.theming.Theme.ColorId.DIAGONAL;
import static org.oldo.baghchal.theming.Theme.ColorId.GRID;

public final class GamePanel extends JPanel implements GameView {

    private static final int DRAG_FRAME_PADDING = 3;
    public static final double SPACING = 0.5;
    private static final double PIECE_SPACE = 1 + SPACING;

    private final Stroke stroke = new BasicStroke(3f);
    private final RenderingHints renderingHints = createRenderingHints();

    private final GameTableFactory gameTableFactory;
    private final Levels levels;
    private final Theme theme;

    private GameTable gameTable;

    private static RenderingHints createRenderingHints() {
        final Map<RenderingHints.Key, Object> map = new HashMap<>();
        map.put(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON);
        map.put(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
        map.put(KEY_STROKE_CONTROL, VALUE_STROKE_NORMALIZE);
        return new RenderingHints(map);
    }

    public GamePanel(GameTableFactory gameTableFactory, IntPair defaultBoardSize,
                     Theme theme, Levels levels) {
        this.gameTableFactory = gameTableFactory;
        this.levels = levels;
        this.theme = theme;
        setOpaque(true);
        setBoardSize(defaultBoardSize);
    }

    public void setBoardSize(Size size) {
        boolean restart = false;
        if (gameTable == null || !gameTable.isBoardSize(size)) {
            final GameTable oldGameTable = gameTable;
            gameTable = gameTableFactory.getGameTable(size);
            if (oldGameTable != null) {
                oldGameTable.discard();
                restart = true;
            }
        }
        setSize();
        if (restart) {
            start();
        }
    }

    private void setSize() {
        final int width = (int) (PIECE_SPACE * gameTable.getXSize() * theme.getPieceWidth());
        final int height = (int) (PIECE_SPACE * gameTable.getYSize() * theme.getPieceHeight());
        final Dimension preferredSize = new Dimension(width, height);
        setPreferredSize(preferredSize);
        setMinimumSize(preferredSize);
        setSize(preferredSize);
    }

    @Override
    @SuppressWarnings({"NumericCastThatLosesPrecision", "ImplicitNumericConversion"})
    public Position getMaxPosition(Size bounds) {
        final int px = (int) (PIECE_SPACE * bounds.x() / theme.getPieceWidth() - DRAG_FRAME_PADDING);
        final int py = (int) (PIECE_SPACE * bounds.y() / theme.getPieceHeight() - DRAG_FRAME_PADDING);
        return new Position(px, py);
    }

    @Override
    public Position getPosition(Point point) {
        return new Position(point.x / xStep(), point.y / yStep());
    }

    @Override
    public Point getPoint(Position p) {
        final int x = (int) ((p.x() + SPACING) * xStep());
        final int y = (int) ((p.y() + SPACING) * yStep());
        return new Point(x, y);
    }

    private int xStep() {
        return getWidth() / gameTable.getXSize();
    }

    private int yStep() {
        return getHeight() / gameTable.getYSize();
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
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        final Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHints(renderingHints);
        g2.setColor(getForeground());

        if (levels.isLevelDone()) {
            drawLevelEnded(g2);
        } else {
            drawGameTable(g2);
            drawDraggedImage(g2);
        }
    }

    private void drawLevelEnded(Graphics2D g2) {
        final int width = getWidth();
        final int height = getHeight();

        final BufferedImage congrats = theme.getImage(CONGRATS);
        g2.drawImage(congrats, width / 2 - congrats.getWidth() / 2, height / 2 - congrats.getHeight(), null);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 34));
        final String s = levels.getLevelEndMessage();
        g2.drawString(s, width / 2 - (g2.getFontMetrics().stringWidth(s) / 2), height / 2 + 20);
    }

    private void drawGameTable(Graphics2D g2) {
        final int xStep = xStep();
        final int yStep = yStep();
        final int xBoardEnd = xStep * gameTable.getBoardXSize();
        final int yBoardEnd = yStep * gameTable.getBoardYSize();

        drawBoard(g2, xStep, yStep, xBoardEnd, yBoardEnd);
        drawPieces(g2, xStep, yStep);
    }

    private void drawBoard(Graphics2D g2, int xStep, int yStep, int xEnd, int yEnd) {
        g2.setPaint(theme.getBoardPaint());
        g2.fillRect(xStep, yStep, xEnd, yEnd);
        g2.setStroke(stroke);
        g2.setColor(theme.getColor(BOARD_EDGE));
        g2.drawRect(xStep, yStep, xEnd, yEnd);
    }

    private void drawPieces(Graphics2D g2, int xStep, int yStep) {
        for (Position p : gameTable.getPositions().getAll()) {
            final Point point = getPoint(p);

            for (Move m : gameTable.getStepsAlongLineFrom(p)) {
                drawLine(g2, xStep, yStep, point.x, point.y, m);
            }
            final Piece piece = gameTable.get(p);
            if (piece != null) {
                final BufferedImage image = theme.getImage(piece);
                g2.drawImage(image,
                        point.x - image.getWidth() / 2,
                        point.y - image.getHeight() / 2,
                        null
                );
            }
        }
    }

    private void drawLine(Graphics2D g2, int xStep, int yStep, int x, int y, Move m) {
        g2.setColor(theme.getColor(m.isOneDimensional() ? GRID : DIAGONAL));
        g2.drawLine(x, y, x + m.xStep() * xStep, y + m.yStep() * yStep);
    }

    private Point lastDragPoint;
    private BufferedImage draggedImage;

    public void setLastDragPoint(Point lastDragPoint) {
        this.lastDragPoint = lastDragPoint;
    }

    @Override
    public void notifyDraggedTo(Point point, Piece piece) {
        draggedImage = theme.getImage(piece);
        if (lastDragPoint != null) {
            repaintForDrag(lastDragPoint);
        }
        if (point != null) {
            point.translate(-draggedImage.getWidth() / 2, -draggedImage.getHeight() / 2);
            if (!point.equals(lastDragPoint)) {
                repaintForDrag(point);
            }
        }
        setLastDragPoint(point);
    }

    private void repaintForDrag(Point p) {
        repaint(addPixels(getRectangle(p, draggedImage), 2));
    }

    @Override
    public Size getScreenSize() {
        final GraphicsConfiguration gc = getGraphicsConfiguration();
        if (gc == null) {
            return null;
        } else {
            final Rectangle screenSize = gc.getBounds();
            return new IntPair(screenSize.width, screenSize.height);
        }
    }

    private void drawDraggedImage(Graphics2D g2) {
        if (lastDragPoint != null && draggedImage != null) {
            g2.drawImage(draggedImage, lastDragPoint.x, lastDragPoint.y, null);
            g2.setStroke(theme.getDragBoxStroke());
            final Rectangle r = getRectangle(lastDragPoint, draggedImage);
            g2.drawRect(r.x, r.y, r.width, r.height);
        }
    }

    private Rectangle getRectangle(Point point, BufferedImage image) {
        final int pad = DRAG_FRAME_PADDING;
        final int x = point.x - pad;
        final int y = point.y - pad;
        final int width = image.getWidth() + 2 * pad;
        final int height = image.getHeight() + 2 * pad;
        return new Rectangle(x, y, width, height);
    }

    private static Rectangle addPixels(Rectangle r, int pixels) {
        return new Rectangle(
                r.x - pixels, r.y - pixels,
                r.width + 2 * pixels,
                r.height + 2 * pixels
        );
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

    public GameTable getGameTable() {
        return gameTable;
    }

    @Override
    public void applyThemeChange() {
        setBackground(theme.getColor(BACKGROUND));
    }

}
