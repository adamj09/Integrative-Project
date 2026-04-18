package project.UI.Popups;

import java.util.Map;
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
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import project.Math.Body;
import project.Math.Constant;
import project.Math.Satellite;
import project.Math.SatelliteData;
import project.Renderer.Renderer;
import project.Renderer.World.World;
import project.StyleSheet;

public class SatelliteCreatorPopup extends Stage {
    private Renderer previewRenderer = new Renderer();;
    private StackPane preview = new StackPane();
    private Satellite previewSatellite;
    private Body previewBody;
    private World previewWorld;
    private World currentWorld;

    private TextField nameField;
    private TextField massField;
    private TextField initialSpeedField;
    private ComboBox<String> colorDropdown;
    private TextField posXField, posYField, posZField;
    private TextField rotIField, rotLField, rotWField;

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
    private static int satCounter = 0;
    private final boolean[] cartColorLock = { false };
    private final boolean[] orbColorLock = { false };

    private boolean confirmed = false;
    private boolean orbitalElementsMode = true;

    public SatelliteCreatorPopup(Stage owner, String themeStyle, World currentWorld) {
        this.currentWorld = currentWorld;
        this.bodyMass = currentWorld.getBody().getMass();
        this.bodyRadius = currentWorld.getBody().getRadius();

        initOwner(owner);
        initModality(Modality.APPLICATION_MODAL);
        setTitle("Create new satellite");
        setResizable(false);

        // Set preview background to black to prevent flickering on renderer update.
        preview.setStyle("-fx-background-color: #000000");

        Label scaleLabel = new Label("Scale (km)");
        scaleLabel.getStyleClass().add("key-label");

        // --- Left: name, mass, color, speed ---
        nameField = entryField(String.format("Sat-%02d", satCounter + 1));
        massField = entryField("e.g. 1000");
        initialSpeedField = entryField("m/s");

        colorDropdown = new ComboBox<>();
        colorDropdown.getItems().addAll("Blue", "Red", "Green", "Orange", "Purple", "White");
        colorDropdown.setValue("Blue");
        colorDropdown.getStyleClass().add("combo-box");

        Label cartNameLbl = formLabel("Name :");
        Label cartMassLbl = formLabel("Mass (kg) :");
        Label cartColorLbl = formLabel("Color :");
        Label cartSpeedLbl = formLabel("Initial speed:");
        tip(cartNameLbl, nameField, "A unique identifier for this satellite.");
        tip(cartMassLbl, massField, "Mass of the satellite in kilograms.");
        tip(cartColorLbl, colorDropdown, "Display colour for this satellite.");
        tip(cartSpeedLbl, initialSpeedField,
                "Magnitude of the initial velocity in m/s.\nDirection is derived from position and rotation.");

        GridPane leftForm = new GridPane();
        leftForm.setHgap(10);
        leftForm.setVgap(8);
        leftForm.add(cartNameLbl, 0, 0);
        leftForm.add(nameField, 1, 0);
        leftForm.add(cartMassLbl, 0, 1);
        leftForm.add(fieldRow(massField, cartLocks, CL_MASS, this::randomizeCartMass), 1, 1);
        leftForm.add(cartColorLbl, 0, 2);
        leftForm.add(colorRow(colorDropdown, cartColorLock), 1, 2);
        leftForm.add(cartSpeedLbl, 0, 3);
        leftForm.add(fieldRow(initialSpeedField, cartLocks, CL_SPEED, this::randomizeCartSpeed), 1, 3);

        // --- Middle: Position ---
        posXField = entryField("0");
        posYField = entryField("0");
        posZField = entryField("0");

        Label posHdr = formLabel("Position (x,y,z) (m):");
        Label posXLbl = formLabel("x :");
        Label posYLbl = formLabel("y :");
        Label posZLbl = formLabel("z :");
        String posHint = "Initial position of the satellite\nrelative to the central body's centre, in metres.";
        tip(posHdr, posXField, posHint);
        tip(posXLbl, posXField, "X component of initial position (metres).");
        tip(posYLbl, posYField, "Y component of initial position (metres).");
        tip(posZLbl, posZField, "Z component of initial position (metres).");

        GridPane posForm = new GridPane();
        posForm.setHgap(10);
        posForm.setVgap(8);
        posForm.add(posHdr, 0, 0, 2, 1);
        posForm.add(posXLbl, 0, 1);
        posForm.add(fieldRow(posXField, cartLocks, CL_POSX, this::randomizeCartPosX), 1, 1);
        posForm.add(posYLbl, 0, 2);
        posForm.add(fieldRow(posYField, cartLocks, CL_POSY, () -> posYField.setText("0")), 1, 2);
        posForm.add(posZLbl, 0, 3);
        posForm.add(fieldRow(posZField, cartLocks, CL_POSZ, () -> posZField.setText("0")), 1, 3);

        // --- Right: Rotation ---
        rotIField = entryField("0");
        rotLField = entryField("0");
        rotWField = entryField("0");

        Label rotHdr = formLabel("Rotation (degrees):");
        Label rotILbl = formLabel("i :");
        Label rotLLbl = formLabel("I :");
        Label rotWLbl = formLabel("w :");
        tip(rotHdr, rotIField, "Initial orientation of the satellite's velocity vector.");
        tip(rotILbl, rotIField,
                "i — Inclination: tilt of the velocity direction\nrelative to the reference plane (degrees).");
        tip(rotLLbl, rotLField, "I — Longitude: azimuthal angle in the\nreference plane (degrees).");
        tip(rotWLbl, rotWField, "w — Roll / argument offset (degrees).");

        GridPane rotForm = new GridPane();
        rotForm.setHgap(10);
        rotForm.setVgap(8);
        rotForm.add(rotHdr, 0, 0, 2, 1);
        rotForm.add(rotILbl, 0, 1);
        rotForm.add(fieldRow(rotIField, cartLocks, CL_ROTI, () -> randomizeAngle360(rotIField)), 1, 1);
        rotForm.add(rotLLbl, 0, 2);
        rotForm.add(fieldRow(rotLField, cartLocks, CL_ROTL, () -> randomizeAngle360(rotLField)), 1, 2);
        rotForm.add(rotWLbl, 0, 3);
        rotForm.add(fieldRow(rotWField, cartLocks, CL_ROTW, () -> randomizeAngle360(rotWField)), 1, 3);

        HBox cartesianFormRow = new HBox(20, leftForm, posForm, rotForm);
        cartesianFormRow.setPadding(new Insets(12, 14, 0, 14));

        // ================================================================
        // --- ORBITAL ELEMENTS MODE FORM ---
        // ================================================================

        // --- Left: name, mass, color ---
        nameOrbField = entryField(String.format("Sat-%02d", satCounter + 1));
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
        orbitalFormRow.setVisible(false);
        orbitalFormRow.setManaged(false);

        // ================================================================
        // --- MODE TOGGLE ---
        // ================================================================

        ToggleButton cartesianBtn = new ToggleButton("Cartesian");
        ToggleButton orbitalBtn = new ToggleButton("Orbital Elements");
        cartesianBtn.getStyleClass().add("style-button");
        orbitalBtn.getStyleClass().add("style-button");

        ToggleGroup modeGroup = new ToggleGroup();
        cartesianBtn.setToggleGroup(modeGroup);
        orbitalBtn.setToggleGroup(modeGroup);
        cartesianBtn.setSelected(true);

        HBox modeToggle = new HBox(4, cartesianBtn, orbitalBtn);
        modeToggle.setPadding(new Insets(8, 14, 0, 14));

        cartesianBtn.setOnAction(e -> {
            orbitalElementsMode = false;
            cartesianFormRow.setVisible(true);
            cartesianFormRow.setManaged(true);
            orbitalFormRow.setVisible(false);
            orbitalFormRow.setManaged(false);

            updatePreview();
        });
        orbitalBtn.setOnAction(e -> {
            orbitalElementsMode = true;
            cartesianFormRow.setVisible(false);
            cartesianFormRow.setManaged(false);
            orbitalFormRow.setVisible(true);
            orbitalFormRow.setManaged(true);

            updatePreview();
        });

        // ================================================================

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
        HBox randAllRow = new HBox(randAllBtn);
        randAllRow.setAlignment(Pos.CENTER_RIGHT);
        randAllRow.setPadding(new Insets(6, 14, 0, 14));

        Label errorLabel = new Label("");
        errorLabel.getStyleClass().add("error-label");

        Button cancelBtn = new Button("CANCEL");
        Button createBtn = new Button("CREATE");
        cancelBtn.getStyleClass().add("style-button");
        createBtn.getStyleClass().add("style-button");
        cancelBtn.setOnAction(e -> {
            previewWorld.stopWorld();
            close();
        });
        createBtn.setOnAction(e -> {
            if (!validate(errorLabel))
                return;
            if (orbitalElementsMode) {
                // Run the math to check physical validity and surface any error
                Satellite tempSat = new Satellite();
                boolean hasError = tempSat.initialiseSatelliteValuesAngles(
                        getSatelliteName(), getSatelliteMass(), "", getMassOfBody(),
                        getaltitude(), getEccentricity(), getTrueAnomaly(),
                        getLongitudeAscendingNode(), getInclination(), getArgumentOfPeriapsis());
                if (hasError) {
                    errorLabel.setText(tempSat.getLatestError());
                    return;
                }
            }
            confirmed = true;

            previewWorld.stopWorld();
            close();
        });

        HBox buttons = new HBox(8, cancelBtn, createBtn);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.setPadding(new Insets(8, 14, 14, 14));

        Label titleLabel = new Label("Create new satellite");
        titleLabel.getStyleClass().add("subheading");

        VBox root = new VBox(titleLabel, preview, modeToggle, cartesianFormRow, orbitalFormRow, randAllRow, errorLabel,
                buttons);
        root.getStyleClass().add("small-pane");
        root.setStyle(themeStyle);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(new StyleSheet().styleSheet);

        owner.setOnCloseRequest(_ -> previewWorld.stopWorld());

        setScene(scene);
    }

