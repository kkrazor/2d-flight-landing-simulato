import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

/**
 * Java Swing Graphical User Interface for the Flight Landing Simulator.
 * Features a stationary long runway, moving airplane silhouette, and restart button.
 */
public class FlightSimGUI extends JFrame {
    private Aircraft plane;
    private Runway runway;
    private FlightPhysics physics;
    private LandingEvaluator evaluator;
    private ScoreCalculator scorer;

    private FlightCanvas canvas;
    private Timer gameTimer;
    private JLabel statusLabel;
    private JLabel telemetryLabel;
    private JButton restartBtn;

    public FlightSimGUI() {
        // Ask for pilot name with input validation
        String pilotName = "";
        while (pilotName == null || pilotName.trim().isEmpty()) {
            pilotName = JOptionPane.showInputDialog(
                null,
                "Enter Pilot Name:",
                "APCSA Flight Landing Simulator",
                JOptionPane.QUESTION_MESSAGE
            );
            if (pilotName == null) {
                System.exit(0);
            }
            if (pilotName.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Pilot name cannot be empty!", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        scorer = new ScoreCalculator(pilotName);
        initSimulation();

        setTitle("APCSA Flight Landing Simulator - " + scorer.getPilotCallsign());
        setSize(1100, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Top HUD bar
        JPanel topHud = new JPanel(new GridLayout(2, 1));
        topHud.setBackground(new Color(25, 35, 45));
        topHud.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        telemetryLabel = new JLabel("", SwingConstants.CENTER);
        telemetryLabel.setForeground(Color.WHITE);
        telemetryLabel.setFont(new Font("Monospaced", Font.BOLD, 15));

        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setForeground(new Color(120, 230, 140));
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        topHud.add(telemetryLabel);
        topHud.add(statusLabel);
        add(topHud, BorderLayout.NORTH);

        // Center 2D flight canvas
        canvas = new FlightCanvas();
        add(canvas, BorderLayout.CENTER);

        // Bottom control bar
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        controlsPanel.setBackground(new Color(35, 45, 55));

        JButton pitchUpBtn = createControlButton("Pitch Up (Up)", () -> plane.adjustPitch(2.5));
        JButton pitchDownBtn = createControlButton("Pitch Down (Down)", () -> plane.adjustPitch(-2.5));
        JButton throttleDownBtn = createControlButton("Throttle - (Left)", () -> plane.adjustThrottle(-10.0));
        JButton throttleUpBtn = createControlButton("Throttle + (Right)", () -> plane.adjustThrottle(10.0));
        JButton gearBtn = createControlButton("Gear (Space)", () -> plane.deployGear());
        JButton brakeBtn = createControlButton("Brakes (B)", () -> plane.applyBrakes());

        // Dedicated Restart Button
        restartBtn = new JButton("Restart Game");
        restartBtn.setFocusable(false);
        restartBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        restartBtn.addActionListener(e -> resetGame());

        controlsPanel.add(pitchUpBtn);
        controlsPanel.add(pitchDownBtn);
        controlsPanel.add(throttleDownBtn);
        controlsPanel.add(throttleUpBtn);
        controlsPanel.add(gearBtn);
        controlsPanel.add(brakeBtn);
        controlsPanel.add(restartBtn);
        add(controlsPanel, BorderLayout.SOUTH);

        // Keyboard controls
        setupKeyBindings();

        // Game loop timer
        gameTimer = new Timer(120, e -> {
            if (!evaluator.isFlightOver()) {
                physics.updateFlightStep(plane, runway);
                evaluator.evaluateState(plane, runway, physics);
                updateHud();
                canvas.repaint();
            } else {
                gameTimer.stop();
                int score = scorer.calculateFinalScore(evaluator, runway);
                statusLabel.setForeground(evaluator.hasCrashed() ? new Color(255, 100, 100) : new Color(100, 255, 150));
                statusLabel.setText(evaluator.getStatusMessage() + " | Final Score: " + score + " pts (Click 'Restart Game' to play again)");
                restartBtn.setBackground(new Color(255, 215, 0));
                restartBtn.setForeground(Color.BLACK);
                canvas.repaint();
            }
        });

        updateHud();
        gameTimer.start();
        setVisible(true);
    }

    private void initSimulation() {
        plane = new Aircraft(150.0, 600.0, -2.0);
        runway = new Runway(2500.0, 2000.0, 15.0);
        physics = new FlightPhysics(0.25);
        evaluator = new LandingEvaluator();
    }

    private void resetGame() {
        gameTimer.stop();
        initSimulation();
        restartBtn.setBackground(null);
        restartBtn.setForeground(Color.BLACK);
        statusLabel.setForeground(new Color(120, 230, 140));
        updateHud();
        canvas.repaint();
        gameTimer.start();
    }

    private JButton createControlButton(String text, Runnable action) {
        JButton btn = new JButton(text);
        btn.setFocusable(false);
        btn.addActionListener(e -> {
            if (!evaluator.isFlightOver()) {
                action.run();
                updateHud();
                canvas.repaint();
            }
        });
        return btn;
    }

    private void setupKeyBindings() {
        JRootPane root = getRootPane();
        InputMap im = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = root.getActionMap();

        bindKey(im, am, "UP", () -> plane.adjustPitch(2.5));
        bindKey(im, am, "DOWN", () -> plane.adjustPitch(-2.5));
        bindKey(im, am, "RIGHT", () -> plane.adjustThrottle(10.0));
        bindKey(im, am, "LEFT", () -> plane.adjustThrottle(-10.0));
        bindKey(im, am, "SPACE", () -> plane.deployGear());
        bindKey(im, am, "B", () -> plane.applyBrakes());
    }

    private void bindKey(InputMap im, ActionMap am, String key, Runnable action) {
        im.put(KeyStroke.getKeyStroke(key), key);
        am.put(key, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!evaluator.isFlightOver()) {
                    action.run();
                    updateHud();
                    canvas.repaint();
                }
            }
        });
    }

