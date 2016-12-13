package net.doepner.baghchal.resources;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Loads and caches images
 */
public final class ImageLoader {

    private final ConcurrentMap<URL, BufferedImage> cache = new ConcurrentHashMap<>();

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
