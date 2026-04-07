package project.UI.Popups;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import project.StyleSheet;

public class SatelliteCreatorPopup extends Stage {

    private TextField nameField;
    private TextField massField;
    private TextField initialSpeedField;
    private ComboBox<String> colorDropdown;
    private TextField posXField, posYField, posZField;
    private TextField rotIField, rotLField, rotWField;
    private boolean confirmed = false;

    public SatelliteCreatorPopup(Stage owner) {
        initOwner(owner);
        initModality(Modality.APPLICATION_MODAL);
        setTitle("Create new satellite");
        setResizable(false);

        // vvv TEMPORARY vvv

        // TODO: make actual 3D preview
        // --- 3D Preview ---
        Circle planet = new Circle(35, Color.web("#7a4a36")); // Dark muted orange
        Circle satellite = new Circle(6, Color.CORNFLOWERBLUE);
        satellite.setTranslateX(60);
        StackPane previewPane = new StackPane(planet, satellite);
        previewPane.setStyle("-fx-background-color: #12121f; -fx-border-color: #444466; -fx-border-width: 1;");
        previewPane.setPrefSize(420, 150);

        // ^^^ TEMPORARY ^^^

        Label scaleLabel = new Label("Scale (km)");
        scaleLabel.getStyleClass().add("key-label");
        ;

        VBox previewBox = new VBox(4, previewPane, scaleLabel);
        previewBox.setAlignment(Pos.BOTTOM_RIGHT);
        previewBox.setPadding(new Insets(10, 10, 0, 10));

        // --- Left: name, mass, color, speed ---
        nameField = entryField("e.g. Sat-01");
        massField = entryField("e.g. 1000");
        initialSpeedField = entryField("m/s");

        colorDropdown = new ComboBox<>();
        colorDropdown.getItems().addAll("Blue", "Red", "Green", "Orange", "Purple", "White");
        colorDropdown.setValue("Blue");
        colorDropdown.getStyleClass().add("combo-box");

        GridPane leftForm = new GridPane();
        leftForm.setHgap(10);
        leftForm.setVgap(8);
        leftForm.add(formLabel("Name :"), 0, 0);
        leftForm.add(nameField, 1, 0);
        leftForm.add(formLabel("Mass (kg) :"), 0, 1);
        leftForm.add(massField, 1, 1);
        leftForm.add(formLabel("Color :"), 0, 2);
        leftForm.add(colorDropdown, 1, 2);
        leftForm.add(formLabel("Initial speed:"), 0, 3);
        leftForm.add(initialSpeedField, 1, 3);

        // --- Middle: Position ---
        posXField = entryField("0");
        posYField = entryField("0");
        posZField = entryField("0");
        GridPane posForm = new GridPane();
        posForm.setHgap(10);
        posForm.setVgap(8);
        posForm.add(formLabel("Position (x,y,z) (m):"), 0, 0, 2, 1);
        posForm.add(formLabel("x :"), 0, 1);
        posForm.add(posXField, 1, 1);
        posForm.add(formLabel("y :"), 0, 2);
        posForm.add(posYField, 1, 2);
        posForm.add(formLabel("z :"), 0, 3);
        posForm.add(posZField, 1, 3);

        // --- Right: Rotation ---
        rotIField = entryField("0");
        rotLField = entryField("0");
        rotWField = entryField("0");
        GridPane rotForm = new GridPane();
        rotForm.setHgap(10);
        rotForm.setVgap(8);
        rotForm.add(formLabel("Rotation (degrees):"), 0, 0, 2, 1);
        rotForm.add(formLabel("i :"), 0, 1);
        rotForm.add(rotIField, 1, 1);
        rotForm.add(formLabel("I :"), 0, 2);
        rotForm.add(rotLField, 1, 2);
        rotForm.add(formLabel("w :"), 0, 3);
        rotForm.add(rotWField, 1, 3);

        HBox formRow = new HBox(20, leftForm, posForm, rotForm);
        formRow.setPadding(new Insets(12, 14, 0, 14));

        Label errorLabel = new Label("");
        errorLabel.getStyleClass().add("error-label");
        ;

        Button cancelBtn = new Button("CANCEL");
        Button createBtn = new Button("CREATE");
        cancelBtn.getStyleClass().add("style-button");
        createBtn.getStyleClass().add("style-button");
        cancelBtn.setOnAction(e -> close());
        createBtn.setOnAction(e -> {
            if (!validate(errorLabel))
                return;
            confirmed = true;
            close();
        });

        HBox buttons = new HBox(8, cancelBtn, createBtn);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.setPadding(new Insets(8, 14, 14, 14));

        Label titleLabel = new Label("Create new satellite");
        titleLabel.getStyleClass().add("subheading");

        VBox root = new VBox(titleLabel, previewBox, formRow, errorLabel, buttons);
        root.getStyleClass().add("small-pane");
        ;

        Scene scene = new Scene(root);
        scene.getStylesheets().add(new StyleSheet().styleSheet);

        setScene(scene);
    }

    private boolean validate(Label errorLabel) {
        if (nameField.getText().isBlank()) {
            errorLabel.setText("Name is required.");
            return false;
        }
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

    public boolean wasConfirmed() {
        return confirmed;
    }

    public String getSatelliteName() {
        String n = nameField.getText().trim();
        return n.isEmpty() ? "Sat-" + colorDropdown.getValue() : n;
    }

    public double getSatelliteMass() {
        return Double.parseDouble(massField.getText());
    }

    public double getInitialSpeed() {
        return Double.parseDouble(initialSpeedField.getText());
    }

    public Color getSatelliteColor() {
        return switch (colorDropdown.getValue()) {
            case "Red" -> Color.web("#7a4a36"); // Dark muted orange-red
            case "Green" -> Color.web("#355c3a"); // Dark muted green
            case "Orange" -> Color.web("#7a5a2e"); // Dark muted orange
            case "Purple" -> Color.web("#4b4063"); // Dark muted purple
            case "White" -> Color.web("#b0b0b0"); // Darker soft white
            default -> Color.web("#39506a"); // Dark muted blue
        };
    }

    public double[] getPosition() {
        return new double[] { p(posXField), p(posYField), p(posZField) };
    }

    public double[] getRotation() {
        return new double[] { p(rotIField), p(rotLField), p(rotWField) };
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
