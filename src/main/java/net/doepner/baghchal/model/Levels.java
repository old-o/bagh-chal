package net.doepner.baghchal.model;

import net.doepner.baghchal.resources.LevelProperties;
import net.doepner.baghchal.resources.LevelResources;

import java.net.URL;

/**
 * Phases of the game (beginning, middle, end)
 */
public final class Levels {

    private final LevelProperties levelProperties;
    private final LevelResources levelResources;
    private final int maxLevel;

    private int level;
    private boolean levelDone;

    public Levels(LevelProperties levelProperties, LevelResources levelResources, int maxLevel) {
        this.levelProperties = levelProperties;
        this.levelResources = levelResources;
        this.maxLevel = maxLevel;
        firstLevel();
    }

    public boolean isLevelDone() {
        return levelDone;
    }

    public int getLevel() {
        return level;
    }

    public void nextLevel() {
        if (!isGameOver()) {
            levelDone = false;
            level++;
        }
    }

    public boolean isGameOver() {
        return level >= maxLevel && levelDone;
    }

    public void firstLevel() {
        level = 1;
        levelDone = false;
    }

    public String getLevelEndMessage() {
        return isGameOver() ? "You won Bagh-Chal!" : "Now try level " + (level + 1);
    }

    public void setLevelDone(boolean levelDone) {
        this.levelDone = levelDone;
    }

    public String getLevelProperty(String name) {
        return levelProperties.getProperties(level).get(name).toString();
    }

    public URL getResource(String fileName) {
        return levelResources.getResource(level, fileName);
    }
}
