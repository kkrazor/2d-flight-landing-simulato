# 2D Flight Landing Simulator

A 2D airplane landing simulation game built in Java. This project features a custom game-physics engine and a modular object-oriented architecture that powers both a text-based console prototype and an interactive 2D graphical interface.

## Project Architecture

### Core Engine (5 Classes)
* **`Aircraft.java`** – Tracks aircraft telemetry, position, velocity, and flight state.
* **`FlightPhysics.java`** – Custom 2D game-physics engine modeling gravity, thrust, lift, and movement kinematics.
* **`Runway.java`** – Defines runway dimensions, touchdown zones, and terrain boundaries.
* **`LandingEvaluator.java`** – Evaluates touchdown conditions (vertical speed, angle, and position) to determine safe landings versus crashes.
* **`ScoreCalculator.java`** – Calculates performance scores based on landing precision, smoothness, and efficiency.

### User Interfaces & Launchers (4 Files)
* **Console Version:** `ConsoleFlightApp.java` (text-based game loop) & `FlightSimMain.java` (console entry point)
* **GUI Version:** `FlightSimGUI.java` (2D graphical rendering and controls) & `FlightSimMainGUI.java` (GUI entry point)

## Development Process
1. **Core Logic & Custom Physics:** Designed the 5 core object-oriented classes and wrote custom physics equations from scratch to create balanced, playable flight mechanics.
2. **Console Prototype:** Built `ConsoleFlightApp` to test flight math, user inputs, and landing evaluation in the terminal.
3. **2D GUI Implementation:** Expanded the simulator into a visual 2D experience (`FlightSimGUI`) running on the exact same backend engine, utilizing AI tools to assist with GUI rendering setup.

## How to Run
1. Download all `.java` files into the same directory.
2. Compile the source code:
   ```bash
   javac *.java
   ```
3. Run either version:
   * **GUI Version:** `java FlightSimMainGUI`
   * **Console Version:** `java FlightSimMain`

