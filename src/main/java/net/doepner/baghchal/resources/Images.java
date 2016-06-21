package net.doepner.baghchal.resources;

import net.doepner.baghchal.model.Levels;
import net.doepner.baghchal.model.Piece;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static net.doepner.baghchal.model.Piece.PREDATOR;

/**
 * Loads images for play pieces from classpath
 */
public class Images {

    private final ConcurrentMap<URL, BufferedImage> cache = new ConcurrentHashMap<>();
    private final Levels levels;
    private final LevelResources resources;

    public Images(Levels levels, LevelResources resources) {
        this.levels = levels;
        this.resources = resources;
    }

    public Image getImage(Piece piece) {
        return getImage(piece == PREDATOR ? "predator.png" : "prey.png");
    }

    public BufferedImage getImage(String resourceFileName) {
        return getImageResource(resources.getResource(levels.getLevel(), resourceFileName));
    }

    public BufferedImage getImageResource(URL resourceLocation) {
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
