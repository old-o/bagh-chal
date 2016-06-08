package net.doepner.baghchal;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Phases of the game (beginning, middle, end)
 */
public class Phases {

    private int phase;
    private int level;

    public Phases() {
        firstLevel();
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
        if (!isGameOver()) {
            phase = 1;
            level++;
        }
        return level;
    }

    public boolean isGameOver() {
        return level > 2;
    }

    public int firstLevel() {
        level = 1;
        phase = 1;
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
