package project.UI;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import project.SimulationPool;
import project.Math.Body;
import project.Math.Satellite;
import project.Math.SatelliteData;
import project.Presets.PresetConfiguration.BottomPanePreset;

/**
 * Class that handles simulation-specific controls and the live satellite data
 * feed.
 * 
 * @author Ryan Lau
 * @author Adam Johnston
 */
public class BottomPane extends VBox {
    /**
     * Simulation pool that this BottomPane controls.
     */
    private SimulationPool pool;

    /**
     * Text field used to set the simulation to a specific time in the future.
     */
    private TextField specificTimeField;

    /**
     * ComboBox used to set the simulation's time scale.
     */
    private ComboBox<String> timescaleDropdown;

    /**
     * Buttons for controlling simulation's run state.
     */
    private Button startButton, stopButton, resetButton;

    /**
     * Tracks whether the simulation is running for UI purposes.
     */
    private boolean running = true;

    /**
     * Labels for UI
     */
    private Label timescaleLabel = new Label("Time scale:"), infoLabel = new Label("Info: "),
            timeLabel = new Label("Set specific time (s):");

    /**
     * HBox containing the simulation's controls and live data feed.
     */
    private HBox dataRoot;

    /**
     * Text field used to display the simulation's current time.
     */
    private TextField timeValueField;

    /**
     * Pane that contains
     */
    private BorderPane controlsPane;

    /**
     * HBox containing the live data feed only.
     */
    private HBox liveDataBox;

    /**
     * Live data view state.
     */
    private String selectedSatellite = "";
    private String worldName = "";
    private Label noDataLabel;
    private GridPane grid = new GridPane();;

    /**
     * 2D array containing data to be displayed and their respective names.
     */
    private static final String[][] FULL_NAMES = {
            { "Distance:", "0.0 km", "Speed:", "0.0 km/s" },
            { "Altitude:", "0.0 km", "Period:", "0.0 days" },
            { "Eccentricity:", "0.0", "Inclination:", "0.0 deg" },
            { "Longitude of Ascending Node:", "0.0 deg", "Argument of Periapsis:", "0.0 deg" },
            { "Total Energy:", "0.0 J", "Kinetic Energy:", "0.0 J" },
            { "Potential Energy:", "0.0 J", "Angular Momentum:", "0.0 (kg * m^2) / s" },
    };

    /**
     * Creates a new BottomPane that controls and obtains data from the given
     * SimulationPool.
     *
     * @param pool the SimulationPool to control and obtain data from.
     */
    public BottomPane(SimulationPool pool) {
        this.pool = pool;

        getStyleClass().add("bottom-pane");

        infoLabel.getStyleClass().add("subheading");
        timeLabel.getStyleClass().add("body");

        setUpTimeControls();
        setUpToggleControls();

        updateButtonStates();

        setUpLiveDataControls();

        noDataLabel = new Label("Click 'View Data' on a satellite to see its data");
        noDataLabel.getStyleClass().add("body");
        noDataLabel.setPadding(new Insets(10));

        dataRoot.getChildren().addAll(liveDataBox, noDataLabel);

        this.setOnMouseClicked(_ -> this.requestFocus());

        getChildren().addAll(controlsPane, dataRoot);
    }

    /**
     * Sets up all the time-related controls.
     */
    private void setUpTimeControls() {
        specificTimeField = new TextField();
        specificTimeField.setPromptText("Entry");
        specificTimeField.setPrefWidth(70);
        specificTimeField.getStyleClass().add("field");

        specificTimeField.setOnAction(_ -> {
            // TODO: this should perhaps be used to set the simulation to a certain time in
            // the
            // future, not for setting the time scale as is implemented.

            double timeScale;
            try {
                timeScale = Double.parseDouble(specificTimeField.getText());
            } catch (NumberFormatException ex) {
                return;
            }

            if (timeScale <= 0) {
                return;
            }

            pool.setTimeScale(timeScale);
        });

        timescaleLabel.getStyleClass().add("body");

        timescaleDropdown = new ComboBox<>();
        timescaleDropdown.getItems().addAll("1x", "2x", "5x", "10x", "100x", "1,000x", "10,000x", "100,000x");
        timescaleDropdown.setValue("1x");
        timescaleDropdown.getStyleClass().add("combo-box");
        timescaleDropdown.setPrefWidth(90);

        timescaleDropdown.setOnAction(_ -> {
            double timeScale;
            try {
                timeScale = Double.parseDouble(specificTimeField.getText());
                if (timeScale > 0) {
                    return;
                }
            } catch (NumberFormatException ex) {
            }

            switch (timescaleDropdown.getValue()) {
                case "1x":
                    pool.setTimeScale(1);
                    break;
                case "2x":
                    pool.setTimeScale(2);
                    break;
                case "5x":
                    pool.setTimeScale(5);
                    break;
                case "10x":
                    pool.setTimeScale(10);
                    break;
                case "100x":
                    pool.setTimeScale(100);
                    break;
                case "1,000x":
                    pool.setTimeScale(1000);
                    break;
                case "10,000x":
                    pool.setTimeScale(10_000);
                    break;
                case "100,000x":
                    pool.setTimeScale(100_000);
                    break;
                default:
                    pool.setTimeScale(1);
                    break;
            }
        });
    }

