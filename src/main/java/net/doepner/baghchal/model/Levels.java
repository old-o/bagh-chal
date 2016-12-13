package net.doepner.baghchal.model;

/**
 * Phases of the game (beginning, middle, end)
 */
public final class Levels {

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

}
