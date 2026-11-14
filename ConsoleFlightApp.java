import java.util.Scanner;

/**
 * Console user interface for the Flight Landing Simulator.
 * Handles all Scanner input and console output separately from core logic.
 * 
 * @author All Group memebers
 */
public class ConsoleFlightApp {

    /**
     * Runs the main flight simulation loop and handles user interaction.
     */
    public void run() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("APCSA Flight Landing Simulator");
        String pilotName = "";
      while (pilotName.trim().isEmpty()) {
           System.out.print("Enter Pilot Name: ");
            pilotName = scanner.nextLine();
            if (pilotName.trim().isEmpty()) {
                System.out.println("Invalid input: Pilot name cannot be empty.");
            }       }

        // set up the starting plane, runway, and helper objects
        ScoreCalculator scorer = new ScoreCalculator(pilotName);
        Aircraft plane = new Aircraft(150.0, 600.0, -2.0);
        Runway runway = new Runway(2500.0, 2000.0, 15.0);
        FlightPhysics physics = new FlightPhysics(2.0);
        LandingEvaluator evaluator = new LandingEvaluator();

        System.out.println("\nWelcome, " + scorer.getPilotCallsign() + "!");
        System.out.println("Goal:Touch down between 0 and -2000 ft distance with the landing gear DoWN,");
        System.out.println("pitch ahould be between -5 and +15 deg to prevent crushing parts of airplane, vertical speed <= 15 ft/s, then BRAKE to 0 knots\n");

        // This entire while loop keeps looping until the plane safely lands or crashes
        while (!evaluator.isFlightOver()) {
            printDashboard(plane, runway, physics, evaluator);
            System.out.print("Command [U=Pitch Up, D=Pitch Down, +=Throttle Up, -=Throttle Down, G=Gear, B=Brake, H=Hold]: ");
            String input = scanner.nextLine().trim().toUpperCase();

            if (input.equals("U")) {
                plane.adjustPitch(3.0);
                System.out.println("Nose pitched UP (+3 deg).");
            } else if (input.equals("D")) {
                plane.adjustPitch(-3.0);
                System.out.println("Nose pitched DOWN (-3 deg).");
            } else if (input.equals("+")) {
                plane.adjustThrottle(15.0);
                System.out.println("Throttle increased (+15 kts).");
            } else if (input.equals("-")) {
                plane.adjustThrottle(-15.0);
                System.out.println("Throttle decreased (-15 kts).");
            } else if (input.equals("G")) {
                if (plane.deployGear()) {
                    System.out.println("Landing gear DEPLOYED.");
                } else {
                    System.out.println("Landing gear is already down.");
                }
            } else if (input.equals("B")) {
                if (plane.applyBrakes()) {
                    System.out.println("Wheel brakes APPLIED.");
                } else {
                    System.out.println("Warning: Brakes have no effect while airborne!");
                }
            } else if (input.equals("H")) {
                System.out.println("Holding current controls.");
            } else {
                // skip the physics step so a typo doesnt waste flight time
                System.out.println("Invalid command! Try again.");
                continue;
            }

            // move the plane forward one step and check if it landed or crashed
            physics.updateFlightStep(plane, runway);
            evaluator.evaluateState(plane, runway, physics);
        }

        // print final results once flight ends
        System.out.println("\n--- Flight Over ---");
        System.out.println(evaluator.getStatusMessage());
        int finalScore = scorer.calculateFinalScore(evaluator, runway);
        System.out.println("Pilot: " + scorer.getPilotCallsign() + " | Final Score: " + finalScore + " pts");

        scanner.close();
    }

    /**
     * Displays the current live data readout for the player.
     * @param plane   the Aircraft objdect
     * @param runway   the Runway object
     * @param physics   the FlightPhysics object
     * @param evaluator    the LandingEvaluator object
     */
    private void printDashboard(Aircraft plane, Runway runway, FlightPhysics physics, LandingEvaluator evaluator) {
        String gearStatus = "UP";
        if (plane.isGearDeployed()) {
            gearStatus = "DOWN";
        }
// on the concole, once user ones break, we have to make sure its on until he turns it off
        String brakeStatus = "OFF";
        if (plane.isBrakesApplied()) {
            brakeStatus = "ON";
        }

        System.out.println("----------------------------------------");
        // This print statement rounds up our valies up to one deciaml to prevent long tedious deicmals from showing on your console
        System.out.printf("Altitude: %.1f ft | Speed: %.1f kts | Pitch: %.1f deg%n",
             plane.getAltitude(), plane.getSpeed(), plane.getPitch());
        System.out.printf("Dist to Runway: %.1f ft | V-Speed: %.1f ft/s |Gear: %s | Brakes: %s%n",
                runway.getDistanceToThreshold(),
                physics.getVerticalSpeed(),
                gearStatus,
                brakeStatus);
        System.out.println("Status: " + evaluator.getStatusMessage());
    }}

