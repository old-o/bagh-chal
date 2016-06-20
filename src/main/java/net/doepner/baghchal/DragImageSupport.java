package net.doepner.baghchal;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;

/**
 * Handles details of dragging an image
 */
public class DragImageSupport implements DragEventHandler, Painter {

    private final Component ui;
    private final Image image;

    private Point dragStartPoint;
    private Point lastDragPoint;

    public DragImageSupport(Component ui, Image image) {
        this.ui = ui;
        this.image = image;
    }

    @Override
    public void draggingStarted(Point point) {
        dragStartPoint = point;
        setLastDragPoint(point);
    }

    @Override
    public void draggedAt(Point p) {
        p.translate(-image.getWidth(null) / 2, -image.getHeight(null) / 2);
        repaintDraggedAt(p);
    }

    @Override
    public void releasedAt(Point point) {
        repaintDraggedAt(point);
        repaintDraggedAt(dragStartPoint);
        clearLastDragPoint();
        dragStartPoint = null;
    }

    public void repaintDraggedAt(Point p) {
        if (lastDragPoint != null) {
            repaintRectangle(lastDragPoint, image);
        }
        if (p != null && !p.equals(lastDragPoint)) {
            repaintRectangle(p, image);
        }
        setLastDragPoint(p);
    }

    void drawDraggedImage(Graphics2D g2) {
        if (lastDragPoint != null) {
            g2.drawImage(image, lastDragPoint.x, lastDragPoint.y, null);
        }
    }

    private void repaintRectangle(Point p, Image img) {
        int imgWidth = img.getWidth(null);
        int imgHeight = img.getHeight(null);
        ui.repaint(p.x - imgWidth, p.y - imgHeight, 2 * imgWidth, 2 * imgHeight);
    }

    public void clearLastDragPoint() {
        setLastDragPoint(null);
    }

    public void setLastDragPoint(Point lastDragPoint) {
        this.lastDragPoint = lastDragPoint;
    }

    @Override
    public void paint(Graphics2D g2) {
        drawDraggedImage(g2);
    }
}
