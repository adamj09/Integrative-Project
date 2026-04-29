package project.UI.Popups;

import java.util.Random;

import org.joml.Vector3f;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.AnimationTimer;
import project.Math.Body;
import project.Math.Utils;
import project.Math.Satellite;
import project.Math.SatelliteData;
import project.Renderer.Renderer;
import project.Renderer.World.World;
import project.UI.SidebarPane;
import project.SimulationPool;
import project.StyleSheet;

public class SatelliteCreatorPopup extends Stage {
    private Renderer previewRenderer = new Renderer();
    private StackPane preview = new StackPane();
    private Satellite previewSatellite;
    private Body previewBody;
    private World previewWorld;
    private World currentWorld;
    private String satelliteName;
    private SidebarPane sideBar;

    private TextField nameField;
    private TextField massField;
    private TextField initialSpeedField;
    private ComboBox<String> colorDropdown;
    private TextField posXField, posYField, posZField;
    private TextField rotIField, rotLField, rotWField;

    private Label errorLabel = new Label("");

    // --- Orbital-elements-mode fields ---
    private TextField nameOrbField;
    private TextField massOrbField;
    private ComboBox<String> colorOrbDropdown;
    private TextField altitudeField;
    private TextField eccentricityField;
    private TextField trueAnomalyField;
    private TextField lonAscNodeField;
    private TextField inclinationField;
    private TextField argPeriapsisField;
    private TextField massOfBodyField;

    private Label timeValueLabelNum;
    private AnimationTimer timeUpdater;

    // --- Randomizer state ---
    private final double bodyMass; // kg
    private final double bodyRadius; // km
    private static final double G = 6.674e-11;
    private final Random rand = new Random();

    // Lock index constants — Orbital Elements mode
    private static final int OL_MASS = 0, OL_DIST = 1, OL_ECC = 2, OL_NU = 3,
            OL_OMEGA = 4, OL_INC = 5, OL_ARG = 6;
    // Lock index constants — Cartesian mode
    private static final int CL_MASS = 0, CL_POSX = 1, CL_POSY = 2, CL_POSZ = 3,
            CL_SPEED = 4, CL_ROTI = 5, CL_ROTL = 6, CL_ROTW = 7;
    private final boolean[] orbLocks = new boolean[7];
    private final boolean[] cartLocks = new boolean[8];
    private final boolean[] cartColorLock = { false };
    private final boolean[] orbColorLock = { false };

    private boolean confirmed = false;
    private boolean orbitalElementsMode = true;

