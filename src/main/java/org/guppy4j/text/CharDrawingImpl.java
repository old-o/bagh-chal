package org.guppy4j.text;

import static java.lang.System.lineSeparator;

import net.doepner.baghchal.model.Move;

/**
 * Simple character drawings
 */
public final class CharDrawingImpl implements CharDrawing {

    private final char[][] chars;

    CharDrawingImpl(int xDim, int yDim) {
        chars = new char[xDim][yDim];
    }

    @Override
    public void addLine(int x, int y, int xStep, int yStep, Move step) {
        chars[x + step.xStep()][y + step.yStep()] = lineChar(step);
    }

    private static char lineChar(Move step) {
        if (step.isOneDimensional()) {
            return step.xStep() == 0 ? '|' : '-';
        } else {
            return step.xStep() * step.yStep() > 0 ? '\\' : '/';
        }
    }

    @Override
    public void addChar(int x, int y, char c) {
        chars[x][y] = c;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (int y = 0; y < chars[0].length; y++) {
            sb.append(lineSeparator());
            for (char[] ac : chars) {
                final char c = ac[y];
                sb.append(c == 0 ? ' ' : c);
            }
        }
        return sb.toString();
    }
}
