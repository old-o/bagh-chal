package net.doepner.baghchal.view;

import java.awt.Color;

import net.doepner.baghchal.model.Levels;

/**
 * UI colors for a game level
 */
public class Colors {

    private final Color gridColor;
    private final Color diagonalColor;
    private final Color backgroundColor;
    private final Color boardEdgeColor;

    public Colors(Levels levels) {
        backgroundColor = getColor(levels, "backgroundColor");
        diagonalColor = getColor(levels, "diagonalColor");
        gridColor = getColor(levels, "gridColor");
        boardEdgeColor = getColor(levels, "boardEdgeColor");
    }

    private static Color getColor(Levels levels, String name) {
        return Color.decode(levels.getLevelProperty(name));
    }

    Color backgroundColor() {
        return backgroundColor;
    }

    Color boardEdgeColor() {
        return boardEdgeColor;
    }

    Color gridColor() {
        return gridColor;
    }

    Color diagonalColor() {
        return diagonalColor;
    }
}
