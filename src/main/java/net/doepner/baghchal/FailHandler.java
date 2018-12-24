package net.doepner.baghchal;

import org.guppy4j.log.Log;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;
import static org.guppy4j.log.Log.Level.error;

/**
 * Handles exception during start-up
 */
public final class FailHandler implements Thread.UncaughtExceptionHandler {

    private final Log log;

    public FailHandler(Log log) {
        this.log = log;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        log.as(error, e);
        showMessageDialog(null, e.getMessage(), "Cannot start", ERROR_MESSAGE);
    }
}