    /**
     * Sets up the controls for starting, stopping, and resetting the simulation.
     */
    private void setUpToggleControls() {
        startButton = new Button("START");
        startButton.getStyleClass().add("start-button");
        startButton.setOnAction(e -> {
            running = true;
            updateButtonStates();

            if (pool.getCurrentWorld() != null) {
                pool.runWorld(pool.getCurrentWorld().getName());
            }
        });

        stopButton = new Button("STOP");
        stopButton.getStyleClass().add("stop-button");
        stopButton.setOnAction(e -> {
            running = false;
            updateButtonStates();
            pool.stopWorld();
        });

        resetButton = new Button("RESET");
        resetButton.getStyleClass().add("reset-button");
        resetButton.setOnAction(e -> {
            running = true;
            applyPresetState(new BottomPanePreset("", "1x", false));
            pool.stopWorld();
            pool.resetWorld();
            // removed because should not automatically start after reset, user should click
            // start
            // pool.runWorld(pool.getCurrentWorld().getName());
            updateButtonStates();
        });
    }

    /**
     * Sets up the controls related to live data display
     */
    private void setUpLiveDataControls() {
        Label timeValueLabel = new Label("Simulation time (minutes):");
        timeValueLabel.getStyleClass().add("body");
        timeValueField = new TextField();
        timeValueField.getStyleClass().add("field");
        timeValueField.setText("0.0");
        timeValueField.setPrefWidth(100);

        HBox timeValueBox = new HBox(4, timeValueLabel, timeValueField);
        timeValueBox.setPadding(new Insets(6, 10, 6, 10));
        timeValueBox.setAlignment(Pos.CENTER_LEFT);

        HBox controlsRow = new HBox(10,
                infoLabel, timeLabel, specificTimeField,
                timescaleLabel, timescaleDropdown,
                startButton, stopButton, resetButton);
        controlsRow.setPadding(new Insets(6, 10, 6, 10));
        controlsRow.setAlignment(Pos.CENTER_LEFT);

        controlsPane = new BorderPane();
        controlsPane.setLeft(controlsRow);
        controlsPane.setRight(timeValueBox);

        dataRoot = new HBox(6);
        dataRoot.setPadding(new Insets(6));
        dataRoot.getStyleClass().add("data-grid");
        dataRoot.setAlignment(Pos.TOP_LEFT);

        // Live data header
        Label liveLabel = new Label("Live data");
        liveLabel.getStyleClass().add("subheading");

        liveDataBox = new HBox(8, liveLabel);
        liveDataBox.setAlignment(Pos.CENTER_LEFT);
        liveDataBox.setPadding(new Insets(0, 0, 4, 0));
    }

    /**
     * Builds a new data grid with the given name.
     * 
     * @param title name of the data grid.
     * @return a new data grid in the form of a VBox.
     */
    private VBox buildDataGrid(String title) {
        Label header = new Label(title);
        header.getStyleClass().add("subheading");

        HBox headerRow = new HBox(header);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        grid.getChildren().clear();
        grid.setHgap(12);
        grid.setVgap(4);
        grid.setPadding(new Insets(6));

        for (int r = 0; r < FULL_NAMES.length; r++) {
            for (int c = 0; c < 4; c++) {
                boolean isKey = c % 2 == 0;
                Label lbl = new Label(FULL_NAMES[r][c]);

                if (isKey) {
                    lbl.getStyleClass().add("key");
                } else {
                    lbl.getStyleClass().add("key-label");
                }

                lbl.setMaxWidth(Double.MAX_VALUE);
                GridPane.setHgrow(lbl, Priority.ALWAYS);
                grid.add(lbl, c, r);
            }
        }

        VBox dataBox = new VBox(grid);
        dataBox.getStyleClass().add("data-box");

        VBox col = new VBox(4, headerRow, dataBox);
        col.setPadding(new Insets(5));
        col.getStyleClass().add("column-expanded");
        col.setFillWidth(true);
        HBox.setHgrow(col, Priority.ALWAYS);

        return col;
    }

    /**
     * @return BottomPanePreset object used to save specific time and time scale
     *         settings.
     */
    public BottomPanePreset toPresetState() {
        String specificTime = specificTimeField.getText() == null ? "" : specificTimeField.getText();
        String timescale = timescaleDropdown.getValue() == null ? "1x" : timescaleDropdown.getValue();
        return new BottomPanePreset(specificTime, timescale, running);
    }

