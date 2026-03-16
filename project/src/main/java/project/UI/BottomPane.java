package project.UI;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class BottomPane extends VBox {

    private final TextField specificTimeField;
    private final ComboBox<String> timescaleDropdown;
    private final Button startStopButton;
    private final Button resetButton;
    private boolean running = false;

    private final HBox dataGrid;
    private VBox dragSource = null;

    private static final double MIN_W     = 160;
    private static final double MAX_W     = 400;
    private static final double DEFAULT_W = 200;

    private static final String[][] FULL_NAMES = {
        {"Distance",     "0.0 km",   "Speed",       "0.0 km/s"},
        {"Altitude",     "0.0 km",   "Period",      "0.0 days"},
        {"Eccentricity", "0.0",      "Inclination", "0.0 deg"},
    };
    private static final String[][] SHORT_NAMES = {
        {"Dist", "0.0 km",   "Spd", "0.0 km/s"},
        {"Alt",  "0.0 km",   "Per", "0.0 days"},
        {"Ecc",  "0.0",      "Inc", "0.0 deg"},
    };

    public BottomPane() {
        setStyle("-fx-background-color: #1a1a2e; -fx-border-color: #444466; -fx-border-width: 1 0 0 0;");

        Label infoLabel = new Label("Info :");
        infoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #c0c0e0; -fx-font-weight: bold;");

        Label timeLabel = new Label("Set specific time (s):");
        timeLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #c0c0e0;");

        specificTimeField = new TextField();
        specificTimeField.setPromptText("Entry");
        specificTimeField.setPrefWidth(70);
        specificTimeField.setStyle(fieldStyle());

        Label timescaleLabel = new Label("Time scale:");
        timescaleLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #c0c0e0;");

        timescaleDropdown = new ComboBox<>();
        timescaleDropdown.getItems().addAll(
            "0.1x", "0.5x", "1x", "2x", "5x", "10x",
            "100x", "1000x", "10000x", "100000x"
        );
        timescaleDropdown.setValue("1x");
        timescaleDropdown.setStyle("-fx-font-size: 11px;");
        timescaleDropdown.setPrefWidth(90);

        startStopButton = new Button("Start/Stop");
        startStopButton.setStyle(controlButtonStyle());
        startStopButton.setOnAction(e -> {
            running = !running;
            System.out.println("Simulation running: " + running);
        });

        resetButton = new Button("RESET");
        resetButton.setStyle(controlButtonStyle());
        resetButton.setOnAction(e -> {
            running = false;
            specificTimeField.clear();
            timescaleDropdown.setValue("1x");
            System.out.println("Simulation reset");
        });

        HBox controlsRow = new HBox(10,
            infoLabel, timeLabel, specificTimeField,
            timescaleLabel, timescaleDropdown,
            startStopButton, resetButton
        );
        controlsRow.setPadding(new Insets(6, 10, 6, 10));
        controlsRow.setAlignment(Pos.CENTER_LEFT);

        dataGrid = new HBox(6);
        dataGrid.setPadding(new Insets(6));
        dataGrid.setStyle("-fx-background-color: #1a1a2e;");
        dataGrid.setAlignment(Pos.TOP_LEFT);
        dataGrid.setFillHeight(false);

        Label liveLabel = new Label("Live data");
        liveLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #c0c0e0; -fx-padding: 0 8 0 8;");
        liveLabel.setAlignment(Pos.CENTER_LEFT);
        dataGrid.getChildren().add(liveLabel);

        getChildren().addAll(controlsRow, dataGrid);
    }

    private VBox makeSatelliteColumn(String title) {
        Label header = new Label(title);
        header.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #c0c0e0;");
        HBox.setHgrow(header, Priority.ALWAYS);

        Button toggleButton = new Button("-");
        toggleButton.setStyle(
            "-fx-background-color: #4a4a6a; -fx-text-fill: #c0c0e0; " +
            "-fx-font-size: 9px; -fx-padding: 1 5 1 5; " +
            "-fx-background-radius: 2; -fx-cursor: hand;"
        );

        HBox headerRow = new HBox(header, toggleButton);
        headerRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(header, Priority.ALWAYS);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(4);
        grid.setPadding(new Insets(6));

        List<Label> keyLabels = new ArrayList<>();

        for (int r = 0; r < FULL_NAMES.length; r++) {
            for (int c = 0; c < 4; c++) {
                boolean isKey = c % 2 == 0;
                Label lbl = new Label(FULL_NAMES[r][c] + (isKey ? ":" : ""));
                lbl.setStyle("-fx-font-size: 10px; -fx-text-fill: " +
                             (isKey ? "#8888bb;" : "#e0e0ff;"));
                lbl.setMaxWidth(Double.MAX_VALUE);
                GridPane.setHgrow(lbl, Priority.ALWAYS);
                grid.add(lbl, c, r);
                if (isKey) keyLabels.add(lbl);
            }
        }

        VBox dataBox = new VBox(grid);
        dataBox.setStyle(
            "-fx-background-color: #252540; " +
            "-fx-border-color: #555577; " +
            "-fx-border-width: 1; " +
            "-fx-border-radius: 3; " +
            "-fx-background-radius: 3;"
        );

        VBox col = new VBox(4, headerRow, dataBox);
        col.setPadding(new Insets(5));
        col.setPrefWidth(DEFAULT_W);
        col.setMinWidth(MIN_W);
        col.setMaxWidth(MAX_W);
        col.setStyle(expandedColStyle());
        col.setFillWidth(true);

        final double[] drag = {0, 0};
        final boolean[] resizing = {false};

        col.setOnMouseMoved(e -> {
            if (e.getX() >= col.getWidth() - 7) {
                col.setCursor(Cursor.H_RESIZE);
            } else {
                col.setCursor(Cursor.DEFAULT);
            }
        });

        col.setOnMousePressed(e -> {
            if (e.getX() >= col.getWidth() - 7) {
                drag[0] = e.getSceneX();
                drag[1] = col.getPrefWidth();
                resizing[0] = true;
                e.consume();
            }
        });

        col.setOnMouseDragged(e -> {
            if (resizing[0]) {
                double newW = drag[1] + (e.getSceneX() - drag[0]);
                newW = Math.max(MIN_W, Math.min(MAX_W, newW));
                col.setPrefWidth(newW);
                col.setMinWidth(newW);
                e.consume();
            }
        });

        col.setOnMouseReleased(e -> {
            resizing[0] = false;
            col.setCursor(Cursor.DEFAULT);
        });

        final boolean[] minimized = {false};
        final double[] savedWidth = {DEFAULT_W};

        toggleButton.setOnAction(e -> {
            minimized[0] = !minimized[0];
            boolean isMin = minimized[0];

            dataBox.setVisible(!isMin);
            dataBox.setManaged(!isMin);
            toggleButton.setText(isMin ? "+" : "-");
            col.setStyle(collapsedColStyle());

            int keyIndex = 0;
            for (int r = 0; r < FULL_NAMES.length; r++) {
                for (int c = 0; c < 4; c += 2) {
                    String name = isMin ? SHORT_NAMES[r][c] : FULL_NAMES[r][c];
                    keyLabels.get(keyIndex).setText(name + ":");
                    keyIndex++;
                }
            }

            if (isMin) {
                savedWidth[0] = col.getWidth();
                col.setSpacing(0);
                col.setPadding(new Insets(3));
                col.setMinWidth(Region.USE_COMPUTED_SIZE);
                col.setMaxWidth(Region.USE_COMPUTED_SIZE);
                col.setPrefWidth(Region.USE_COMPUTED_SIZE);
                col.setMinHeight(Region.USE_COMPUTED_SIZE);
                col.setMaxHeight(Region.USE_COMPUTED_SIZE);
                col.setPrefHeight(Region.USE_COMPUTED_SIZE);
            } else {
                col.setSpacing(4);
                col.setPadding(new Insets(5));
                col.setMinWidth(MIN_W);
                col.setMaxWidth(MAX_W);
                col.setPrefWidth(savedWidth[0]);
                col.setMinHeight(Region.USE_COMPUTED_SIZE);
                col.setMaxHeight(Region.USE_COMPUTED_SIZE);
                col.setPrefHeight(Region.USE_COMPUTED_SIZE);
            }
        });

        col.setOnDragDetected(e -> {
            if (col.getCursor() != Cursor.H_RESIZE) {
                dragSource = col;
                col.startFullDrag();
                col.setOpacity(0.4);
                e.consume();
            }
        });

        col.setOnMouseDragEntered(e -> {
            if (dragSource != null && dragSource != col) {
                col.setStyle("-fx-border-color: #8888ff; -fx-border-width: 2; " +
                             "-fx-border-radius: 4; -fx-background-color: #2a2a4a; -fx-background-radius: 4;");
            }
        });

        col.setOnMouseDragExited(e -> {
            if (dragSource != col) {
                col.setStyle(collapsedColStyle());
            }
        });

        col.setOnMouseDragReleased(e -> {
            if (dragSource != null && dragSource != col) {
                int fromIndex = dataGrid.getChildren().indexOf(dragSource);
                int toIndex   = dataGrid.getChildren().indexOf(col);
                if (fromIndex >= 0 && toIndex >= 0) {
                    dataGrid.getChildren().remove(dragSource);
                    dataGrid.getChildren().add(toIndex, dragSource);
                }
                dragSource.setOpacity(1.0);
                col.setStyle(collapsedColStyle());
                dragSource = null;
            }
            e.consume();
        });

        return col;
    }

    public void addSatelliteColumn(String title) {
        VBox col = makeSatelliteColumn(title);
        dataGrid.getChildren().add(col);
    }

    public void updateSatelliteData(int index, String[][] keyValuePairs) {
        // TODO: implement live data updates once data layer exists
    }

    private String expandedColStyle() {
        return "-fx-border-color: #444466; -fx-border-width: 1; -fx-border-radius: 4; " +
               "-fx-background-color: #2a2a4a; -fx-background-radius: 4;";
    }

    private String collapsedColStyle() {
        return "-fx-border-color: #444466; -fx-border-width: 1; -fx-border-radius: 4; " +
               "-fx-background-color: #2a2a4a; -fx-background-radius: 4;";
    }

    private String fieldStyle() {
        return "-fx-background-color: #2a2a4a; -fx-text-fill: #c0c0e0; -fx-border-color: #444466; " +
               "-fx-border-radius: 2; -fx-background-radius: 2; -fx-font-size: 11px;";
    }

    private String controlButtonStyle() {
        return "-fx-background-color: #4a4a6a; -fx-text-fill: #c0c0e0; -fx-font-size: 11px; " +
               "-fx-padding: 3 10 3 10; -fx-background-radius: 3; -fx-cursor: hand;";
    }
}