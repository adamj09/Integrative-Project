package oms.Physics;

/**
 * Class that controls orbital simulation time.
 * 
 * @author Maxime Gauthier
 */
public class OrbitsTime implements Runnable {
    private static final long NANOS_PER_MS = 1_000_000L;

    private double timeScale = 1;
    private double lastTimeScale = 1;
    private double finalTime; // in milliseconds (simulation time)
    private double tempTime = 0;
    private long startTimeReal; // when simulation started in nanoseconds
    private long totalPausedTime; // total time spent paused (in nanoseconds)
    private long pauseStartTime; // when we paused in nanoseconds
    private boolean runningStatus = false;
    private Thread timeThread; // thread for time calculations

    /**
     * Creates a new time manager with time set to zero.
     */
    public OrbitsTime() {
        this.startTimeReal = 0;
        this.totalPausedTime = 0;
        this.pauseStartTime = 0;
        this.timeThread = null;
    }

    /**
     * Sets a time multiplier.
     * 
     * Results in 1 second of real time being equal to x seconds of simulation time,
     * where x is the timeScale.
     * 
     * @param timeScale the multiplier to set.
     */
    public void setTimeScale(double timeScale) {
        if (timeScale != this.timeScale) {
            this.lastTimeScale = this.timeScale;
            this.timeScale = timeScale;
        }
    }

    /**
     * @return the current time scale.
     */
    public double getTimeScale() {
        return this.timeScale;
    }

    /**
     * Resets time to zero.
     */
    public void resetTime() {
        this.finalTime = 0;
        this.startTimeReal = System.nanoTime();
        this.totalPausedTime = 0;
        this.pauseStartTime = 0;
        this.tempTime = 0;
    }

    /**
     * Set the time to a specific value (in seconds).
     * 
     * @param timeSeconds the time value in seconds.
     */
    public void setTime(double timeSeconds) {
        this.resetTime();
        this.tempTime = timeSeconds * 1000; // convert to milliseconds
        calculateTime();
    }

    /**
     * Starts incrementing time. If time was paused, adds paused time before
     * beginning to increment once more.
     */
    public void start() {
        if (!this.runningStatus) {
            // If we're resuming from a pause, accumulate the pause duration
            if (this.pauseStartTime > 0) {
                this.totalPausedTime += System.nanoTime() - this.pauseStartTime;
                this.pauseStartTime = 0;
            } else if (this.startTimeReal == 0) {
                // First start of the simulation
                this.startTimeReal = System.nanoTime();
            }
        }
        this.runningStatus = true;
    }

    /**
     * Stops incrementing time.
     */
    public void stop() {
        this.runningStatus = false;
        if (this.pauseStartTime == 0) {
            this.pauseStartTime = System.nanoTime();
        }
    }

    /**
     * 
     * @return true if the time is runing or false if it's stop
     */
    public boolean getRunningSatus() {
        return this.runningStatus;
    }

    /**
     * @return the current time in seconds.
     */
    public synchronized double getTimeSeconds() {
        return this.finalTime / 1000;
    }

    /**
     * Starts the time calculation thread
     */
    public void startTimeThread() {
        if (this.timeThread == null || !this.timeThread.isAlive()) {
            this.timeThread = new Thread(this);
            this.timeThread.start();
        }
    }

    /**
     * Stops the time calculation thread
     */
    public void stopTimeThread() {
        if (this.timeThread != null && this.timeThread.isAlive()) {
            this.timeThread.interrupt();
            try {
                this.timeThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.timeThread = null;
    }

    /**
     * Updates all satellites with the current simulation time.
     * This method should be overridden in subclasses that manage satellites.
     */
    protected void updateSatellitesTime() {
        // This method will be overridden in Body class
    }

    /**
     * Runs the time thread.
     */
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            calculateTime();
            updateSatellitesTime(); // Update satellites with new time
        }
    }

    /**
     * Calculates the elapsed time with support for pause/resume and time scaling.
     * Called repeatedly in the run method to update the simulation time.
     * Derives simulation time directly from real elapsed time to avoid accumulation
     * errors.
     * Properly accounts for paused periods. Uses nanosecond precision.
     */
    private synchronized void calculateTime() {
        if (startTimeReal > 0) {

            if (this.lastTimeScale != this.timeScale) {
                double temp = this.finalTime;
                this.lastTimeScale = this.timeScale;
                this.resetTime();
                this.tempTime = temp;
            }

            // Calculate elapsed real time since simulation started, minus paused periods
            // (in nanoseconds)
            long currentRealTime = System.nanoTime();
            long elapsedRealTimeNanos = currentRealTime - startTimeReal - totalPausedTime;

            // Convert to milliseconds and apply time scale
            double elapsedRealTimeMs = elapsedRealTimeNanos / (double) NANOS_PER_MS;
            this.finalTime = this.tempTime + (elapsedRealTimeMs * this.timeScale);
        }
    }
}