    private boolean validate(Label errorLabel) {
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
        previewBody = new Body("previewBody", currentWorld.getBody().getMass(),
                currentWorld.getBody().getRadius(), currentWorld.getBody().getSemiMajorAxis(),
                currentWorld.getBody().getEccentricity());
        previewBody.setTimeScale(currentWorld.getBody().getTimeScale());

        previewWorld = new World(previewBody, currentWorld.getColour());

        preview.getChildren().add(previewRenderer.getViewport().getGLCanvas());
        previewRenderer.getViewport().getGLCanvas().setPrefSize(400, 400);

        previewSatellite = new Satellite();
        if (!previewSatellite.initialiseSatelliteValuesAngles(previewBody, "previewSatellite", getSatelliteMass(),
                previewBody.getRadius() + getaltitude(), getEccentricity(), getTrueAnomaly(),
                getLongitudeAscendingNode(), getInclination(), getArgumentOfPeriapsis())) {
            System.err.println("Failed to initialize preview satellite: " + previewSatellite.getLatestError());
        }

        Color color = getSatelliteColor();
        previewWorld.addSatellite(previewSatellite,
                new Vector3f((float) color.getRed(), (float) color.getGreen(), (float) color.getBlue()));

        previewWorld.runWorld();
        previewRenderer.setWorld(previewWorld);
        previewRenderer.setFocusObject(previewSatellite.getData().name);

        altitudeField.textProperty().addListener(_-> updatePreview());
        eccentricityField.textProperty().addListener(_-> updatePreview());
        trueAnomalyField.textProperty().addListener(_-> updatePreview());
        
        lonAscNodeField.textProperty().addListener(_-> updatePreview());
        inclinationField.textProperty().addListener(_-> updatePreview());
        argPeriapsisField.textProperty().addListener(_-> updatePreview());

        colorOrbDropdown.setOnAction(e -> updatePreviewColor()); // Update preview colour when changed
    }

