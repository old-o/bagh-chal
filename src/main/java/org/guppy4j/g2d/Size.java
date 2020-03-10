package org.guppy4j.g2d;

/**
 * 2-dimensional size interface
 */
public interface Size {

    int getX();

    int getY();

    default boolean isSameAs(Size size) {
        return getX() == size.getX() && getY() == size.getY();
    }

    default int getMaxDimension() {
        return Math.max(getX(), getY());
    }
}
