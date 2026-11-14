/**
 * Tracks the plane's current speed, altitude, pitch, gear, and brakes.
 * 
 * @author Aditya B
 */
public class Aircraft {
    private double speed;
    private double altitude;
    private double pitch;
    private boolean gearDeployed;
    private boolean brakesApplied;

    /**
     * Creates a new aircraft with starting flight values.
     * 
     * @param startSpeed    starting speed in knots
     * @param startAltitude starting altitude in feet
     * @param startPitch    starting pitch angle in degrees
     */
    public Aircraft(double startSpeed, double startAltitude, double startPitch) {
        this.speed = startSpeed;
        this.altitude = startAltitude;
        this.pitch = startPitch;
        this.gearDeployed = false;
        this.brakesApplied = false;
    }

    /**
     * Changes the nose pitch and keeps it within -25 to 25 degrees.
     * 
     * @param deltaPitch degrees to add or subtract from current pitch
     */
    public void adjustPitch(double deltaPitch) {
        pitch += deltaPitch;
        // keep pitch between -25 and 25 degrees so the plane doesnt flip or stall
        if (pitch > 25.0) {
            pitch = 25.0;
        } else if (pitch < -25.0) {
            pitch = -25.0;
        }
    }

    /**
     * Changes the airspeed and caps it between 0 and 250 knots.
     * 
     * @param deltaSpeed knots to add or subtract from current speed
     */
    public void adjustThrottle(double deltaSpeed) {
        speed += deltaSpeed;
        // make sure speed stays realistic and doesnt go negative or above max engine limit
        if (speed < 0.0) {
            speed = 0.0;
        } else if (speed > 250.0) {
            speed = 250.0;
        }
    }

    /**
     * Lowers the landing gear if it is still up.
     * 
     * @return true if the gear was lowered, false if it was already down
     */
    public boolean deployGear() {
        // only lower the gear if its currently up so we can tell the player if it actually moved
        if (!gearDeployed) {
            gearDeployed = true;
            return true;
        }
        return false;
    }

    /**
     * Turns on wheel brakes if the plane is on the ground.
     * 
     * @return true if brakes were applied, false if still in the air
     */
    public boolean applyBrakes() {
        // wheel brakes wont do anything until the plane is actually touching the runway
        if (altitude <= 0.0) {
            brakesApplied = true;
            return true;
        }
        return false;
    }

    /**
     * Sets the altitude and stops it from dropping below zero.
     * 
     * @param newAltitude updated altitude in feet
     */
    public void setAltitude(double newAltitude) {
        // prevent the plane from going underground
        if (newAltitude < 0.0) {
            this.altitude = 0.0;
        } else {
            this.altitude = newAltitude;
        }
    }

    // getters for current plane status
    public double getSpeed() {
        return speed;
    }

    public double getAltitude() {
        return altitude;
    }

    public double getPitch() {
        return pitch;
    }

    public boolean isGearDeployed() {
        return gearDeployed;
    }

    public boolean isBrakesApplied() {
        return brakesApplied;
    }
}
