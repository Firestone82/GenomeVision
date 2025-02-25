package cz.mik0486.semestralproject.utils.log;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

public class Debouncer<T> {
    private Timer timer;
    private final int delay;
    private final Consumer<T> callback;

    public Debouncer(int delay, Consumer<T> callback) {
        this.delay = delay;
        this.callback = callback;
    }

    /**
     * Call this method with the latest argument. The callback will be invoked
     * after the specified delay if no new calls occur in the meantime.
     */
    public void call(T argument) {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }

        timer = new Timer(delay, (ActionEvent e) -> callback.accept(argument));
        timer.setRepeats(false);
        timer.start();
    }
}
