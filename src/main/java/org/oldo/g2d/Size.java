package org.oldo.g2d;

/**
 * 2-dimensional size interface
 */
public interface Size {

    int x();

    int y();

    default boolean sameAs(Size size) {
        return x() == size.x() && y() == size.y();
    }

    default int getMaxDimension() {
        return Math.max(x(), y());
    }

}
