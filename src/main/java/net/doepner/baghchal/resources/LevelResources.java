package net.doepner.baghchal.resources;

import java.net.URL;

/**
 * Determines URLs of level-dependent resource files
 */
public final class LevelResources {

    private final String resourcePathFormat;

    public LevelResources(String resourcePathFormat) {
        this.resourcePathFormat = resourcePathFormat;
    }

    public URL getResource(int level, String fileName) {
        return getClass().getResource(String.format(resourcePathFormat, level, fileName));
    }
}
