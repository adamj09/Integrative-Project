package project.UI;

import java.util.ArrayList;
import java.util.List;

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
import javafx.scene.text.Text;
import javafx.util.Duration;
import project.SimulationPool;
import project.Math.Body;
import project.Math.Utils;
import project.Math.Satellite;
import project.Math.SatelliteData;
import project.Presets.PresetConfiguration.BottomPanePreset;

public class BottomPane extends VBox {
    private SimulationPool pool;

    private final TextField specificTimeField;
    private final ComboBox<String> timescaleDropdown;
    private final Button startButton;
    private final Button stopButton;
    private final Button resetButton;
    private boolean running = true; //TODO fix this

    private final HBox dataGrid;

    private final TextField timeValueField;
    private final Label timeValueLabelNum;

    // Live data view state
    private String selectedSatellite = "";
    private String worldName = "";
    private final List<String> satelliteColumnNames = new ArrayList<>();
    private final Label noDataLabel;
    private final HBox liveDataControls;
    private GridPane grid;

    private static final String[][] FULL_NAMES = {
            { "Distance:", "0.0 km", "Speed:", "0.0 km/s" },
            { "Altitude:", "0.0 km", "Period:", "0.0 days" },
            { "Eccentricity:", "0.0", "Inclination:", "0.0 deg" },
            { "Longitude of Ascending Node:", "0.0 deg", "Argument of Periapsis:", "0.0 deg" },
            { "Total Energy:", "0.0 J", "Kinetic Energy:", "0.0 J" },
            { "Potential Energy:", "0.0 J", "Angular Momentum:", "0.0 (kg * m^2) / s" },
    };

