package project.UI.Popups;

import java.util.Random;

import org.joml.Vector3f;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import project.StyleSheet;
import project.Math.Body;
import project.Math.Constant;
import project.Renderer.Renderer;
import project.Renderer.World.World;

public class BodyCreatorPopup extends Stage {
    private Renderer previewRenderer = new Renderer();
    private TextField nameField;
    private TextField massField;
    private TextField radiusField;
    private ComboBox<String> colorDropdown;
    private boolean confirmed = false;
    private StackPane preview = new StackPane();

    // Realistic planet/body randomiser ranges
    private static final double MASS_MIN = 1e23; // kg — small moon
    private static final double MASS_MAX = 2e27; // kg — super-Jupiter
    private static final double RADIUS_MIN = 1_000; // km — small rocky body
    private static final double RADIUS_MAX = 70_000; // km — large gas giant

    private final Random rand = new Random();

    private boolean massLocked = false;
    private boolean radiusLocked = false;
    private static int bodyCounter = 0;

    public BodyCreatorPopup(Stage owner, String themeStyle) {
        initOwner(owner);
        initModality(Modality.APPLICATION_MODAL);
        setTitle("Create new celestial body");
        setResizable(false);

        // Set preview background to black to prevent flickering on renderer update.
        preview.setStyle("-fx-background-color: #000000");

        Label scaleLabel = new Label("Scale (km)");
        scaleLabel.getStyleClass().add("key");

        // --- Form fields ---
        nameField = entryField(String.format("Body-%02d", bodyCounter + 1));
        massField = entryField("e.g. 5.972e24");
        radiusField = entryField("e.g. 6.371e6");

        colorDropdown = new ComboBox<>();
        colorDropdown.getItems().addAll("Red", "Blue", "Green", "Orange", "Purple", "White");
        colorDropdown.setValue("Red");
        colorDropdown.getStyleClass().add("combo-box");
        colorDropdown.setOnAction(e -> updatePreview());

        // --- Mass row: field + randomize + lock ---
        ToggleButton massLockBtn = lockButton();
        Button massRandBtn = randButton();
        massRandBtn.setOnAction(e -> {
            if (!massLocked)
                randomizeMass();

            updatePreview();
        });
        massLockBtn.setOnAction(e -> {
            massLocked = massLockBtn.isSelected();
            massLockBtn.setText(massLocked ? "\uD83D\uDD12" : "\uD83D\uDD13");
        });
        HBox massRow = new HBox(5, massField, massRandBtn, massLockBtn);
        massRow.setAlignment(Pos.CENTER_LEFT);

        // --- Radius row: field + randomize + lock ---
        ToggleButton radiusLockBtn = lockButton();
        Button radiusRandBtn = randButton();
        radiusRandBtn.setOnAction(e -> {
            if (!radiusLocked)
                randomizeRadius();

            updatePreview();
        });
        radiusLockBtn.setOnAction(e -> {
            radiusLocked = radiusLockBtn.isSelected();
            radiusLockBtn.setText(radiusLocked ? "\uD83D\uDD12" : "\uD83D\uDD13");
        });
        HBox radiusRow = new HBox(5, radiusField, radiusRandBtn, radiusLockBtn);
        radiusRow.setAlignment(Pos.CENTER_LEFT);

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(12);
        form.setPadding(new Insets(14));

        form.add(formLabel("Name :"), 0, 0);
        form.add(nameField, 1, 0);
        form.add(formLabel("Mass (kg) :"), 0, 1);
        form.add(massRow, 1, 1);
        form.add(formLabel("Radius (km) :"), 0, 2);
        form.add(radiusRow, 1, 2);
        form.add(formLabel("Color :"), 0, 3);
        form.add(colorDropdown, 1, 3);

        // --- Randomize All button ---
        Button randAllBtn = new Button("\u27F3  Randomize All");
        randAllBtn.getStyleClass().add("style-button");
        randAllBtn.setOnAction(e -> {
            if (!massLocked)
                randomizeMass();
            if (!radiusLocked)
                randomizeRadius();
            randomizeColor();

            updatePreview();
        });
        HBox randAllRow = new HBox(randAllBtn);
        randAllRow.setAlignment(Pos.CENTER_RIGHT);
        randAllRow.setPadding(new Insets(0, 14, 4, 14));

        // --- Cancel / Create buttons ---
        Button cancelBtn = new Button("CANCEL");
        Button createBtn = new Button("CREATE");
        cancelBtn.getStyleClass().add("style-button");
        createBtn.getStyleClass().add("style-button");

        Label errorLabel = new Label("");
        errorLabel.getStyleClass().add("error-label");

        HBox buttons = new HBox(8, cancelBtn, createBtn);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.setPadding(new Insets(0, 14, 14, 14));

        cancelBtn.setOnAction(e -> close());
        createBtn.setOnAction(e -> {
            if (!validate(errorLabel))
                return;
            confirmed = true;
            close();
        });

        // Pre-populate with randomised defaults on open
        randomizeMass();
        randomizeRadius();
        randomizeColor();

        // Init preview
        updatePreview();

        previewRenderer.getViewport().getGLCanvas().setPrefSize(200, 200);
        preview.getChildren().add(previewRenderer.getViewport().getGLCanvas());

        VBox formCol = new VBox(form, randAllRow, errorLabel, buttons);

        // --- Left = form, Right = preview ---
        HBox content = new HBox(10, formCol, preview);
        content.setPadding(new Insets(10));
        content.getStyleClass().add("small-pane");

        Label titleLabel = new Label("Create new celestial body");
        titleLabel.getStyleClass().add("subheading");

        VBox root = new VBox(titleLabel, content);
        root.getStyleClass().add("small-pane");
        root.setStyle(themeStyle);

        Scene scene = new Scene(root, 600, 400, true);
        scene.getStylesheets().add(new StyleSheet().styleSheet);

        setScene(scene);
        Platform.runLater(() -> root.requestFocus());
    }