    private void updatePreview() {
        SatelliteData data = previewSatellite.getData();

        double altitude = Math.clamp(getaltitude(), Constant.MINIMUM_ALTITUDE, (data.hillRadius / 1000) - previewBody.getRadius() - 1);

        double eccentricity = Math.clamp(getEccentricity(), 1e-10, 0.99);

        System.out.println(data.a);

        previewWorld.updateOrbitalElements(data.name, getSatelliteMass(),
                altitude, eccentricity, getTrueAnomaly(),
                getLongitudeAscendingNode(), getInclination(), getArgumentOfPeriapsis());
    }

    private void updatePreviewColor() {
        Color color = getSatelliteColor();
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
        if (orbitalElementsMode) {
            String n = nameOrbField.getText().trim();
            return n.isEmpty() ? String.format("Sat-%02d", ++satCounter) : n;
        }
        String n = nameField.getText().trim();
        return n.isEmpty() ? String.format("Sat-%02d", ++satCounter) : n;
    }

    public double getSatelliteMass() {
        return orbitalElementsMode
                ? Double.parseDouble(massOrbField.getText())
                : Double.parseDouble(massField.getText());
    }

    public double getInitialSpeed() {
        return Double.parseDouble(initialSpeedField.getText());
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

    public double getaltitude() {
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
                randomLog(bodyRadius * 1.5, bodyRadius * 100.0)));
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
