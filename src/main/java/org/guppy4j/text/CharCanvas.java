package org.guppy4j.text;

/**
 * A character "canvas" is a factory for character drawings
 */
public interface CharCanvas {

    CharDrawing newDrawing(int xDim, int yDim);
}
