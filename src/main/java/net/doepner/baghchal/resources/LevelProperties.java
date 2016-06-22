package net.doepner.baghchal.resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Loads and caches level properties
 */
public class LevelProperties {

    private final ConcurrentMap<Integer, Properties> propertiesMap = new ConcurrentHashMap<>();

    private final LevelResources resources;
    private final String fileName;

    public LevelProperties(LevelResources resources, String fileName) {
        this.resources = resources;
        this.fileName = fileName;
    }

    public Properties getProperties(int level) {
        final Properties p = propertiesMap.get(level);
        return p != null ? p : loadAndCacheProperties(level);
    }

    private Properties loadAndCacheProperties(int level) {
        final Properties p = new Properties();
        try (final InputStream stream = resources.getResource(level, fileName).openStream()) {
            p.load(stream);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        propertiesMap.put(level, p);
        return p;
    }
}
