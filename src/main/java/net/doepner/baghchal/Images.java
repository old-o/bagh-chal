package net.doepner.baghchal;

import static net.doepner.baghchal.Piece.PREDATOR;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.imageio.ImageIO;

/**
 * Loads images for play pieces from classpath
 */
public class Images {

    private final ConcurrentMap<String, BufferedImage> cache = new ConcurrentHashMap<>();
    private final Levels levels;

    Images(Levels levels) {
        this.levels = levels;
    }

    BufferedImage getImage(String resourceFileName) {
        final String resourceFilePath = "levels/" + levels.getLevel() + "/" + resourceFileName;
        return getImageResource(resourceFilePath);
    }

    Image getImage(Piece piece) {
        return getImage(piece == PREDATOR ? "predator.png" : "prey.png");
    }

    BufferedImage getImageResource(String resourceFilePath) {
        final BufferedImage cachedImage = cache.get(resourceFilePath);
        if (cachedImage != null) {
            return cachedImage;
        } else {
            final BufferedImage image = loadImage(resourceFilePath);
            cache.put(resourceFilePath, image);
            return image;
        }
    }

    private BufferedImage loadImage(String s) {
        try {
            return ImageIO.read(getClass().getResource(s));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
