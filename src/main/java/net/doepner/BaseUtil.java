package net.doepner;

/**
 * Basic utility methods
 */
public final class BaseUtil {

    public static boolean bothNullOrEqual(Object o1, Object o2) {
        return o1 == null ? o2 == null : o1.equals(o2);
    }
}
