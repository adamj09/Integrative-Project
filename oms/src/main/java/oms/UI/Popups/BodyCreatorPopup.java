package oms.UI.Popups;

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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import oms.SimulationPool;
import oms.StyleSheet;
import oms.Physics.Body;
import oms.Physics.Constant;
import oms.Renderer.Renderer;
import oms.Renderer.World.World;
import oms.UI.SidebarPane;

/**
 * A pop-up window for creating new celestial bodies.
 * 
 * @author Ryan Lau
 * @author Adam Johnston
 */
public class BodyCreatorPopup extends Stage {
    /**
     * The SimulationPool to be modified by this pop-up window.
     */
    private SimulationPool pool;

    /**
     * The SidebarPane to be modified by this pop-up window.
     */
    private SidebarPane sideBar;

    /**
     * Preview variables.
     */
    private Renderer previewRenderer = new Renderer();
    private Body previewBody;
    private String worldName;

    /**
     * Form fields.
     */
    private TextField nameField;
    private TextField massField;
    private TextField radiusField;
    private ComboBox<String> colorDropdown;
    private boolean confirmed = false;
    private StackPane preview = new StackPane();

    /**
     * Realistic planet/body randomiser ranges.
     */
    private static final double MASS_MIN = 1e23; // kg — small moon
    private static final double MASS_MAX = 2e27; // kg — super-Jupiter
    private static final double RADIUS_MIN = 1_000; // km — small rocky body
    private static final double RADIUS_MAX = 70_000; // km — large gas giant

    /**
     * Randomizer.
     */
    private final Random rand = new Random();

    /**
     * Locks for randomizer (if true, will be randomized, untouched otherwise).
     */
    private boolean massLocked = false;
    private boolean radiusLocked = false;

    /**
     * Creates a new body creator pop-up window.
     * 
     * @param owner      the root stage of the JavaFX application.
     * @param sideBar    the SidebarPane to be modified by this pop-up window.
     * @param themeStyle the theme to be used when rendering this pop-up window.
     * @param pool       the simulation pool that will be modified by this pop-up
     *                   window.
     */
    public BodyCreatorPopup(Stage owner, SidebarPane sideBar, String themeStyle, SimulationPool pool) {
        this.sideBar = sideBar;
        this.pool = pool;
        initOwner(owner);
        initModality(Modality.APPLICATION_MODAL);
        setTitle("Create new celestial body");
        setResizable(false);

        // Set preview background to black to prevent flickering on renderer update.
        preview.setStyle("-fx-background-color: #000000");

        setUpFormControls();

        // Init preview
        setUpPreview();

        previewRenderer.getViewport().getGLCanvas().setPrefSize(200, 200);
        preview.getChildren().add(previewRenderer.getViewport().getGLCanvas());

        // --- Left = form, Right = preview ---
        HBox content = new HBox(10, setUpFormControls(), preview);
        content.setPadding(new Insets(10));
        content.getStyleClass().add("small-pane");

        Label titleLabel = new Label("Create new celestial body");
        titleLabel.getStyleClass().add("subheading");

        VBox root = new VBox(titleLabel, content);
        root.getStyleClass().add("small-pane");
        root.setStyle(themeStyle);

        Scene scene = new Scene(root, 600, 400, true);
        scene.getStylesheets().add(new StyleSheet().styleSheet);

        randomizeMass();
        randomizeRadius();
        randomizeColor();

        setScene(scene);
        Platform.runLater(() -> root.requestFocus());

        setOnCloseRequest(_ -> previewRenderer.getViewport().dispose());
    }

