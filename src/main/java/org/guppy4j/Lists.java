package org.guppy4j;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Static utility methods for dealing with lists
 */
public final class Lists {

    /**
     * @param list A list
     * @param <T> The list element type
     * @return A randomly chosen element from the provided list, or null if the list is null or empty
     */
    public static <T> T getRandomFrom(List<T> list) {
        return Objects.isNullOrEmpty(list) ? null : list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }

}
