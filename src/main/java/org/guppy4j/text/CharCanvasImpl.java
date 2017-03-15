package org.guppy4j.text;

/**
 * Creates character drawings
 */
public final class CharCanvasImpl implements CharCanvas {

    @Override
    public CharDrawing newDrawing(int xDim ,int yDim) {
        return new CharDrawingImpl(xDim, yDim);
    }
}
