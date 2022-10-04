package pp.util;

/**
 * StopWatch measures time and can be started and stopped. Whenever a stop watch is running, it accumulates the
 * seconds passing. Each stop watch represents time in seconds with resolution of nanoseconds.
 */
public class StopWatch {
    private long time;
    private long startTime;
    private boolean running;

    /**
     * Creates a stop watch in stopped state and 0 milliseconds accumulated.
     */
    public StopWatch() {
        running = false;
        time = 0L;
    }

    /**
     * Resets the stop watch to its initial state. This is in stopped state and 0 seconds accumulated.
     */
    public void reset() {
        running = false;
        time = 0L;
    }

    /**
     * Starts the stop watch, i.e., gets the stop watch in a running state. If the stop watch hasn't been reset
     * before, it adds the time passing to its current time value. A call to this method is ineffective if the
     * current state is not stopped.
     */
    public void start() {
        if (!running) {
            startTime = System.nanoTime();
            running = true;
        }
    }

    /**
     * Stops the stop watch, i.e., gets the stop watch in a stopped state, but doesn't reset it. The next call to
     * {@linkplain #start()} will then add the time passing to its current time value. A call to this method is
     * ineffective if the current state is not running.
     */
    public void stop() {
        if (running) {
            time += System.nanoTime() - startTime;
            running = false;
        }
    }

    /**
     * Returns the current time value in seconds. The stop watch's state is insignificant.
     */
    public double getTime() {
        if (running)
            return (time + System.nanoTime() - startTime) * 1e-9;
        else
            return time * 1.0e-9;
    }

    /**
     * Returns the current stop watch state.
     *
     * @return true if the stop watch is in a running state.
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Returns the current time value in String representation.
     *
     * @see StopWatch#getTime()
     */
    @Override
    public String toString() {
        return getTime() + " sec"; //NON-NLS
    }
}
