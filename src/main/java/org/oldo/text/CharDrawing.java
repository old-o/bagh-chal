package org.oldo.text;

import org.oldo.baghchal.model.Move;

/**
 * A character drawing using grid coordinates
 */
public interface CharDrawing {

    void addLine(int x, int y, int xStep, int yStep, Move step);

    void addChar(int x, int y, char c);
}
