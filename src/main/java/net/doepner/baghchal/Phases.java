package net.doepner.baghchal;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Phases of the game (beginning, middle, end)
 */
public class Phases {

    private final int maxLevel;

    private Phase phase;
    private int level;

    public Phases(int maxLevel) {
        this.maxLevel = maxLevel;
        firstLevel();
    }

    public boolean isBeginning() {
        return phase == Phase.BEGINNING;
    }

    public boolean isMiddle() {
        return phase == Phase.MIDDLE;
    }

    public boolean isEnd() {
        return phase == Phase.END;
    }

    public void setMiddle() {
        phase = Phase.MIDDLE;
    }

    public void setEnd() {
        phase = Phase.END;
    }


    public int getLevel() {
        return level;
    }

    public int nextLevel() {
        if (!isGameOver()) {
            phase = Phase.BEGINNING;
            level++;
        }
        return level;
    }

    public boolean isGameOver() {
        return level >= maxLevel;
    }

    public int firstLevel() {
        level = 1;
        phase = Phase.BEGINNING;
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
}