    private void updateHud() {
        telemetryLabel.setText(String.format(
            "ALT: %.1f ft  |  SPD: %.1f kts  |  PITCH: %.1f°  |  DIST: %.0f ft  |  V-SPD: %.1f ft/s  |  GEAR: %s  |  BRAKES: %s",
            plane.getAltitude(),
            plane.getSpeed(),
            plane.getPitch(),
            runway.getDistanceToThreshold(),
            physics.getVerticalSpeed(),
            plane.isGearDeployed() ? "DOWN" : "UP",
            plane.isBrakesApplied() ? "ON" : "OFF"
        ));
        statusLabel.setText(evaluator.getStatusMessage());
    }

    /**
     * Custom 2D drawing panel with a fixed runway and a moving airplane.
     */
    private class FlightCanvas extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int groundY = h - 85;

            // 1. Sky gradient
            GradientPaint sky = new GradientPaint(0, 0, new Color(70, 145, 225), 0, groundY, new Color(185, 225, 255));
            g2.setPaint(sky);
            g2.fillRect(0, 0, w, groundY);

            // 2. Stationary clouds
            g2.setColor(new Color(255, 255, 255, 190));
            drawCloud(g2, 150, 75);
            drawCloud(g2, 500, 105);
            drawCloud(g2, 850, 65);

            // 3. Grass / terrain ground
            g2.setColor(new Color(55, 130, 65));
            g2.fillRect(0, groundY, w, h - groundY);

            // 4. Fixed Runway on the right portion of the screen
            // Total horizontal world span: 2500 ft approach + 2000 ft runway = 4500 ft
            int leftMargin = 70;
            int rightMargin = 50;
            int usableWidth = w - leftMargin - rightMargin;
            double totalWorldFeet = 4500.0; // 2500 ft approach + 2000 ft runway
            double pixelsPerFoot = usableWidth / totalWorldFeet;

            // Runway starts at 2500 ft from the starting point and spans 2000 ft
            int runwayStartX = leftMargin + (int) (2500.0 * pixelsPerFoot);
            int runwayPixelLength = (int) (runway.getRunwayLength() * pixelsPerFoot);

            // Draw fixed asphalt runway
            g2.setColor(new Color(45, 48, 52));
            g2.fillRect(runwayStartX, groundY - 4, runwayPixelLength, 24);

            // Threshold stripes (start of runway)
            g2.setColor(Color.WHITE);
            for (int i = 0; i < 4; i++) {
                g2.fillRect(runwayStartX + 6, groundY + (i * 4), 16, 2);
            }

            // Center dashed runway lines
            for (int x = runwayStartX + 35; x < runwayStartX + runwayPixelLength - 25; x += 30) {
                g2.fillRect(x, groundY + 7, 15, 3);
            }

            // Red end-of-runway warning lights
            g2.setColor(Color.RED);
            g2.fillOval(runwayStartX + runwayPixelLength - 8, groundY - 6, 8, 8);

