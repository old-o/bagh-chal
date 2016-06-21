package net.doepner.baghchal;

/**
 * Phases of the game (beginning, middle, end)
 */
public class Levels {

    private final LevelProperties levelProperties;

    private final int maxLevel;

    private int level;
    private boolean levelDone;

    Levels(LevelProperties levelProperties, int maxLevel) {
        this.levelProperties = levelProperties;
        this.maxLevel = maxLevel;
        firstLevel();
    }

    boolean isLevelDone() {
        return levelDone;
    }

    int getLevel() {
        return level;
    }

    void nextLevel() {
        if (!isGameOver()) {
            levelDone = false;
            level++;
        }
    }

    boolean isGameOver() {
        return level >= maxLevel;
    }

    void firstLevel() {
        level = 1;
        levelDone = false;
    }

    String getLevelEndMessage() {
        return isGameOver() ? "You won Bagh-Chal!" : "Now try level " + (level + 1);
    }

    String getLevelProperty(String name) {
        return levelProperties.getProperties(level).get(name).toString();
    }

    void setLevelDone(boolean levelDone) {
        this.levelDone = levelDone;
    }
}
