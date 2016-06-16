package net.doepner.baghchal;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Phases of the game (beginning, middle, end)
 */
public class Levels {

    private final int maxLevel;

    private int level;
    private boolean levelDone;

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

    public int firstLevel() {
        level = 1;
        levelDone = false;
        return level;
    }

    public String getLevelEndMessage() {
        return isGameOver() ? "You have completed Bagh-Chal" : "Now try level " + (level + 1);
    }

    public Properties getLevelProperties() {
        final Properties p = new Properties();
        final InputStream stream = getClass().getResourceAsStream("levels/" + level + "/level.properties");
        try {
            p.load(stream);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return p;
    }

    public String getLevelProperty(String name) {
        return getLevelProperties().get(name).toString();
    }

    public void setLevelDone(boolean levelDone) {
        this.levelDone = levelDone;
    }
}
