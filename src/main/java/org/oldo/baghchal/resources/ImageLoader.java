package org.oldo.baghchal.resources;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Loads and caches images
 */
public final class ImageLoader {

    private final ConcurrentMap<URI, BufferedImage> cache = new ConcurrentHashMap<>();

    public BufferedImage getImage(URL resourceLocation) {
        final URI uri = toUri(resourceLocation);
        final BufferedImage cachedImage = cache.get(uri);
        if (cachedImage != null) {
            return cachedImage;
        } else {
            final BufferedImage image = loadImage(resourceLocation);
            cache.put(uri, image);
            return image;
        }
    }

    private static URI toUri(URL resourceLocation) {
        try {
            return resourceLocation.toURI();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static BufferedImage loadImage(URL location) {
        try {
            return ImageIO.read(location);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
