package net.doepner.baghchal.resources;

import net.doepner.baghchal.model.Levels;
import net.doepner.baghchal.model.Piece;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static net.doepner.baghchal.model.Piece.PREDATOR;

/**
 * Loads images for control pieces from classpath
 */
public class Images {

    private final ConcurrentMap<URL, BufferedImage> cache = new ConcurrentHashMap<>();
    private final Levels levels;

    public Images(Levels levels) {
        this.levels = levels;
    }

    public BufferedImage getImage(Piece piece) {
        return getImage(piece == PREDATOR ? "predator.png" : "prey.png");
    }

    public BufferedImage getImage(String resourceFileName) {
        return getImage(levels.getResource(resourceFileName));
    }

    public BufferedImage getImage(URL resourceLocation) {
        final BufferedImage cachedImage = cache.get(resourceLocation);
        if (cachedImage != null) {
            return cachedImage;
        } else {
            final BufferedImage image = loadImage(resourceLocation);
            cache.put(resourceLocation, image);
            return image;
        }
    }

    private BufferedImage loadImage(URL location) {
        try {
            return ImageIO.read(location);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