    public void applyPresetState(BottomPanePreset preset) {
        specificTimeField.setText(preset.specificTime());

        String timescale = preset.timescale();
        if (timescale == null || !timescaleDropdown.getItems().contains(timescale)) {
            timescale = "1x";
        }
        timescaleDropdown.setValue(timescale);

        running = preset.running();
        updateButtonStates();
    }

    /**
     * Selects a satellite for viewing of its data.
     * 
     * @param worldName world to which the satellite belongs.
     * @param name      name of the satellite.
     */
    public void selectSatelliteForView(String worldName, String name) {
        if (name.isEmpty()) {
            noDataLabel.setVisible(true);
            noDataLabel.setManaged(true);

            dataRoot.getChildren().clear();
            dataRoot.getChildren().addAll(noDataLabel);
            return;
        }
        selectedSatellite = name;

        noDataLabel.setVisible(false);
        noDataLabel.setManaged(false);
    }

    /**
     * Runs a JavaFx animation that updates the data of the selected satellite every
     * 0.5 seconds.
     */
    public void updateLoop() {
        Timeline updateLoop = new Timeline();
        updateLoop.setCycleCount(Timeline.INDEFINITE);

        KeyFrame frame = new KeyFrame(Duration.seconds(0.5), // Enter the target frame time here.
                _ -> {
                    // If a world is running or has run in the past, update the satellite's data
                    // view.
                    if (pool.getCurrentWorld() != null) {
                        updateSatelliteData();
                    }
                });

        updateLoop.getKeyFrames().add(frame);
        updateLoop.play();
    }

    /**
     * Updates the grid of satellite data to contain the latest information,
     * straight from the SatelliteData class of the currently selected satellite.
     */
    public void updateSatelliteData() {
        // Update display of simulation time.
        Body body = pool.getCurrentWorld().getBody();
        double timeMinutes = (body.getTimeSeconds() / 60.0);
        timeValueField.setText(String.format("%.4f", timeMinutes));

        if (selectedSatellite.isEmpty()) {
            return;
        }

        Satellite satellite = pool.getCurrentWorld().getBody().getSatellite(selectedSatellite);

        // If the satellite we are trying to update does not exist, do nothing.
        if (satellite == null) {
            return;
        }

        SatelliteData data = satellite.getData();

        // Obtain all data and convert to appropriate units if necessary.
        double distance = data.distance / 1000.d; // In km
        double speed = data.speed / 1000.d; // In km/s
        double altitude = distance - body.getRadius();
        double period = data.period / 60 / 60 / 24; // In days
        double eccentricity = data.eccentricity;
        double inclination = Math.toDegrees(data.inclination);
        double longitudeOfAscendingNode = Math.toDegrees(data.longitudeOfAscendingNode);
        double argumentOfPeriapsis = Math.toDegrees(data.argumentOfPeriapsis);
        double totalEnergy = data.totalEnergy; // In Joules
        double kineticEnergy = data.kineticEnergy;
        double potentialEnergy = data.gravitationalPotentialEnergy;
        double angularMomentum = data.angularMomentum; // In (kg * m^2) / s

        grid.getChildren().clear();

        FULL_NAMES[0][1] = String.format("%.4f km", distance);
        FULL_NAMES[0][3] = String.format("%.4f km/s", speed);
        FULL_NAMES[1][1] = String.format("%.4f km", altitude);
        FULL_NAMES[1][3] = String.format("%.4f days", period);
        FULL_NAMES[2][1] = String.format("%.4f", eccentricity);
        FULL_NAMES[2][3] = String.format("%.4f deg", inclination);
        FULL_NAMES[3][1] = String.format("%.4f deg", longitudeOfAscendingNode);
        FULL_NAMES[3][3] = String.format("%.4f deg", argumentOfPeriapsis);
        FULL_NAMES[4][1] = String.format("%.4f J", totalEnergy);
        FULL_NAMES[4][3] = String.format("%.4f J", kineticEnergy);
        FULL_NAMES[5][1] = String.format("%.4f J", potentialEnergy);
        FULL_NAMES[5][3] = String.format("%.4f (kg * m^2) / s", angularMomentum);

        VBox col = buildDataGrid(selectedSatellite);
        col.setVisible(true);
        col.setManaged(true);
        dataRoot.getChildren().set(1, col);
    }

    /**
     * Updates the start/stop buttons' states.
     */
    private void updateButtonStates() {
        startButton.setDisable(running);
        stopButton.setDisable(!running);
    }

    /**
     * Sets the world to track data from.
     * 
     * @param worldName desired world to track.
     */
    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }
}