    public BottomPane(SimulationPool pool) {
        this.pool = pool;

        getStyleClass().add("bottom-pane");

        Label infoLabel = new Label("Info :");
        infoLabel.getStyleClass().add("subheading");

        Label timeLabel = new Label("Set specific time (min):");
        timeLabel.getStyleClass().add("body");

        specificTimeField = new TextField();
        specificTimeField.setPromptText("Entry");
        specificTimeField.setPrefWidth(70);
        specificTimeField.getStyleClass().add("field");

        specificTimeField.setOnAction(_ -> {
            double newTime;
            try {
                newTime = Double.parseDouble(specificTimeField.getText());
            } catch(NumberFormatException ex) {
                return;
            }
            
            if(newTime < 0) {
                return;
            }

            pool.setTimeSec(newTime * 60); // Convert minutes to seconds
        });

        Label timescaleLabel = new Label("Time scale:");
        timescaleLabel.getStyleClass().add("body");

        timescaleDropdown = new ComboBox<>();
        timescaleDropdown.getItems().addAll(
                "1x", "2x", "5x", "10x",
                "100x", "1,000x", "10,000x", "100,000x");
        timescaleDropdown.setValue("1x");
        timescaleDropdown.getStyleClass().add("combo-box");
        timescaleDropdown.setPrefWidth(90);

        timescaleDropdown.setOnAction(_ -> {
            double timeScale;
            try {
                timeScale = Double.parseDouble(specificTimeField.getText());
                if(timeScale > 0) {
                    return;
                }
            } catch(NumberFormatException ex) {}

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

        startButton = new Button("START");
        startButton.getStyleClass().add("start-button");
        startButton.setOnAction(e -> {
            running = true;
            updateButtonStates();

            if(pool.getCurrentWorld() != null) {
                //pool.runWorld(pool.getCurrentWorld().getName());
                pool.startWorld();
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
            running = false;
            applyPresetState(new BottomPanePreset("", "1x", false));
            pool.stopWorld();
            pool.resetWorld();
            // removed because should not automatically start after reset, user should click start
            //pool.runWorld(pool.getCurrentWorld().getName()); 
            updateButtonStates();
        });

        updateButtonStates();

        Label timeValueLabel = new Label("Simulation time (minutes):");
        timeValueLabel.getStyleClass().add("body");
        timeValueField = new TextField();
        timeValueField.setEditable(false);
        timeValueField.getStyleClass().add("field");
        timeValueField.setText("0.0");
        timeValueField.setPrefWidth(100);
        timeValueLabelNum = new Label();
        timeValueLabelNum.setText(Utils.getWorldTimeFormated(0));
        timeValueLabelNum.getStyleClass().add("body");    

        HBox timeValueBox = new HBox(4, timeValueLabel, timeValueField,timeValueLabelNum);
        timeValueBox.setPadding(new Insets(6, 10, 6, 10));
        timeValueBox.setAlignment(Pos.CENTER_LEFT);

        HBox controlsRow = new HBox(10,
                infoLabel, timeLabel, specificTimeField,
                timescaleLabel, timescaleDropdown,
                startButton, stopButton, resetButton); 
        controlsRow.setPadding(new Insets(6, 10, 6, 10));
        controlsRow.setAlignment(Pos.CENTER_LEFT);

        BorderPane borderPane = new BorderPane();
        borderPane.setLeft(controlsRow);
        borderPane.setRight(timeValueBox);

        dataGrid = new HBox(6);
        dataGrid.setPadding(new Insets(6));
        dataGrid.getStyleClass().add("data-grid");
        dataGrid.setAlignment(Pos.TOP_LEFT);

        // Live data header
        Label liveLabel = new Label("Live data");
        liveLabel.getStyleClass().add("subheading");

        liveDataControls = new HBox(8, liveLabel);
        liveDataControls.setAlignment(Pos.CENTER_LEFT);
        liveDataControls.setPadding(new Insets(0, 0, 4, 0));

        noDataLabel = new Label("Click 'View Data' on a satellite to see its data");
        noDataLabel.getStyleClass().add("body");
        noDataLabel.setPadding(new Insets(10));

        dataGrid.getChildren().addAll(liveDataControls, noDataLabel);

        this.setOnMouseClicked(_ -> this.requestFocus());

        getChildren().addAll(borderPane, dataGrid);
    }

    private VBox makeSatelliteColumn(String title) {
        Label header = new Label(title);
        header.getStyleClass().add("subheading");

        HBox headerRow = new HBox(header);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        grid = new GridPane();
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

    private String getColumnName(VBox col) {
        if (!col.getChildren().isEmpty() && col.getChildren().get(0) instanceof HBox headerRow
                && !headerRow.getChildren().isEmpty()
                && headerRow.getChildren().get(0) instanceof Label label) {
            return label.getText();
        }
        return null;
    }

    public void addSatelliteColumn(String title) {
        VBox col = makeSatelliteColumn(title);
        col.setVisible(false);
        col.setManaged(false);
        dataGrid.getChildren().add(col);
        satelliteColumnNames.add(title);
    }

    public void clearSatelliteColumns() {
        if (dataGrid.getChildren().size() > 2) {
            dataGrid.getChildren().remove(2, dataGrid.getChildren().size());
        }
        satelliteColumnNames.clear();
        selectedSatellite = "";
        noDataLabel.setVisible(true);
        noDataLabel.setManaged(true);
    }

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

    public void selectSatelliteForView(String worldName, String name) {
        if (name.isEmpty()) {
            noDataLabel.setVisible(true);
            noDataLabel.setManaged(true);

            dataGrid.getChildren().clear();
            dataGrid.getChildren().addAll(liveDataControls, noDataLabel);
            return;
        }
        selectedSatellite = name;

        noDataLabel.setVisible(false);
        noDataLabel.setManaged(false);
    }

    public void removeSatelliteColumn(String title) {
        dataGrid.getChildren().removeIf(node -> {
            if (node instanceof VBox col) {
                String colName = getColumnName(col);
                if (title.equals(colName)) {
                    if (title.equals(selectedSatellite))
                        selectedSatellite = "";
                    return true;
                }
            }
            return false;
        });
        satelliteColumnNames.remove(title);

        if (selectedSatellite.isEmpty()) {
            noDataLabel.setVisible(true);
            noDataLabel.setManaged(true);
        }
    }

    public void updateLoop() {
        // Update live data
        Timeline updateLoop = new Timeline();
        updateLoop.setCycleCount(Timeline.INDEFINITE);

        KeyFrame frame = new KeyFrame(Duration.seconds(0.5), // Enter the target frame time here.
                _ -> {
                    if (pool.getCurrentWorld() != null) {
                        updateSatelliteData();
                    }
                });

        updateLoop.getKeyFrames().add(frame);
        updateLoop.play();
    }

    public void updateSatelliteData() {
        //set time simulation
        Body body = pool.getCurrentWorld().getBody();
        double timeSeconds = body.getTimeSeconds();
        double timeMinutes = (timeSeconds/60.0);
        timeValueField.setText(String.format("%.4f", timeMinutes));
        timeValueLabelNum.setText(Utils.getWorldTimeFormated(timeSeconds));

        if (selectedSatellite.isEmpty()) {
            return;
        }

        Satellite satellite = pool.getCurrentWorld().getBody().getSatellite(selectedSatellite);
        if (satellite == null) {
            return;
        }

        SatelliteData data = satellite.getData();

        double distance = data.distance / 1000.d; // In km
        double speed = data.speed / 1000.d; // In km/s
        double altitude = distance - body.getRadius();
        double period = data.period / 60 / 60 / 24; // In days

        double eccentricity = data.eccentricity;
        double inclination = Math.toDegrees(data.inclination);
        double longitudeOfAscendingNode = Math.toDegrees(data.longitudeOfAscendingNode);
        double argumentOfPeriapsis = Math.toDegrees(data.argumentOfPeriapsis);
        double totalEnergy = data.totalEnergy;
        double kineticEnergy = data.kineticEnergy;
        double potentialEnergy = data.gravitationalPotentialEnergy;
        double angularMomentum = data.angularMomentum;

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

        VBox col = makeSatelliteColumn(selectedSatellite);
        col.setVisible(true);
        col.setManaged(true);
        dataGrid.getChildren().set(1, col);
    }

    private void updateButtonStates() {
        startButton.setDisable(running);
        stopButton.setDisable(!running);
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    
}
