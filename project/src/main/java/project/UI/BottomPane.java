package project.UI;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import project.Presets.PresetConfiguration.BottomPanePreset;

public class BottomPane extends VBox {

    private final TextField specificTimeField;
    private final ComboBox<String> timescaleDropdown;
    private final Button startButton;
    private final Button stopButton;
    private final Button resetButton;
    private boolean running = false;

    private final HBox dataGrid;

    // Live data view state
    private String selectedSatellite = null;
    private String comparedSatellite = null;
    private final List<String> satelliteColumnNames = new ArrayList<>();
    private final Label noDataLabel;
    private final ComboBox<String> compareDropdown;
    private final HBox liveDataControls;

    private static final String[][] FULL_NAMES = {
        {"Distance",     "0.0 km",   "Speed",       "0.0 km/s"},
        {"Altitude",     "0.0 km",   "Period",      "0.0 days"},
        {"Eccentricity", "0.0",      "Inclination", "0.0 deg"},
    };

    public BottomPane() {
        getStyleClass().add("bottom-pane");

        Label infoLabel = new Label("Info :");
        infoLabel.getStyleClass().add("subheading");

        Label timeLabel = new Label("Set specific time (s):");
        timeLabel.getStyleClass().add("body");

        specificTimeField = new TextField();
        specificTimeField.setPromptText("Entry");
        specificTimeField.setPrefWidth(70);
        specificTimeField.getStyleClass().add("field");

        Label timescaleLabel = new Label("Time scale:");
        timescaleLabel.getStyleClass().add("body");

        timescaleDropdown = new ComboBox<>();
        timescaleDropdown.getItems().addAll(
            "1x", "2x", "5x", "10x",
            "100x", "1000x", "10000x", "100000x"
        );
        timescaleDropdown.setValue("1x");
        timescaleDropdown.getStyleClass().add("combo-box");
        timescaleDropdown.setPrefWidth(90);

        startButton = new Button("START");
        startButton.getStyleClass().add("start-button");
        startButton.setOnAction(e -> {
            running = true;
            updateButtonStates();
            System.out.println("Simulation started");
        });

        stopButton = new Button("STOP");
        stopButton.getStyleClass().add("stop-button");
        stopButton.setOnAction(e -> {
            running = false;
            updateButtonStates();
            System.out.println("Simulation stopped");
        });

        resetButton = new Button("RESET");
        resetButton.getStyleClass().add("reset-button");
        resetButton.setOnAction(e -> {
            running = false;
            applyPresetState(new BottomPanePreset("", "1x", false));
            System.out.println("Simulation reset");
        });

        updateButtonStates();

        HBox controlsRow = new HBox(10,
            infoLabel, timeLabel, specificTimeField,
            timescaleLabel, timescaleDropdown,
            startButton, stopButton, resetButton
        );
        controlsRow.setPadding(new Insets(6, 10, 6, 10));
        controlsRow.setAlignment(Pos.CENTER_LEFT);

        dataGrid = new HBox(6);
        dataGrid.setPadding(new Insets(6));
        dataGrid.getStyleClass().add("data-grid");
        dataGrid.setAlignment(Pos.TOP_LEFT);

        // Live data header
        Label liveLabel = new Label("Live data");
        liveLabel.getStyleClass().add("subheading");

        compareDropdown = new ComboBox<>();
        compareDropdown.setPromptText("Compare with...");
        compareDropdown.getStyleClass().add("combo-box");
        compareDropdown.setPrefWidth(150);
        compareDropdown.setVisible(false);
        compareDropdown.setManaged(false);
        compareDropdown.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(item == null || empty ? "Compare with..." : item);
            }
        });

        compareDropdown.setOnAction(e -> {
            String chosen = compareDropdown.getValue();
            if (chosen != null && !chosen.isEmpty() && selectedSatellite != null) {
                comparedSatellite = chosen;
                // Reset display back to prompt after selection
                javafx.application.Platform.runLater(() -> compareDropdown.setValue(null));
                refreshDataView();
            }
        });

        liveDataControls = new HBox(8, liveLabel, compareDropdown);
        liveDataControls.setAlignment(Pos.CENTER_LEFT);
        liveDataControls.setPadding(new Insets(0, 0, 4, 0));

        noDataLabel = new Label("Click 'View Data' on a satellite to see its data");
        noDataLabel.getStyleClass().add("body");
        noDataLabel.setPadding(new Insets(10));

        dataGrid.getChildren().addAll(liveDataControls, noDataLabel);

        this.setOnMouseClicked(_ -> this.requestFocus());

        getChildren().addAll(controlsRow, dataGrid);
    }

    private VBox makeSatelliteColumn(String title) {
        Label header = new Label(title);
        header.getStyleClass().add("subheading");

        HBox headerRow = new HBox(header);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(4);
        grid.setPadding(new Insets(6));

        for (int r = 0; r < FULL_NAMES.length; r++) {
            for (int c = 0; c < 4; c++) {
                boolean isKey = c % 2 == 0;
                Label lbl = new Label(FULL_NAMES[r][c] + (isKey ? ":" : ""));

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

        // Swap dropdown at the bottom of each column
        ComboBox<String> swapDropdown = new ComboBox<>();
        swapDropdown.setPromptText("Swap to...");
        swapDropdown.getStyleClass().add("combo-box");
        swapDropdown.setPrefWidth(150);

        // Populate with all satellites except this column's satellite
        for (String s : satelliteColumnNames) {
            if (!s.equals(title)) {
                swapDropdown.getItems().add(s);
            }
        }

        swapDropdown.setOnAction(e -> {
            String chosen = swapDropdown.getValue();
            if (chosen == null || chosen.isEmpty()) return;

            if (title.equals(selectedSatellite)) {
                selectedSatellite = chosen;
            } else if (title.equals(comparedSatellite)) {
                comparedSatellite = chosen;
            }
            updateCompareDropdownItems();
            if (comparedSatellite != null) {
                compareDropdown.setValue(comparedSatellite);
            }
            refreshDataView();
            rebuildSwapDropdowns();
        });

        HBox swapRow = new HBox(swapDropdown);
        swapRow.setAlignment(Pos.CENTER_LEFT);
        swapRow.setPadding(new Insets(4, 0, 0, 0));

        VBox col = new VBox(4, headerRow, dataBox, swapRow);
        col.setPadding(new Insets(5));
        col.getStyleClass().add("column-expanded");
        col.setFillWidth(true);
        HBox.setHgrow(col, Priority.ALWAYS);

        return col;
    }

    private void updateCompareDropdownItems() {
        compareDropdown.getItems().clear();
        for (String s : satelliteColumnNames) {
            if (!s.equals(selectedSatellite)) {
                compareDropdown.getItems().add(s);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void rebuildSwapDropdowns() {
        // Collect which satellites are currently shown
        List<String> shown = new ArrayList<>();
        if (selectedSatellite != null) shown.add(selectedSatellite);
        if (comparedSatellite != null) shown.add(comparedSatellite);

        for (int i = 2; i < dataGrid.getChildren().size(); i++) {
            var node = dataGrid.getChildren().get(i);
            if (node instanceof VBox col) {
                String colName = getColumnName(col);
                var lastChild = col.getChildren().get(col.getChildren().size() - 1);
                if (lastChild instanceof HBox swapRow && !swapRow.getChildren().isEmpty()
                        && swapRow.getChildren().get(0) instanceof ComboBox<?>) {
                    ComboBox<String> dd = (ComboBox<String>) swapRow.getChildren().get(0);
                    dd.getItems().clear();
                    for (String s : satelliteColumnNames) {
                        // Exclude this column's own satellite AND the other shown satellite
                        if (!s.equals(colName) && !shown.contains(s)) {
                            dd.getItems().add(s);
                        }
                    }
                    dd.setValue(null);
                }
            }
        }
    }

    private void refreshDataView() {
        for (int i = 2; i < dataGrid.getChildren().size(); i++) {
            var node = dataGrid.getChildren().get(i);
            if (node instanceof VBox col) {
                String colName = getColumnName(col);
                if (colName != null) {
                    boolean show;
                    if (comparedSatellite != null) {
                        show = colName.equals(selectedSatellite) || colName.equals(comparedSatellite);
                    } else {
                        show = colName.equals(selectedSatellite);
                    }
                    col.setVisible(show);
                    col.setManaged(show);
                }
            }
        }
        rebuildSwapDropdowns();
    }

    private String getColumnName(VBox col) {
        if (!col.getChildren().isEmpty() && col.getChildren().get(0) instanceof HBox headerRow
                && !headerRow.getChildren().isEmpty()
                && headerRow.getChildren().get(0) instanceof Label label) {
            return label.getText();
        }
        return null;
    }

    public void selectSatelliteForView(String name) {
        selectedSatellite = name;
        comparedSatellite = null;

        updateCompareDropdownItems();
        compareDropdown.setValue(null);
        compareDropdown.setVisible(true);
        compareDropdown.setManaged(true);

        noDataLabel.setVisible(false);
        noDataLabel.setManaged(false);

        refreshDataView();
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
        selectedSatellite = null;
        comparedSatellite = null;
        compareDropdown.getItems().clear();
        compareDropdown.setVisible(false);
        compareDropdown.setManaged(false);
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

    public void removeSatelliteColumn(String title) {
        dataGrid.getChildren().removeIf(node -> {
            if (node instanceof VBox col) {
                String colName = getColumnName(col);
                if (title.equals(colName)) {
                    if (title.equals(selectedSatellite)) selectedSatellite = null;
                    if (title.equals(comparedSatellite)) comparedSatellite = null;
                    return true;
                }
            }
            return false;
        });
        satelliteColumnNames.remove(title);
        compareDropdown.getItems().remove(title);

        if (selectedSatellite == null) {
            noDataLabel.setVisible(true);
            noDataLabel.setManaged(true);
            compareDropdown.setVisible(false);
            compareDropdown.setManaged(false);
        }
    }

    public void updateSatelliteData(int index, String[][] keyValuePairs) {
        // TODO: implement live data updates once data layer exists
    }

    private void updateButtonStates() {
        startButton.setDisable(running);
        stopButton.setDisable(!running);
    }
}
