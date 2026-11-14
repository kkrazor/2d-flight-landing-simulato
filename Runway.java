/**
 * Represents the destination runway and tracks tje horizontal position according to it.
 * 
 * @author Aleksey K
 */
public class Runway {
    private double distanceToThreshold;
    private double runwayLength;
    private double maxSafeVerticalSpeed;

    /**
     * Constructs a Runway with a starting approach distance and physical length.
     * 
     * @param startDistance   horizontal distance to runway start in feet
     * @param runwayLength     total paved length of the runway in feet
     * @param maxSafeVerticalSpeed maximum safe descent rate at touchdown in ft/sec
     */
    public Runway(double startDistance, double runwayLength, double maxSafeVerticalSpeed) {
        this.distanceToThreshold = startDistance;
        this.runwayLength = runwayLength;
        this.maxSafeVerticalSpeed = maxSafeVerticalSpeed;
    }

    /**
     * Reduces the remaining distance to or along the runway based on forward motion.
     * 
     * @param horizontalTravel distance covered during the current time step in feet
     */
    public void advancePosition(double horizontalTravel) {
		// this decreases the distance as the plane moves forwar. The negative values mean the plane is past the beginning of the runway
        distanceToThreshold -= horizontalTravel;  }

    /**
     * Determines if the aircraft is currently aligned over the paved runway surface.
     * 
     * @return true if over the runway, false if short or past the end
     */
    public boolean isOverRunway() {
		
	// 0 is the start of the runway and -runwayLength is the end of it

        return distanceToThreshold <= 0.0 && distanceToThreshold >= -runwayLength;
    }

   /**
     * Determines if the aircraft has traveled past the far end of the runway.
     * @return true if overshot, false otherwise
     */
    public boolean isPastRunway() {
        return distanceToThreshold < -runwayLength;
    }

 /**
     * Gets the remaining distance to the start of the runway (negative means on runway).
     * @return distance in feet
     */
    public double getDistanceToThreshold() {
        return distanceToThreshold;
    }

    /**
     * Gets the total runway length. 
     * @return length in feet
     */
    public double getRunwayLength() {
        return runwayLength;
    }

    /**
     * Gets the structural descent speed limit for safe landing.
     * @return max safe vertical speed in ft/sec
     */
    public double getMaxSafeVerticalSpeed() {
        return maxSafeVerticalSpeed;
    }
    }
