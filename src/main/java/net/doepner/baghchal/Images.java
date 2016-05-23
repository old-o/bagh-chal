package net.doepner.baghchal;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

/**
 * Loads images for play pieces from classpath
 */
public class Images {

    private static final String[] TIGER_IMAGE_NAMES = {
            "1_BrownMoth.gif", "2_Bunny.gif", "3_Crab.gif", "4_Snail.gif",
            "5_FishGold.gif", "6_FishBlue.gif", "7_CoolShark.gif"
    };


    Image getImage(String s) {
        try {
            return ImageIO.read(getClass().getResource(s));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public Image getTigerImage(int level) {
        return getImage(TIGER_IMAGE_NAMES[level - 1]);
    }

    public Image getGoatImage() {
        return getImage("1_Buckeye.gif");
    }
}
