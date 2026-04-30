package project.Math;

/**
 * General math utilities (conversions, etc.).
 * 
 * @author Maxime Gauthier
 */
public class Utils {

    /**
     * Creates a String that displyas the current time as follows:
     * 
     * years days hours minutes seconds
     * 
     * @param timeSeconds the time in seconds from which to calculate the formatted time.
     * @return the current time as a String formatted as above.
     */
    public static String getWorldTimeFormated(double timeSeconds) {
        int secondsPerDay = 24 * 3600;
        int secondsPerYear = 365 * secondsPerDay;

        int years = (int) (timeSeconds / secondsPerYear);
        int days = (int) ((timeSeconds % secondsPerYear) / secondsPerDay);
        int hours = (int) ((timeSeconds % secondsPerDay) / 3600);
        int minutes = (int) ((timeSeconds % 3600) / 60);
        int seconds = (int) (timeSeconds % 60);

        return String.format("(%dy %dd %02dh %02dm %02ds)", years, days, hours, minutes, seconds);
    }
}
