package net.doepner.baghchal;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static net.doepner.baghchal.Piece.PREDATOR;

/**
 * Loads images for play pieces from classpath
 */
public class Images {

    private final Phases phases;

    private final ConcurrentMap<String, BufferedImage> cache = new ConcurrentHashMap<>();

    public Images(Phases phases) {
        this.phases = phases;
    }

    BufferedImage getImage(String resourceFileName) {
        final String resourceFilePath = phases.getLevel() + "/" + resourceFileName;
        final BufferedImage cachedImage = cache.get(resourceFilePath);
        if (cachedImage != null) {
            return cachedImage;
        } else {
            final BufferedImage image = loadImage(resourceFilePath);
            cache.put(resourceFileName, image);
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

    public Image getImage(Piece piece) {
        return getImage(piece == PREDATOR ? "predator.png" : "prey.png");
    }
}
