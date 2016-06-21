package net.doepner.baghchal;

import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Loads and caches level properties
 */
public class LevelProperties {

    private static final Class<?> myClass = MethodHandles.lookup().lookupClass();
    private final String resourcePathFormat;

    private final ConcurrentMap<Integer, Properties> propertiesMap = new ConcurrentHashMap<>();

    public LevelProperties(String resourcePathFormat) {
        this.resourcePathFormat = resourcePathFormat;
    }

    public Properties getProperties(int level) {
        final Properties properties = propertiesMap.get(level);
        if (properties == null) {
            final Properties loadedProperties = loadProperties(level);
            propertiesMap.put(level, loadedProperties);
            return loadedProperties;
        } else {
            return properties;
        }
    }

    private Properties loadProperties(int level) {
        final Properties p = new Properties();
        final InputStream stream = myClass.getResourceAsStream(String.format(resourcePathFormat, level));
        try {
            p.load(stream);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return p;
    }
}
