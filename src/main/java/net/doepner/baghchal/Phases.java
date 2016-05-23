package net.doepner.baghchal;

/**
 * Phases of the game (beginning, middle, end)
 */
public class Phases {

    int phase;

    public void reset() {
        phase = 1;
    }

    public boolean isBeforeGame() {
        return phase == 0;
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

}
