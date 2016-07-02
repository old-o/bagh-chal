package net.doepner.baghchal.util;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * TODO: Document this!
 */
public final class ListUtil {

    public static <T> T getRandomFrom(List<T> list) {
        return list.isEmpty() ? null : list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }
}
