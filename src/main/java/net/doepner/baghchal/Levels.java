package net.doepner.baghchal;

import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Phases of the game (beginning, middle, end)
 */
public class Levels {

    private static final Class<?> myClass = MethodHandles.lookup().lookupClass();

    private final int maxLevel;

    private int level;
    private boolean levelDone;

    private final Map<Integer, Properties> propertiesMap = new HashMap<>();

    public Levels(int maxLevel) {
        this.maxLevel = maxLevel;
        firstLevel();
    }

    public boolean isLevelDone() {
        return levelDone;
    }

    public int getLevel() {
        return level;
    }

    public int nextLevel() {
        if (!isGameOver()) {
            levelDone = false;
            level++;
        }
        return level;
    }

    public boolean isGameOver() {
        return level >= maxLevel;
    }

    public void firstLevel() {
        level = 1;
        levelDone = false;
    }

    public String getLevelEndMessage() {
        return isGameOver() ? "You won Bagh-Chal!" : "Now try level " + (level + 1);
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

    private static Properties loadProperties(int level) {
        final Properties p = new Properties();
        final InputStream stream = myClass.getResourceAsStream("levels/" + level + "/level.properties");
        try {
            p.load(stream);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return p;
    }

    public String getLevelProperty(String name) {
        return getProperties(level).get(name).toString();
    }

    public void setLevelDone(boolean levelDone) {
        this.levelDone = levelDone;
    }
}
