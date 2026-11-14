/**
 * Formats pilot identifiers using String methods and calculates landing scores.
 * 
 * @author Aiden N
 */
public class ScoreCalculator {
    private String pilotCallsign;
    private int totalScore;

    /**
     * Constructs a ScoreCalculator and formats the raw pilot name into a callsign.
     * @param rawPilotName user-entered pilot name
     */
    public ScoreCalculator(String rawPilotName) {
        this.pilotCallsign = formatCallsign(rawPilotName);
        this.totalScore = 0;
    }

    /**
     * Cleans and formats the pilot name using String manipulation methods.
     * 
     * @param rawName the raw text entered by the user
     * @return formatted uppercase callsign badge
     */
    public String formatCallsign(String rawName) {
        String cleaned = rawName.trim().toUpperCase();
        int spaceIndex = cleaned.indexOf(" ");
        if (spaceIndex != -1) {
            cleaned = cleaned.substring(0, spaceIndex);
        }
        if (cleaned.length() > 8) {
            cleaned = cleaned.substring(0, 8);
        }
        return "CAPT-" + cleaned;
    }

    /**
     * Calculates the final score based on touchdown smoothness and your runway position.
     * 
     * @param evaluator the LandingEvaluator containing touchdown metrics
     * @param runway    the Runway used for landing
     * @return total calculated points
     */
    public int calculateFinalScore(LandingEvaluator evaluator, Runway runway) {
		// returns a score of 0 if plane has crashed or has landed unsafe
        if (evaluator.hasCrashed() || !evaluator.hasLandedSafely()) {
            totalScore = 0;
            return totalScore;
        }

        double impactSpeed = evaluator.getTouchdownVerticalSpeed();
        double maxSafe = runway.getMaxSafeVerticalSpeed();

        // Base landing score
int basePoints;
// checks if landing speed is less than or equals to half of max sae speed
if (impactSpeed <= maxSafe * 0.5) {
	// if true give 1000 points base
    basePoints = 1000;
} else {
	// if didnt meet requiremnts, base poitns is 500
    basePoints = 500;
}

        // Smoothness bonus inversely proportional to impact force
        int smoothnessBonus = (int) ((maxSafe - impactSpeed) * 25.0);
        if (smoothnessBonus < 0) {
            smoothnessBonus = 0;
        }

        // Stop bonus for halting safely on the runway
        int stopBonus = 250;
        
// adds total score
        totalScore = basePoints + smoothnessBonus + stopBonus;
        
        return totalScore;
    }

    /**
     * Gets the formatted pilot callsign.
     * 
     * @return formatted callsign string
     */
    public String getPilotCallsign() {
        return pilotCallsign;
    }

    /**
     * Gets the final score.
     * 
     * @return total score
     */
    public int getTotalScore() {
        return totalScore;
    }
}
