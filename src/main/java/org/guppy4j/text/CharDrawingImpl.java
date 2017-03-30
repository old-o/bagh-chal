package org.guppy4j.text;

import net.doepner.baghchal.model.Move;

import static java.lang.System.lineSeparator;

/**
 * Simple character drawings
 */
public final class CharDrawingImpl implements CharDrawing {

    private final char[][] c;

    CharDrawingImpl(int xDim, int yDim) {
        c = new char[xDim][yDim];
    }

    @Override
    public void addLine(int x, int y, int xStep, int yStep, Move step) {
        c[x + step.xStep()][y + step.yStep()] = lineChar(step);
    }

    private char lineChar(Move step) {
        if (step.isOneDimensional()) {
            return step.xStep() == 0 ? '|' : '-';
        } else {
            return step.xStep() * step.yStep() > 0 ? '\\' : '/';
        }
    }

    @Override
    public void addChar(int x, int y, char c) {
        this.c[x][y] = c;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (int y = 0; y < c[0].length; y++) {
            sb.append(lineSeparator());
            for (int x = 0; x < c.length; x++) {
                final char c = this.c[x][y];
                sb.append(c == 0 ? ' ' : c);
            }
        }
        return sb.toString();
    }
}