    public SatelliteCreatorPopup(Stage owner, SidebarPane sidebar, String themeStyle, SimulationPool pool) {
        this.sideBar = sidebar;
        this.currentWorld = pool.getCurrentWorld();
        this.bodyMass = currentWorld.getBody().getMass();
        this.bodyRadius = currentWorld.getBody().getRadius();

        initOwner(owner);
        initModality(Modality.APPLICATION_MODAL);
        setTitle("Create new satellite");
        setResizable(false);

        // Set preview background to black to prevent flickering on renderer update.
        preview.setStyle("-fx-background-color: #000000");

        // ================================================================
        // --- ORBITAL ELEMENTS MODE FORM ---
        // ================================================================

        // --- Left: name, mass, color ---
        nameOrbField = entryField("Satellite Name");
        massOrbField = entryField("e.g. 1000");
        colorOrbDropdown = new ComboBox<>();
        colorOrbDropdown.getItems().addAll("Blue", "Red", "Green", "Orange", "Purple", "White");
        colorOrbDropdown.setValue("Blue");
        colorOrbDropdown.getStyleClass().add("combo-box");

        Label orbNameLbl = formLabel("Name :");
        Label orbMassLbl = formLabel("Mass (kg) :");
        Label orbColorLbl = formLabel("Color :");
        Label orbBodyMassLbl = formLabel("Body mass (kg) :");
        massOfBodyField = entryField("kg");
        tip(orbNameLbl, nameOrbField, "A unique identifier for this satellite.");
        tip(orbMassLbl, massOrbField, "Mass of the satellite in kilograms.");
        tip(orbColorLbl, colorOrbDropdown, "Display colour for this satellite.");
        tip(orbBodyMassLbl, massOfBodyField,
                "Mass of the central body being orbited (kg).\nUsed to compute the gravitational parameter μ = G·M.");

        GridPane orbLeftForm = new GridPane();
        orbLeftForm.setHgap(10);
        orbLeftForm.setVgap(8);
        orbLeftForm.add(orbNameLbl, 0, 0);
        orbLeftForm.add(nameOrbField, 1, 0);
        orbLeftForm.add(orbMassLbl, 0, 1);
        orbLeftForm.add(fieldRow(massOrbField, orbLocks, OL_MASS, this::randomizeSatMass), 1, 1);
        orbLeftForm.add(orbColorLbl, 0, 2);
        orbLeftForm.add(colorRow(colorOrbDropdown, orbColorLock), 1, 2);
        orbLeftForm.add(orbBodyMassLbl, 0, 3);
        orbLeftForm.add(massOfBodyField, 1, 3);

        // --- Middle: orbital parameters ---
        altitudeField = entryField("km");
        eccentricityField = entryField("0 < e < 1");
        trueAnomalyField = entryField("0-360");

        Label orbAltLbl = formLabel("Alt (km):");
        Label orbEccLbl = formLabel("Ecc:");
        Label orbNuLbl = formLabel("\u03bd (deg):");
        tip(orbAltLbl, altitudeField,
                "Altitude from the satellite to the centre\nof the central body at the initial position (km).");
        tip(orbEccLbl, eccentricityField,
                "Eccentricity (e): shape of the orbit.\n  e = 0  → perfect circle\n  0 < e < 1 → ellipse\nMust be strictly between 0 and 1.");
        tip(orbNuLbl, trueAnomalyField,
                "True anomaly (\u03bd): angle between the\ndirection of periapsis and the satellite's\ncurrent position, measured from the focus (0–360°).");

        GridPane orbMidForm = new GridPane();
        orbMidForm.setHgap(10);
        orbMidForm.setVgap(8);
        ColumnConstraints orbMidLblCol = new ColumnConstraints(70);
        orbMidForm.getColumnConstraints().addAll(orbMidLblCol, new ColumnConstraints());
        orbMidForm.add(formLabel("Orbital Parameters:"), 0, 0, 2, 1);
        orbMidForm.add(orbAltLbl, 0, 1);
        orbMidForm.add(fieldRow(altitudeField, orbLocks, OL_DIST, this::randomizeDist), 1, 1);
        orbMidForm.add(orbEccLbl, 0, 2);
        orbMidForm.add(fieldRow(eccentricityField, orbLocks, OL_ECC, this::randomizeEcc), 1, 2);
        orbMidForm.add(orbNuLbl, 0, 3);
        orbMidForm.add(fieldRow(trueAnomalyField, orbLocks, OL_NU, () -> randomizeAngle360(trueAnomalyField)), 1, 3);

        // --- Right: 3D orientation ---
        lonAscNodeField = entryField("0-360");
        inclinationField = entryField("0-360");
        argPeriapsisField = entryField("0-360");

        Label orbOmegaLbl = formLabel("\u03a9:");
        Label orbIncLbl = formLabel("i:");
        Label orbOmegaSmLbl = formLabel("\u03c9:");
        tip(orbOmegaLbl, lonAscNodeField,
                "Longitude of the Ascending Node (\u03a9):\nAngle from the reference direction (x-axis)\nto where the orbit crosses the reference\nplane going north (0–360°).");
        tip(orbIncLbl, inclinationField,
                "Inclination (i): tilt of the orbital plane\nrelative to the reference plane (0–360°).\n  0° or 360° → equatorial prograde\n  90° → polar orbit\n  180° → equatorial retrograde");
        tip(orbOmegaSmLbl, argPeriapsisField,
                "Argument of Periapsis (\u03c9): angle from\nthe ascending node to the point of\nclosest approach (periapsis), measured\nin the orbital plane (0–360°).");

        GridPane orbRightForm = new GridPane();
        orbRightForm.setHgap(10);
        orbRightForm.setVgap(8);
        ColumnConstraints orbRightLblCol = new ColumnConstraints(30);
        orbRightForm.getColumnConstraints().addAll(orbRightLblCol, new ColumnConstraints());
        orbRightForm.add(formLabel("Orientation (deg):"), 0, 0, 2, 1);
        orbRightForm.add(orbOmegaLbl, 0, 1);
        orbRightForm.add(fieldRow(lonAscNodeField, orbLocks, OL_OMEGA, () -> randomizeAngle360(lonAscNodeField)), 1, 1);
        orbRightForm.add(orbIncLbl, 0, 2);
        orbRightForm.add(fieldRow(inclinationField, orbLocks, OL_INC, this::randomizeInc), 1, 2);
        orbRightForm.add(orbOmegaSmLbl, 0, 3);
        orbRightForm.add(fieldRow(argPeriapsisField, orbLocks, OL_ARG, () -> randomizeAngle360(argPeriapsisField)), 1,
                3);

        HBox orbitalFormRow = new HBox(20, orbLeftForm, orbMidForm, orbRightForm);
        orbitalFormRow.setPadding(new Insets(12, 14, 0, 14));
        orbitalFormRow.setVisible(true);
        orbitalFormRow.setManaged(true);

        // Create initial central body (deep copy of current simulation's central
        // celestial body).
        previewBody = new Body("previewBody", currentWorld.getBody().getMass(),
                currentWorld.getBody().getRadius(), currentWorld.getBody().getSemiMajorAxis(),
                currentWorld.getBody().getEccentricity());

        // Pre-fill body mass and auto-populate all fields on open
        massOfBodyField.setText(String.format("%.3e", this.bodyMass));
        randomizeAll();

        // Init preview
        setUpPreview();

        Button randAllBtn = new Button("\u27F3  Randomize All");
        randAllBtn.getStyleClass().add("style-button");
        randAllBtn.setOnAction(e -> {
            randomizeAll();

            updatePreviewColor();
            updatePreview();
        });
        BorderPane randAllPane = new BorderPane();

        HBox randAllRow = new HBox(randAllBtn);
        randAllRow.setAlignment(Pos.CENTER_RIGHT);
        randAllRow.setPadding(new Insets(12, 14, 0, 14));
        randAllPane.setRight(randAllRow);
        
        Label timescaleLabel = formLabel("Time scale of the preview:");
        timescaleLabel.getStyleClass().add("body");

        ComboBox<String> timescaleDropdown = new ComboBox<>();
        timescaleDropdown.getItems().addAll(
                "1x", "2x", "5x", "10x",
                "100x", "1,000x", "10,000x", "100,000x");
        timescaleDropdown.setValue("1x");
        timescaleDropdown.getStyleClass().add("combo-box");
        //timescaleDropdown.setPrefWidth(90);

        timescaleDropdown.setOnAction(_ -> {

            switch (timescaleDropdown.getValue()) {
                case "1x":
                    previewWorld.getBody().setTimeScale(1);
                    break;
                case "2x":
                    previewWorld.getBody().setTimeScale(2);
                    System.out.println("Setting time scale to 2x");
                    break;
                case "5x":
                    previewWorld.getBody().setTimeScale(5);
                    break;
                case "10x":
                    previewWorld.getBody().setTimeScale(10);
                    break;
                case "100x":
                    previewWorld.getBody().setTimeScale(100);
                    break;
                case "1,000x":
                    previewWorld.getBody().setTimeScale(1000);
                    break;
                case "10,000x":
                    previewWorld.getBody().setTimeScale(10_000);
                    break;
                case "100,000x":
                    previewWorld.getBody().setTimeScale(100_000);
                    break;
                default:
                    previewWorld.getBody().setTimeScale(1);
                    break;
            }
        });

        timeValueLabelNum = new Label();
        timeValueLabelNum.setText(Utils.getWorldTimeFormated(0));
        timeValueLabelNum.getStyleClass().add("body");

        timeUpdater = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double timeSeconds = previewWorld.getBody().getTimeSeconds();
                timeValueLabelNum.setText(Utils.getWorldTimeFormated(timeSeconds));
            }
        };
        timeUpdater.start();

        HBox timescaleBox = new HBox(10, timescaleLabel, timescaleDropdown,timeValueLabelNum);
        timescaleBox.setPadding(new Insets(12, 14, 0, 14));
        timescaleBox.setAlignment(Pos.CENTER_LEFT);
        randAllPane.setLeft(timescaleBox);



        errorLabel.getStyleClass().add("error-label");

        Button cancelBtn = new Button("CANCEL");
        Button createBtn = new Button("CREATE");
        cancelBtn.getStyleClass().add("style-button");
        createBtn.getStyleClass().add("style-button");

        cancelBtn.setOnAction(e -> {
            previewWorld.stopWorld();
            previewRenderer.getViewport().dispose();
            timeUpdater.stop();

            close();
        });

        createBtn.setOnAction(e -> {
            if (!validate())
                return;
            if (orbitalElementsMode) {
                // Run the math to check physical validity and surface any error
                satelliteName = nameOrbField.getText().trim();

                if(satelliteName.isEmpty()) {
                    errorLabel.setText("You must provide a name for the satellite!");
                    return;
                }

                if(sideBar.getSatelliteEntries(sidebar.getSelectedBody()) != null) {
                    if(sideBar.getSatelliteEntries(sidebar.getSelectedBody()).containsKey(satelliteName)) {
                        if(!UnsavedChangesPopup.confirm("A body with this name is already exists! Continue and overwrite that body's data with this one?")) {
                            return;
                        }
                        sideBar.removeSatellite(satelliteName);
                    }
                }
            
                Satellite tempSat = new Satellite();
                boolean success = tempSat.initialiseSatelliteValuesAngles(currentWorld.getBody(), satelliteName,
                        getSatelliteMass(),
                        p(altitudeField), getEccentricity(), getTrueAnomaly(),
                        getLongitudeAscendingNode(), getInclination(), getArgumentOfPeriapsis());
                if (!success) {
                    errorLabel.setText(tempSat.getLatestError());
                    return;
                }

                boolean wasWorldRunning = currentWorld.isWorldRunning();

                currentWorld.stopWorld();

                Satellite newSatellite = new Satellite();

                newSatellite.initialiseSatelliteValuesAngles(currentWorld.getBody(), satelliteName,
                        getSatelliteMass(),
                        p(altitudeField), getEccentricity(), getTrueAnomaly(),
                        getLongitudeAscendingNode(), getInclination(), getArgumentOfPeriapsis());

                Color color = getSatelliteColor();

                currentWorld.addSatellite(newSatellite,
                        new Vector3f((float) color.getRed(), (float) color.getGreen(), (float) color.getBlue()));

                currentWorld.runWorld();
                if(wasWorldRunning){
                    currentWorld.startWorld();
                }
            }
            confirmed = true;

            previewWorld.stopWorld();
            timeUpdater.stop();
            previewRenderer.getViewport().dispose();

            close();
        });

        HBox buttons = new HBox(8, cancelBtn, createBtn);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.setPadding(new Insets(8, 14, 14, 14));

        Label titleLabel = new Label("Create new satellite");
        titleLabel.getStyleClass().add("subheading");

        VBox root = new VBox(titleLabel, preview, orbitalFormRow, randAllPane, errorLabel,
                buttons);
        root.getStyleClass().add("small-pane");
        root.setStyle(themeStyle);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(new StyleSheet().styleSheet);

        setOnCloseRequest(_ -> {
            previewWorld.stopWorld();
            timeUpdater.stop();
            previewRenderer.getViewport().dispose();
        });

        setScene(scene);
    }

    private boolean validate() {
        if (orbitalElementsMode) {
            try {
                Double.parseDouble(massOrbField.getText());
            } catch (NumberFormatException e) {
                errorLabel.setText("Mass must be a number.");
                return false;
            }
            try {
                double parsedBodyMass = Double.parseDouble(massOfBodyField.getText());
                if (parsedBodyMass <= 0) {
                    errorLabel.setText("Body mass must be positive.");
                    return false;
                }
            } catch (NumberFormatException e) {
                errorLabel.setText("Body mass must be a number.");
                return false;
            }
            double dist;
            try {
                dist = Double.parseDouble(altitudeField.getText());
            } catch (NumberFormatException e) {
                errorLabel.setText("altitude must be a number.");
                return false;
            }
            if (dist <= 0) {
                errorLabel.setText("altitude must be positive.");
                return false;
            }
            double ecc;
            try {
                ecc = Double.parseDouble(eccentricityField.getText());
            } catch (NumberFormatException e) {
                errorLabel.setText("Eccentricity must be a number.");
                return false;
            }
            if (ecc <= 0 || ecc >= 1) {
                errorLabel.setText("Eccentricity must be between 0 and 1 (exclusive).");
                return false;
            }
            String[] angleTexts = {
                    trueAnomalyField.getText(), lonAscNodeField.getText(),
                    inclinationField.getText(), argPeriapsisField.getText()
            };
            String[] angleNames = {
                    "True anomaly", "Longitude of ascending node",
                    "Inclination", "Argument of periapsis"
            };
            for (int i = 0; i < angleTexts.length; i++) {
                try {
                    Double.parseDouble(angleTexts[i]);
                } catch (NumberFormatException e) {
                    errorLabel.setText(angleNames[i] + " must be a number.");
                    return false;
                }
            }
            errorLabel.setText("");

            Satellite test = new Satellite();
            if (!test.initialiseSatelliteValuesAngles(previewBody, "previewSatellite",
                    getSatelliteMass(),
                    dist, getEccentricity(), getTrueAnomaly(),
                    getLongitudeAscendingNode(), getInclination(), getArgumentOfPeriapsis())) {
                errorLabel.setText(test.getLatestError());
                return false;
            }

            return true;
        } else {
            try {
                Double.parseDouble(massField.getText());
            } catch (NumberFormatException e) {
                errorLabel.setText("Mass must be a number.");
                return false;
            }
            try {
                Double.parseDouble(initialSpeedField.getText());
            } catch (NumberFormatException e) {
                errorLabel.setText("Initial speed must be a number.");
                return false;
            }
            errorLabel.setText("");
            return true;
        }
    }

    private void setUpPreview() {
        // Set time scale to be same as current simulation.
        previewBody.setTimeScale(1);

        // Create preview world for rendering.
        previewWorld = new World(previewBody, currentWorld.getColour());

        // Add renderer viewport to the pop-up.
        preview.getChildren().add(previewRenderer.getViewport().getGLCanvas());
        previewRenderer.getViewport().getGLCanvas().setPrefSize(400, 400);

        // Initialize the preview satellite.
        previewSatellite = new Satellite();
        if (!previewSatellite.initialiseSatelliteValuesAngles(previewBody, "previewSatellite", getSatelliteMass(),
                getAltitude(), getEccentricity(), getTrueAnomaly(),
                getLongitudeAscendingNode(), getInclination(), getArgumentOfPeriapsis())) {
            System.err.println("Failed to initialize preview satellite: " + previewSatellite.getLatestError());
            return;
        }

        // Add the preview satellite to the world.
        Color color = getSatelliteColor();
        previewWorld.addSatellite(previewSatellite,
                new Vector3f((float) color.getRed(), (float) color.getGreen(), (float) color.getBlue()));

        // Run the simulation.
        previewWorld.runWorld();

        // Set renderer's world to the preview world.
        previewRenderer.setWorld(previewWorld);

        // Set the preview satellite as the camera's focus.
        previewRenderer.setFocusObject(previewSatellite.getData().name);

        // Add update listeners for text fields.
        altitudeField.textProperty().addListener(_ -> updatePreview());
        eccentricityField.textProperty().addListener(_ -> updatePreview());
        trueAnomalyField.textProperty().addListener(_ -> updatePreview());

        lonAscNodeField.textProperty().addListener(_ -> updatePreview());
        inclinationField.textProperty().addListener(_ -> updatePreview());
        argPeriapsisField.textProperty().addListener(_ -> updatePreview());

        // Update preview colour when changed
        colorOrbDropdown.setOnAction(e -> updatePreviewColor());
    }

    private void updatePreview() {
        SatelliteData data = previewSatellite.getData();

        // Check if values are valid for rendering BEFORE updating test satellite.
        Satellite testSatellite = new Satellite();
        if (!testSatellite.initialiseSatelliteValuesAngles(previewBody, "testSatellite", getSatelliteMass(),
                getAltitude(), getEccentricity(), getTrueAnomaly(),
                getLongitudeAscendingNode(), getInclination(), getArgumentOfPeriapsis())) {
            System.err.println("Failed to update preview satellite: " + testSatellite.getLatestError());
            return;
        }

        // Update the satellite's orbital elements.
        previewWorld.updateOrbitalElements(data.name, getSatelliteMass(),
                getAltitude(), getEccentricity(), getTrueAnomaly(),
                getLongitudeAscendingNode(), getInclination(), getArgumentOfPeriapsis());
    }

    private void updatePreviewColor() {
        Color color = getSatelliteColor();

        // Update the satellite's colour.
        previewWorld.updateColor(previewSatellite.getData().name,
                new Vector3f((float) color.getRed(), (float) color.getGreen(), (float) color.getBlue()));
    }

    public boolean wasConfirmed() {
        return confirmed;
    }

    public boolean isOrbitalElementsMode() {
        return orbitalElementsMode;
    }

    public String getSatelliteName() {
        return satelliteName;
    }

    public double getSatelliteMass() {
        try {
            String text = orbitalElementsMode
                    ? massOrbField.getText().trim()
                    : massField.getText().trim();
            return text.isEmpty() ? 0.0 : Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public double getInitialSpeed() {
        try {
            String text = initialSpeedField.getText().trim();
            return text.isEmpty() ? 0.0 : Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public Color getSatelliteColor() {
        String val = orbitalElementsMode ? colorOrbDropdown.getValue() : colorDropdown.getValue();
        return switch (val) {
            case "Red" -> Color.web("#7a4a36");
            case "Green" -> Color.web("#355c3a");
            case "Orange" -> Color.web("#7a5a2e");
            case "Purple" -> Color.web("#4b4063");
            case "White" -> Color.web("#b0b0b0");
            default -> Color.web("#39506a");
        };
    }

    public double[] getPosition() {
        return new double[] { p(posXField), p(posYField), p(posZField) };
    }

    public double[] getRotation() {
        return new double[] { p(rotIField), p(rotLField), p(rotWField) };
    }

    public double getAltitude() {
        double altitude = p(altitudeField);

        Satellite testSatellite = new Satellite();

        if (!testSatellite.initialiseSatelliteValuesAngles(previewBody, "testSatellite", getSatelliteMass(),
                altitude, getEccentricity(), getTrueAnomaly(),
                getLongitudeAscendingNode(), getInclination(), getArgumentOfPeriapsis())) {
            return previewSatellite.getData().altitude; // Return last altitude if current one doesn't work.
        }

        errorLabel.setText("");

        return p(altitudeField);
    }

    public double getEccentricity() {
        return p(eccentricityField);
    }

    public double getTrueAnomaly() {
        return p(trueAnomalyField);
    }

    public double getLongitudeAscendingNode() {
        return p(lonAscNodeField);
    }

    public double getInclination() {
        return p(inclinationField);
    }

    public double getArgumentOfPeriapsis() {
        return p(argPeriapsisField);
    }

    public double getMassOfBody() {
        return p(massOfBodyField);
    }

    /** Randomizes all unlocked fields for the currently active mode. */
    private void randomizeAll() {
        if (orbitalElementsMode) {
            if (!orbLocks[OL_MASS])
                randomizeSatMass();
            if (!orbLocks[OL_DIST])
                randomizeDist();
            if (!orbLocks[OL_ECC])
                randomizeEcc();
            if (!orbLocks[OL_NU])
                randomizeAngle360(trueAnomalyField);
            if (!orbLocks[OL_OMEGA])
                randomizeAngle360(lonAscNodeField);
            if (!orbLocks[OL_INC])
                randomizeInc();
            if (!orbLocks[OL_ARG])
                randomizeAngle360(argPeriapsisField);
            if (!orbColorLock[0])
                randomizeColor(colorOrbDropdown);

            Satellite testSatellite = new Satellite();
            if (!testSatellite.initialiseSatelliteValuesAngles(previewBody, "testSatellite", getSatelliteMass(),
                p(altitudeField), getEccentricity(), getTrueAnomaly(),
                getLongitudeAscendingNode(), getInclination(), getArgumentOfPeriapsis())) {
                
                randomizeAll();
            }
        } else {
            if (!cartLocks[CL_MASS])
                randomizeCartMass();
            if (!cartLocks[CL_POSX])
                randomizeCartPosX(); // X first — speed depends on it
            if (!cartLocks[CL_POSY])
                posYField.setText("0");
            if (!cartLocks[CL_POSZ])
                posZField.setText("0");
            if (!cartLocks[CL_SPEED])
                randomizeCartSpeed();
            if (!cartLocks[CL_ROTI])
                randomizeAngle360(rotIField);
            if (!cartLocks[CL_ROTL])
                randomizeAngle360(rotLField);
            if (!cartLocks[CL_ROTW])
                randomizeAngle360(rotWField);
            if (!cartColorLock[0])
                randomizeColor(colorDropdown);
        }
    }

    private void randomizeCartMass() {
        massField.setText(String.format("%.3e", randomLog(1, 1e6)));
    }

    private void randomizeSatMass() {
        massOrbField.setText(String.format("%.3e", randomLog(1, 1e6)));
    }

    private void randomizeDist() {
        // Log-uniform between 1.5× and 100× the body's radius (km)
        altitudeField.setText(String.format("%.1f",
                randomLog(bodyRadius * 1.5, bodyRadius * 100)));
    }

    private void randomizeEcc() {
        eccentricityField.setText(String.format("%.3f", 0.01 + rand.nextDouble() * 0.69));
    }

    private void randomizeInc() {
        // Physical inclinations are 0–180° (retrograde = > 90°)
        inclinationField.setText(String.format("%.1f", rand.nextDouble() * 180.0));
    }

    private void randomizeAngle360(TextField f) {
        f.setText(String.format("%.1f", rand.nextDouble() * 360.0));
    }

    private void randomizeCartPosX() {
        // Place satellite at a body-dependent orbital altitude (metres)
        double distM = randomLog(bodyRadius * 1.5, bodyRadius * 100.0) * 1000.0;
        posXField.setText(String.format("%.4e", distM));
    }

    private void randomizeCartSpeed() {
        // Circular orbit speed at the current posX altitude
        try {
            double x = Double.parseDouble(posXField.getText());
            if (x > 0) {
                initialSpeedField.setText(String.format("%.2f", Math.sqrt(G * bodyMass / x)));
                return;
            }
        } catch (NumberFormatException ignored) {
        }
        double fallbackDist = bodyRadius * 3.0 * 1000.0;
        initialSpeedField.setText(String.format("%.2f", Math.sqrt(G * bodyMass / fallbackDist)));
    }

    private double randomLog(double min, double max) {
        if (min <= 0)
            min = 1e-10;
        double logMin = Math.log10(min);
        double logMax = Math.log10(max);
        return Math.pow(10, logMin + rand.nextDouble() * (logMax - logMin));
    }

    private void randomizeColor(ComboBox<String> dropdown) {
        var items = dropdown.getItems();
        dropdown.setValue(items.get(rand.nextInt(items.size())));
    }

    private HBox colorRow(ComboBox<String> dropdown, boolean[] lock) {
        Button randBtn = randButton();
        ToggleButton lockBtn = lockButton();
        randBtn.setOnAction(e -> {
            if (!lock[0]) {
                randomizeColor(dropdown);

                updatePreviewColor();
            }
        });
        lockBtn.setOnAction(e -> {
            lock[0] = lockBtn.isSelected();
            lockBtn.setText(lock[0] ? "\uD83D\uDD12" : "\uD83D\uDD13");
        });
        HBox row = new HBox(5, dropdown, randBtn, lockBtn);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private HBox fieldRow(TextField field, boolean[] locks, int idx, Runnable randomizer) {
        Button randBtn = randButton();
        ToggleButton lockBtn = lockButton();
        randBtn.setOnAction(e -> {
            if (!locks[idx])
                randomizer.run();

            Satellite testSatellite = new Satellite();

            while (!testSatellite.initialiseSatelliteValuesAngles(previewBody, "testSatellite", getSatelliteMass(),
                    p(altitudeField), getEccentricity(), getTrueAnomaly(),
                    getLongitudeAscendingNode(), getInclination(), getArgumentOfPeriapsis())) {
                randomizer.run();
            }
        });
        lockBtn.setOnAction(e -> {
            locks[idx] = lockBtn.isSelected();
            lockBtn.setText(locks[idx] ? "\uD83D\uDD12" : "\uD83D\uDD13");
        });
        HBox row = new HBox(5, field, randBtn, lockBtn);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private Button randButton() {
        Button btn = new Button("\u27F3");
        btn.getStyleClass().add("icon-button");
        btn.setMinWidth(28);
        btn.setPrefWidth(28);
        return btn;
    }

    private ToggleButton lockButton() {
        ToggleButton btn = new ToggleButton("\uD83D\uDD13"); // \uD83D\uDD13
        btn.getStyleClass().add("lock-button");
        btn.setMinWidth(28);
        btn.setPrefWidth(28);
        return btn;
    }

    /** Attach the same tooltip text to a label and its associated control. */
    private void tip(javafx.scene.Node label, javafx.scene.Node control, String text) {
        Tooltip tt = new Tooltip(text);
        tt.setShowDelay(Duration.millis(300));
        tt.setWrapText(true);
        tt.setMaxWidth(280);
        Tooltip.install(label, tt);
        Tooltip.install(control, tt);
    }

    private double p(TextField tf) {
        try {
            return Double.parseDouble(tf.getText());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private TextField entryField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setPrefWidth(100);
        tf.getStyleClass().add("field");
        ;
        return tf;
    }

    private Label formLabel(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("key");
        ;
        return l;
    }
}
