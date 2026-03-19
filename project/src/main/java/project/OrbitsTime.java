package project;

public class OrbitsTime implements Runnable{
    private double timeScale = 1;
    private double lastTime; // in millisecond
    private double finalTime; // in millisecond
    private boolean runningStatus = false;
    private Thread timeThread; // thread for time calculations


    public OrbitsTime(){
        this.lastTime = System.currentTimeMillis();
        this.timeThread = null;
    }

    /**
     * time scale like 0.2,1,2,...
     * @param timeScale
     */
    public void setTimeScale(double timeScale){
        this.timeScale = timeScale;
    }

    public void resetTime(){
        this.finalTime = 0;
        this.lastTime = System.currentTimeMillis();
    }

    /**
     * Set the time to a specific value (in seconds)
     * @param timeSeconds the time value in seconds
     */
    public void setTime(double timeSeconds){
        this.finalTime = timeSeconds * 1000; // convert to milliseconds
        this.lastTime = System.currentTimeMillis();
    }


    public void start(){
        this.runningStatus = true;
    }

    public void stop(){
        this.runningStatus = false;
    }

    /**
     * 
     * @return true if the time is runing or false if it's stop
     */
    public boolean getRunningSatus(){
        return this.runningStatus;
    }

    public double getTimeSeconds(){
        return this.finalTime/1000;
    }

    /**
     * Starts the time calculation thread
     */
    public void startTimeThread(){
        if (this.timeThread == null || !this.timeThread.isAlive()) {
            this.timeThread = new Thread(this);
            this.timeThread.start();
        }
    }

    /**
     * Stops the time calculation thread
     */
    public void stopTimeThread(){
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
    protected void updateSatellitesTime(){
        // This method will be overridden in Body class
    }


    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            calculateTime();
            updateSatellitesTime(); // Update satellites with new time
            try {
                Thread.sleep(Constant.UPDATE_TIME);        
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * Calculates the elapsed time with support for pause/resume and time scaling.
     * Called repeatedly in the run method to update the simulation time.
     */
    private void calculateTime(){
        double currentTime = System.currentTimeMillis();
        
        // Only update finalTime if the simulation is running
        if (runningStatus) {
            // Calculate the delta time since the last update
            double deltaTime = currentTime - lastTime;
            
            // Apply time scale to delta time and add to final time
            this.finalTime += deltaTime * this.timeScale;
        }
        
        // Always update lastTime to the current time
        this.lastTime = currentTime;
        
    }
}
 