package org.guppy4j;

import java.util.Collection;

/**
 * Static utility methods for dealing with objects
 */
public class Objects {

    public static <T> boolean isNullOrEmpty(Collection<T> list) {
        return (list == null) || list.isEmpty();
    }
}
