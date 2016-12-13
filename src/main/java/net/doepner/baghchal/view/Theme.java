package net.doepner.baghchal.view;

import net.doepner.baghchal.resources.Images;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.net.URL;

/**
 * Theming (images, colors, etc)
 */
public interface Theme extends Images {

    enum ColorId { GRID, DIAGONAL, BOARD_EDGE, BACKGROUND }

    Color getColor(ColorId colorId);

    enum SoundResourceId { WELCOME, PREDATOR_KILLS, PREY_MOVES, PREDATOR_MOVES }

    URL getSoundResource(SoundResourceId resourceId);

    int getPieceWidth();

    int getPieceHeight();

    Paint getBoardPaint();

    Stroke getDragBoxStroke();
}
