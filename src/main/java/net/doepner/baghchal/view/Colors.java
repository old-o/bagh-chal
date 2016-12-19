package net.doepner.baghchal.view;

import net.doepner.baghchal.theming.Theme.ColorId;

import java.awt.Color;
import java.util.EnumMap;
import java.util.Map;
import java.util.Properties;

/**
 * UI colors for a game theme
 */
public final class Colors {

    private final Map<ColorId, Color> colorMap = new EnumMap<>(ColorId.class);

    public Colors(Properties properties) {
        for (ColorId colorId : ColorId.values()) {
            colorMap.put(colorId, Color.decode(properties.getProperty(key(colorId))));
        }
    }

    public Color getColor(ColorId colorId) {
        return colorMap.get(colorId);
    }

    private static String key(ColorId colorId) {
        switch (colorId) {
            case BACKGROUND:
                return "backgroundColor";
            case BOARD_EDGE:
                return "boardEdgeColor";
            case DIAGONAL:
                return "diagonalColor";
            case GRID:
                return "gridColor";
        }
        throw new IllegalArgumentException("Unknown color id : " + colorId);
    }

}
