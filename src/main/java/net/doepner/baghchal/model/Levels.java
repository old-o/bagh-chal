package net.doepner.baghchal.model;

import net.doepner.baghchal.resources.LevelProperties;

/**
 * Phases of the game (beginning, middle, end)
 */
public class Levels {

    private final LevelProperties levelProperties;

    private final int maxLevel;

    private int level;
    private boolean levelDone;

    public Levels(LevelProperties levelProperties, int maxLevel) {
        this.levelProperties = levelProperties;
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
        return level > maxLevel;
    }

    public void firstLevel() {
        level = 1;
        levelDone = false;
    }

    public String getLevelEndMessage() {
        return isGameOver() ? "You won Bagh-Chal!" : "Now try level " + (level + 1);
    }

    public String getLevelProperty(String name) {
        return levelProperties.getProperties(level).get(name).toString();
    }

    public void setLevelDone(boolean levelDone) {
        this.levelDone = levelDone;
    }
}
