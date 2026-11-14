/**
 * Handles basic flight physics, vertical speed, and ground braking.
 * 
 * @author Member 3
 */
public class FlightPhysics {
    private double verticalSpeed;
    private double timeStepSeconds;

    /**
     * Sets up the physics engine with a fixed time step.
     * 
     * @param timeStepSeconds length of each step in seconds
     */
    public FlightPhysics(double timeStepSeconds) {
        this.verticalSpeed = 0.0;
        this.timeStepSeconds = timeStepSeconds;    }

    /**
     * Updates altitude, speed, and runway distance for one step.
     * @param plane  the aircraft being simulated
     * @param runway the runway it's on
     */
    public void updateFlightStep(Aircraft plane, Runway runway) {
        // convert knots to approx ft/s (1 knot= 1.69 ft/s)
        double forwardFeetPerSec = plane.getSpeed() * 1.69;

        if (plane.getAltitude() > 0.0) {
            // vertical speed depends on pitch angle and forward speed
            verticalSpeed = (plane.getPitch() *1.8) - (140.0 / (plane.getSpeed() + 1.0)) * 8.0;
            double newAltitude = plane.getAltitude() + (verticalSpeed * timeStepSeconds);
            plane.setAltitude(newAltitude);
        } else {
            // on the ground: apply brakes or rolling friction
            verticalSpeed = 0.0;
            if (plane.isBrakesApplied()) {
                plane.adjustThrottle(-35.0);
            } else {
                plane.adjustThrottle(-5.0);
            }     }

        double horizontalDistance = forwardFeetPerSec * timeStepSeconds;
        runway.advancePosition(horizontalDistance);
    }
 
    // returns vertical speed (negative = descending)
    public double getVerticalSpeed() {
        return verticalSpeed;
    }
}
