package net.doepner.baghchal;

/**
 * Phases of the game (beginning, middle, end)
 */
public class Phases {

    private int phase;
    private int level;

    public Phases() {
        level = 1;
        phase = 1;
    }

    public boolean isBeginning() {
        return phase == 1;
    }

    public boolean isMiddle() {
        return phase == 2;
    }

    public boolean isEnd() {
        return phase > 2;
    }

    public void setMiddle() {
        phase = 2;
    }

    public void setEnd() {
        phase = 3;
    }


    public int getLevel() {
        return level;
    }

    public int nextLevel() {
        if (!isOver()) {
            phase = 1;
            level++;
        }
        return level;
    }

    public boolean isOver() {
        return level > 7;
    }

    public int firstLevel() {
        level = 1;
        return level;
    }
}