    /**
     * @return a new GridPane containing all text fields for celestial body
     *         variables.
     */
    private GridPane setUpForm() {
        Label scaleLabel = new Label("Scale (km)");
        scaleLabel.getStyleClass().add("key");

        // --- Form fields ---
        nameField = entryField("Body Name");
        massField = entryField("e.g. 5.972e24");
        radiusField = entryField("e.g. 6.371e6");

        colorDropdown = new ComboBox<>();
        colorDropdown.getItems().addAll("Red", "Blue", "Green", "Orange", "Purple", "White");
        colorDropdown.setValue("Red");
        colorDropdown.getStyleClass().add("combo-box");
        colorDropdown.setOnAction(e -> updatePreviewColor());

        // --- Mass row: field + randomize + lock ---
        ToggleButton massLockBtn = lockButton();
        Button massRandBtn = randButton();
        massRandBtn.setOnAction(e -> {
            if (!massLocked)
                randomizeMass();
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

            updatePreviewRadius();
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

        return form;
    }

    /**
     * @return a new VBox containing a GridPane with all text fields for body
     *         variables, and all controllers for the creator.
     */
    private VBox setUpFormControls() {
        // --- Randomize All button ---
        Button randAllBtn = new Button("\u27F3  Randomize All");
        randAllBtn.getStyleClass().add("style-button");
        randAllBtn.setOnAction(e -> {
            if (!massLocked)
                randomizeMass();
            if (!radiusLocked)
                randomizeRadius();
            randomizeColor();

            updatePreviewRadius();
            updatePreviewColor();
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
        errorLabel.setPadding(new Insets(0, 14, 4, 14));

        HBox buttons = new HBox(8, cancelBtn, createBtn);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.setPadding(new Insets(0, 14, 14, 14));

        cancelBtn.setOnAction(e -> {
            previewRenderer.getViewport().dispose();
            close();
        });
        createBtn.setOnAction(_ -> createBody(errorLabel));

        return new VBox(setUpForm(), randAllRow, errorLabel, buttons);
    }

    /**
     * Creates a new body using the values provided in the text fields.
     * 
     * @param errorLabel the label with which errors with be displayed.
     */
    private void createBody(Label errorLabel) {
        if (!validate(errorLabel))
            return;

        worldName = nameField.getText().trim();
        if (worldName.isEmpty()) {
            errorLabel.setText("You must provide a name for the celestial body!");
            return;
        }

        if (sideBar.getBodyEntries().containsKey(worldName)) {
            if (!UnsavedChangesPopup.confirm(
                    "A body with this name is already exists! Continue and overwrite that body's data with this one?")) {
                return;
            }
            sideBar.removeBody(worldName);
        }

        confirmed = true;

        // Create a world with this body.
        Color color = getBodyColor();
        pool.createWorld(worldName, new Body(worldName, getBodyMass(), getBodyRadius(),
                Constant.EARTH_ORBIT_SEMIMAJOR_AXIS, Constant.EARTH_ORBIT_ECCENTRICITY),
                new Vector3f((float) color.getRed(), (float) color.getGreen(), (float) color.getBlue()));

        previewRenderer.getViewport().dispose();
        close();
    }

    /**
     * Updates the colour of the body in the preview window.
     */
    private void updatePreviewColor() {
        Color color = getBodyColor();
        previewRenderer.getWorld().updateColor(previewBody.getName(),
                new Vector3f((float) color.getRed(), (float) color.getGreen(), (float) color.getBlue()));
    }

    /**
     * Updates the radius of the body in the preview window.
     */
    private void updatePreviewRadius() {
        previewRenderer.getWorld().updateRadius(previewBody.getName(), (float) getBodyRadius());
    }

    /**
     * Sets up the preview renderer.
     */
    private void setUpPreview() {
        // Note that the name, mass, and eccentricity remain the same (arbitrary), as
        // they don't affect the preview's appearance. The semi-major axis component is
        // not yet implemented.
        previewBody = new Body("preview", 20, getBodyRadius(),
                Constant.EARTH_ORBIT_SEMIMAJOR_AXIS, Constant.EARTH_ORBIT_ECCENTRICITY);
        Color color = getBodyColor();

        // Set up preview renderer.
        previewRenderer.setWorld(new World(previewBody,
                new Vector3f((float) color.getRed(), (float) color.getGreen(), (float) color.getBlue())));

        // Set previewBody as camera's focus.
        previewRenderer.setFocusObject(previewBody.getName());

        // Add update listeners.
        radiusField.textProperty().addListener(_ -> updatePreviewRadius());
        colorDropdown.setOnAction(_ -> updatePreviewColor());
    }

    /**
     * Log-uniform random — appropriate for values spanning many orders of magnitude
     */
    private double randomLog(double min, double max) {
        double logMin = Math.log10(min);
        double logMax = Math.log10(max);
        return Math.pow(10, logMin + rand.nextDouble() * (logMax - logMin));
    }

    /**
     * Randomizes the value of the mass text field.
     */
    private void randomizeMass() {
        massField.setText(String.format("%.3e", randomLog(MASS_MIN, MASS_MAX)));
    }

    /**
     * Randomizes the values of the radius text field.
     */
    private void randomizeRadius() {
        radiusField.setText(String.format("%.0f", randomLog(RADIUS_MIN, RADIUS_MAX)));
    }

    /**
     * Randomizes the colour of the colour text field.
     */
    private void randomizeColor() {
        var items = colorDropdown.getItems();
        colorDropdown.setValue(items.get(rand.nextInt(items.size())));
    }

    /**
     * @return a new randomizer button.
     */
    private Button randButton() {
        Button btn = new Button("\u27F3");
        btn.getStyleClass().add("icon-button");
        btn.setMinWidth(28);
        btn.setPrefWidth(28);
        return btn;
    }

    /**
     * @return a new lock button.
     */
    private ToggleButton lockButton() {
        ToggleButton btn = new ToggleButton("\uD83D\uDD13"); // 🔓
        btn.getStyleClass().add("lock-button");
        btn.setMinWidth(28);
        btn.setPrefWidth(28);
        return btn;
    }

    /**
     * @param errorLabel the label to which errors will be printed.
     * @return true if all values obtained from the text fields are valid, false
     *         otherwise.
     */
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
            double mass = Double.parseDouble(massField.getText());
            if (mass <= 0) {
                errorLabel.setText("Mass must be a positive number.");
                return false;
            }
        } catch (NumberFormatException e) {
            errorLabel.setText("Mass must be a number.");
            return false;
        }
        try {
            double radius = Double.parseDouble(radiusField.getText());

            if (radius <= 0) {
                errorLabel.setText("Radius must be a positive number.");
                return false;
            }
        } catch (NumberFormatException e) {
            errorLabel.setText("Radius must be a number.");
            return false;
        }
        return true;
    }

    /**
     * @return true if body creation was confirmed, false otherwise.
     */
    public boolean wasConfirmed() {
        return confirmed;
    }

    /**
     * @return the body's name.
     */
    public String getBodyName() {
        return worldName;
    }

    /**
     * @return the body's mass (kilograms) from its corresponding text field,
     *         Earth's mass if parsing is unsuccessful.
     */
    public double getBodyMass() {
        try {
            return Double.parseDouble(massField.getText());
        } catch (NumberFormatException ex) {
            return Constant.EARTH_DEFAULT_MASS;
        }
    }

    /**
     * @return the body's radius (kilometers) from its corresponding text field,
     *         Earth radius if parsing is unsuccessful.
     */
    public double getBodyRadius() {
        if (radiusField.getText().isEmpty()) {
            return Constant.EARTH_DEFAULT_RADIUS;
        }

        try {
            return Double.parseDouble(radiusField.getText());
        } catch (NumberFormatException ex) {
            return Constant.EARTH_DEFAULT_RADIUS;
        }
    }

    /**
     * @return the currently selected colour in the colour dropdown menu.
     */
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

    /**
     * Creates a new text field.
     * 
     * @param prompt the prompt to fill the text field with.
     * @return a new TextField object.
     */
    private TextField entryField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setPrefWidth(130);
        tf.getStyleClass().add("field");
        return tf;
    }

    /**
     * Creates a new label for the form.
     * 
     * @param text label's text.
     * @return a new Label object.
     */
    private Label formLabel(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("key-label");
        return l;
    }
}