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

public class BodyCreatorPopup extends Stage {

    private TextField nameField;
    private TextField massField;
    private TextField radiusField;
    private ComboBox<String> colorDropdown;
    private boolean confirmed = false;

    private Circle previewCircle;

    public BodyCreatorPopup(Stage owner) {
        initOwner(owner);
        initModality(Modality.APPLICATION_MODAL);
        setTitle("Create new celestial body");
        setResizable(false);

        // --- 3D Preview ---
        previewCircle = new Circle(40, Color.TOMATO);
        StackPane preview = new StackPane(previewCircle);
        preview.setStyle("-fx-background-color: #12121f; -fx-border-color: #444466; -fx-border-width: 1;");
        preview.setPrefSize(200, 160);

        Label scaleLabel = new Label("Scale (km)");
        scaleLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #8888bb;");

        VBox previewBox = new VBox(4, preview, scaleLabel);
        previewBox.setAlignment(Pos.TOP_RIGHT);

        // --- Form fields ---
        nameField   = entryField("e.g. Earth");
        massField   = entryField("e.g. 5.972e24");
        radiusField = entryField("e.g. 6.371e6");

        colorDropdown = new ComboBox<>();
        colorDropdown.getItems().addAll("Red", "Blue", "Green", "Orange", "Purple", "White");
        colorDropdown.setValue("Red");
        colorDropdown.setStyle(
            "-fx-background-color: #4a4a6a; -fx-text-fill: #c0c0e0; -fx-font-size: 12px;"
        );
        colorDropdown.setOnAction(e -> updatePreviewColor());

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(12);
        form.setPadding(new Insets(14));

        form.add(formLabel("Name :"),        0, 0); form.add(nameField,     1, 0);
        form.add(formLabel("Mass (kg) :"),   0, 1); form.add(massField,     1, 1);
        form.add(formLabel("Radius (km) :"), 0, 2); form.add(radiusField,   1, 2);
        form.add(formLabel("Color :"),       0, 3); form.add(colorDropdown, 1, 3);

        // --- Buttons ---
        Button cancelBtn = new Button("CANCEL");
        Button createBtn = new Button("CREATE");
        cancelBtn.setStyle(btnStyle());
        createBtn.setStyle(btnStyle());

        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: #ff6666; -fx-font-size: 11px;");

        HBox buttons = new HBox(8, cancelBtn, createBtn);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.setPadding(new Insets(0, 14, 14, 14));

        cancelBtn.setOnAction(e -> close());
        createBtn.setOnAction(e -> {
            if (!validate(errorLabel)) return;
            confirmed = true;
            close();
        });

        VBox formCol = new VBox(form, errorLabel, buttons);

        // --- Left = form, Right = preview ---
        HBox content = new HBox(10, formCol, previewBox);
        content.setPadding(new Insets(10));
        content.setStyle("-fx-background-color: #2a2a4a; -fx-border-color: #444466; -fx-border-width: 1;");

        Label titleLabel = new Label("Create new celestial body");
        titleLabel.setStyle(
            "-fx-font-size: 13px; -fx-font-weight: bold; " +
            "-fx-padding: 10 14 6 14; -fx-text-fill: #c0c0e0;"
        );

        VBox root = new VBox(titleLabel, content);
        root.setStyle("-fx-background-color: #1a1a2e;");

        setScene(new Scene(root));
    }

    private void updatePreviewColor() {
        previewCircle.setFill(getBodyColor());
    }

    private boolean validate(Label errorLabel) {
        if (nameField.getText().isBlank())   { errorLabel.setText("Name is required.");   return false; }
        if (massField.getText().isBlank())   { errorLabel.setText("Mass is required.");   return false; }
        if (radiusField.getText().isBlank()) { errorLabel.setText("Radius is required."); return false; }
        try { Double.parseDouble(massField.getText()); }
        catch (NumberFormatException e) { errorLabel.setText("Mass must be a number."); return false; }
        try { Double.parseDouble(radiusField.getText()); }
        catch (NumberFormatException e) { errorLabel.setText("Radius must be a number."); return false; }
        return true;
    }

    public boolean wasConfirmed() { return confirmed; }

    public String getBodyName() {
        String n = nameField.getText().trim();
        return n.isEmpty() ? "Body-" + colorDropdown.getValue() : n;
    }

    public double getBodyMass()   { return Double.parseDouble(massField.getText()); }
    public double getBodyRadius() { return Double.parseDouble(radiusField.getText()); }

    public Color getBodyColor() {
        return switch (colorDropdown.getValue()) {
            case "Blue"   -> Color.CORNFLOWERBLUE;
            case "Green"  -> Color.MEDIUMSEAGREEN;
            case "Orange" -> Color.DARKORANGE;
            case "Purple" -> Color.MEDIUMPURPLE;
            case "White"  -> Color.LIGHTGRAY;
            default       -> Color.TOMATO;
        };
    }

    private TextField entryField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setPrefWidth(130);
        tf.setStyle(
            "-fx-background-color: #12121f; -fx-text-fill: #c0c0e0; " +
            "-fx-prompt-text-fill: #555577; -fx-border-color: #444466; " +
            "-fx-border-radius: 3; -fx-background-radius: 3; -fx-font-size: 12px;"
        );
        return tf;
    }

    private Label formLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size: 12px; -fx-text-fill: #8888bb;");
        return l;
    }

    private String btnStyle() {
        return "-fx-background-color: #4a4a6a; -fx-text-fill: #c0c0e0; -fx-font-size: 12px; " +
               "-fx-padding: 4 14 4 14; -fx-background-radius: 3; -fx-cursor: hand;";
    }
}