            // Runway labels
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 11));
            g2.drawString("RUNWAY START", runwayStartX, groundY + 36);
g2.drawString("RUNWAY END", runwayStartX + runwayPixelLength - 75, groundY + 36);
// 5. Calculate Moving Airplane X & Y positions
// distanceToThreshold goes from +2500 ft (start) -> 0 ft (runway start) -> -2000 ft (runway end)
double feetTraveledFromStart = 2500.0 - runway.getDistanceToThreshold();
int planeScreenX = leftMargin + (int) (feetTraveledFromStart * pixelsPerFoot);
double maxDisplayAlt = 650.0;
int planeScreenY = groundY - 14 - (int) ((plane.getAltitude() / maxDisplayAlt) * (groundY - 70));
if (planeScreenY > groundY - 14) {
planeScreenY = groundY - 14;
}
// 6. Draw the realistic airplane silhouette
drawAirplane(g2, planeScreenX, planeScreenY, plane.getPitch(), plane.isGearDeployed(), evaluator.hasCrashed());
}
private void drawCloud(Graphics2D g2, int x, int y) {
g2.fillOval(x, y, 60, 28);
g2.fillOval(x + 20, y - 12, 55, 35);
g2.fillOval(x + 48, y, 50, 26);
}
private void drawAirplane(Graphics2D g2, int x, int y, double pitchDeg, boolean gearDown, boolean crashed) {
AffineTransform oldTransform = g2.getTransform();
g2.translate(x, y);
g2.rotate(Math.toRadians(-pitchDeg));
// Landing gear
if (gearDown) {
g2.setColor(Color.DARK_GRAY);
g2.setStroke(new BasicStroke(2.5f));
g2.drawLine(28, 5, 31, 14);
g2.setColor(Color.BLACK);
g2.fillOval(28, 11, 7, 7);
g2.setColor(Color.DARK_GRAY);
g2.drawLine(-8, 5, -10, 14);
g2.setColor(Color.BLACK);
g2.fillOval(-14, 11, 8, 8);
}
// Fuselage
g2.setColor(crashed ? new Color(220, 60, 60) : Color.WHITE);
RoundRectangle2D fuselage = new RoundRectangle2D.Double(-40, -7, 76, 14, 12, 12);
g2.fill(fuselage);
g2.setColor(new Color(40, 50, 65));
g2.setStroke(new BasicStroke(1.5f));
g2.draw(fuselage);
// Nose cone
Polygon nose = new Polygon();
nose.addPoint(33, -6);
nose.addPoint(47, 1);
nose.addPoint(33, 6);
g2.setColor(crashed ? new Color(220, 60, 60) : Color.WHITE);
g2.fillPolygon(nose);
g2.setColor(new Color(40, 50, 65));
g2.drawPolygon(nose);
// Tail fin
Polygon tailFin = new Polygon();
tailFin.addPoint(-37, -5);
tailFin.addPoint(-45, -22);
tailFin.addPoint(-32, -22);
tailFin.addPoint(-22, -5);
g2.setColor(new Color(20, 90, 180));
g2.fillPolygon(tailFin);
// Airline stripe
g2.setColor(new Color(20, 90, 180));
g2.fillRect(-35, -1, 68, 2);
// Cockpit windshield
Polygon cockpit = new Polygon();
cockpit.addPoint(31, -5);
cockpit.addPoint(39, -1);
cockpit.addPoint(31, -1);
g2.setColor(new Color(30, 40, 50));
g2.fillPolygon(cockpit);
// Passenger windows
g2.setColor(new Color(40, 60, 80));
for (int wx = -22; wx <= 22; wx += 7) {
g2.fillRect(wx, -4, 3, 2);
}
// Swept wing
Polygon wing = new Polygon();
wing.addPoint(5, 2);
wing.addPoint(-15, 10);
wing.addPoint(-22, 10);
wing.addPoint(-7, 2);
g2.setColor(new Color(190, 200, 210));
g2.fillPolygon(wing);
g2.setColor(new Color(70, 80, 90));
g2.drawPolygon(wing);
// Engine pod
g2.setColor(new Color(120, 130, 140));
g2.fillRoundRect(-5, 5, 14, 6, 4, 4);
// Explosion visual on crash
if (crashed) {
g2.setColor(new Color(255, 140, 0, 210));
g2.fillOval(-22, -18, 48, 36);
g2.setColor(Color.YELLOW);
g2.fillOval(-10, -9, 26, 18);
}
g2.setTransform(oldTransform);
}
}
}

