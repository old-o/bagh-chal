package org.guppy4j;

/**
 * Defines a target duration, then starts a timer and later
 * waits for the required remaining time
 */
public final class WaitClock {

    private final long millis;
    private long start;

    public WaitClock(long millis) {
        this.millis = millis;
    }

    public void start() {
        start = System.currentTimeMillis();
    }

    public void waitRemaining() {
        final long durationMillis = System.currentTimeMillis() - start;
        if (durationMillis < millis) {
            try {
                Thread.sleep(millis - durationMillis);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
    }

}
