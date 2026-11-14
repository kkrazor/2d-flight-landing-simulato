/**
 * Evaluates touchdown conditions, crash triggers, and safe landing completion.
 * 
 * @author Raulin K
 */
public class LandingEvaluator {
    private boolean crashed;
    private boolean landedSafely;
    private String statusMessage;
    private double touchdownVerticalSpeed;

    /**
     * Constructs a LandingEvaluator in an active flight state.
     */
    public LandingEvaluator() {
		// by default, weant the crashed and landSafely variabel to be set to false because the plane inntially hasnt landed or crashed
        this.crashed = false;
        this.landedSafely = false;
        this.statusMessage = "Approach in progress.";
        this.touchdownVerticalSpeed = 0.0;
    }

    /**
     * Evaluates the aircraft's current state against crash and landing rules.
    *
     * @param plane the aircraft being evaluated
     * @param runway  the runway being approached
     * @param physics the flightPhysics tracking vertical descent
     */
    public void evaluateState(Aircraft plane, Runway runway, FlightPhysics physics) {
		// If the plane has already or at the moment crashed or finsiehd alnding safely, then this ends the loop
        if (crashed || landedSafely) {
            return;
        }

        // Check if the aircraft has touched the ground
        if (plane.getAltitude() <= 0.0) {
            if (touchdownVerticalSpeed == 0.0) {
                touchdownVerticalSpeed = Math.abs(physics.getVerticalSpeed());
            }

            // Compound boolean crash checks
            if (!runway.isOverRunway() && !runway.isPastRunway()) {
                crashed = true;
                statusMessage = "CRASH: Landed short of the runway threshold!";
            } else if (runway.isPastRunway()) {
                crashed = true;
                statusMessage = "CRASH: Overshot the runway and hit terrain!";
           } else if (!plane.isGearDeployed()) {
                 crashed = true;
                statusMessage = "CRASH: Touched down without deploying landing gear!";
            } else if (plane.getPitch() < -5.0) {
                crashed = true;
                statusMessage = "CRASH: Nose first impact (pitch angle is too low)!";
            } else if (plane.getPitch() > 15.0) {
                crashed = true;
                statusMessage = "CRASH: Tail-strike on touchdown (pitch angle too high)!";
          } else if (touchdownVerticalSpeed > runway.getMaxSafeVerticalSpeed()) {
                crashed = true;
                statusMessage = "CRASH: Vertical descent rate exceeded structural limit!";
            } else if (plane.getSpeed() <= 0.0) {
                landedSafely = true;
                statusMessage = "SUCCESS: Aircraft has been brought to a complete, safe stop on the runway!";
            } else {
                statusMessage = "ON RUNWAY: Apply wheel brakes (B) to come to a complete stop!";  }
        } else if (runway.isPastRunway()) {
            crashed = true;
            statusMessage = "CRASH: Flew past the runway without landing!";      }   }

    /**
     * Checks if the flight has ended due to a crash or safe stop.
     * @return true if the simulation loop should terminate
     */
    public boolean isFlightOver() {
        return crashed || landedSafely;
    }

    /**
     * Checks if the aircraft crashed.
     * @return true if crashed
     */
    public boolean hasCrashed() {
        return crashed;
    }

    /**
     * Checks if the aircraft landed and stopped safely. 
     * @return true if landed safely
     */
    public boolean hasLandedSafely() {
        return landedSafely;
    }

    /**
     * Gets the latest status or crash explanation message.
     * @return descriptive status string
     */
    public String getStatusMessage() {
        return statusMessage;
    }

    /**
     * Gets the recorded vertical speed at the moment of touchdown.
     * @return touchdown descent rate in ft/sec
     */
    public double getTouchdownVerticalSpeed() {
        return touchdownVerticalSpeed;
    }}