    private void updatePreview() {
        // TODO: add distance to sun and eccentricity
        Body previewBody = new Body(getBodyName(), getBodyMass(), getBodyRadius(),
                Constant.EARTH_ORBIT_SEMIMAJOR_AXIS, Constant.EARTH_ORBIT_ECCENTRICITY);
        Color color = getBodyColor();

        previewRenderer.setWorld(new World(previewBody, new Vector3f((float) color.getRed(), (float) color.getGreen(), (float) color.getBlue())));
        previewRenderer.setFocusObject(previewBody.getName());
    }

    // Log-uniform random — appropriate for values spanning many orders of magnitude
    private double randomLog(double min, double max) {
        double logMin = Math.log10(min);
        double logMax = Math.log10(max);
        return Math.pow(10, logMin + rand.nextDouble() * (logMax - logMin));
    }

    private void randomizeMass() {
        massField.setText(String.format("%.3e", randomLog(MASS_MIN, MASS_MAX)));
    }

    private void randomizeRadius() {
        radiusField.setText(String.format("%.0f", randomLog(RADIUS_MIN, RADIUS_MAX)));
    }

    private void randomizeColor() {
        var items = colorDropdown.getItems();
        colorDropdown.setValue(items.get(rand.nextInt(items.size())));
        updatePreviewColor();
    }

    private Button randButton() {
        Button btn = new Button("\u27F3");
        btn.getStyleClass().add("icon-button");
        btn.setMinWidth(28);
        btn.setPrefWidth(28);
        return btn;
    }

    private ToggleButton lockButton() {
        ToggleButton btn = new ToggleButton("\uD83D\uDD13"); // 🔓
        btn.getStyleClass().add("lock-button");
        btn.setMinWidth(28);
        btn.setPrefWidth(28);
        return btn;
    }

    private void updatePreviewColor() {
        // previewCircle.setFill(getBodyColor());
    }

    private boolean validate(Label errorLabel) {
        if (massField.getText().isBlank()) {
            errorLabel.setText("Mass is required.");
            return false;
        }
        if (radiusField.getText().isBlank()) {
            errorLabel.setText("Radius is required.");
            return false;
        }
        try {
            Double.parseDouble(massField.getText());
        } catch (NumberFormatException e) {
            errorLabel.setText("Mass must be a number.");
            return false;
        }
        try {
            Double.parseDouble(radiusField.getText());
        } catch (NumberFormatException e) {
            errorLabel.setText("Radius must be a number.");
            return false;
        }
        return true;
    }

    public boolean wasConfirmed() {
        return confirmed;
    }

    public String getBodyName() {
        String n = nameField.getText().trim();
        return n.isEmpty() ? String.format("Body-%02d", ++bodyCounter) : n;
    }

    public double getBodyMass() {
        return Double.parseDouble(massField.getText());
    }

    public double getBodyRadius() {
        return Double.parseDouble(radiusField.getText());
    }

    public Color getBodyColor() {
        return switch (colorDropdown.getValue()) {
            case "Blue" -> Color.web("#39506a"); // Dark muted blue
            case "Green" -> Color.web("#355c3a"); // Dark muted green
            case "Orange" -> Color.web("#7a5a2e"); // Dark muted orange
            case "Purple" -> Color.web("#4b4063"); // Dark muted purple
            case "White" -> Color.web("#b0b0b0"); // Darker soft white
            default -> Color.web("#7a4a36"); // Dark muted orange-red
        };
    }

    private TextField entryField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setPrefWidth(130);
        tf.getStyleClass().add("field");
        return tf;
    }

    private Label formLabel(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("key-label");
        return l;
    }
}