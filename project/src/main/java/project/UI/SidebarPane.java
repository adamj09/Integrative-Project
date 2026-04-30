
package project.UI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import project.SimulationPool;
import project.Presets.PresetConfiguration;
import project.Presets.PresetConfiguration.BodyPreset;
import project.Presets.PresetConfiguration.SatellitePreset;
import project.Presets.WorldConfiguration;
import project.UI.Popups.BodyCreatorPopup;
import project.UI.Popups.SatelliteCreatorPopup;

/**
 * Class that handles lists of loaded celestial bodies and satellites within the user interface.
 * 
 * @author Ryan Lau
 * @author Adam Johnston
 */
public class SidebarPane extends VBox {
    private SimulationPool pool;

    private Button celestialTab;
    private Button satellitesTab;

    /**
     * The VBox containing all Body cards.
     */
    private VBox bodyListVBox;

    /**
     * A HashMap containing all body cards, with the body's name as a key and VBox
     * (the body's card) as the value.
     * This is useful for removing cards from the bodyListVBox VBox.
     */
    private HashMap<String, VBox> bodyCards = new HashMap<>();

    /**
     * The lists of satellites, keyed by body name.
     * Contains VBoxes as JavaFX children:
     * @see satelliteListVBoxes
     */
    private HashMap<String, VBox> satelliteLists = new HashMap<>();

        /**
     * The VBoxes each containing a list of satellites for each Body.
     * The key is the Body's name, the VBox is the list itself (analogous to the
     * single bodyListVBox variable).
     */
    private HashMap<String, VBox> satelliteListVBoxes = new HashMap<>();

    /**
     * 2D HashMap with all satellite cards. The first key corresponds to the Body,
     * the second to the particular Satellite belonging to that Body. The value of
     * type VBox holds the Satellite's card that corresponds to the address given
     * via the two keys.
     */
    private HashMap<String, HashMap<String, VBox>> satelliteCards = new HashMap<>();

    /**
     * Scroll pane for the bodies tab.
     */
    private ScrollPane bodyScroll;

    /**
     * Holds each tab's content.
     * 
     */
    private StackPane contentArea;

    /**
     * The BottomPane (used to display live satellite data).
     */
    private BottomPane bottom;

    /**
     * Track entries so we can wire adding/removing bodies and satellites.
     */
    private HashMap<String, BodyPreset> bodyEntries = new HashMap<>();
    private HashMap<String, HashMap<String, SatellitePreset>> satelliteEntries = new HashMap<>();

    /**
     * Track focused card (only one at a time)
     */
    private String focusedObjectName = null;
    private String focusedNameToRestore = null;
    private List<Button> allFocusButtons = new ArrayList<>();

    /**
     * Track selected celestial body (only one at a time)
     */
    private String selectedBody = ""; // Name of the selected body
    private Button selectedBodyToggle = null;
    private Label selectedBodyName = null;
    private Button selectedBodyFocus = null;
    private VBox selectedBodyCard = null;

    /**
     * HashMap containing every body's toggle button, keyed by body name.
     */
    private final HashMap<String, Button> bodyToggleButtons = new HashMap<>();


    /**
     * Creates a new SidebarPane.
     * 
     * @param bottom the BottomPane this SidebarPane will modify.
     * @param pool the SimulationPool this SidebarPane will command.
     */
    public SidebarPane(BottomPane bottom, SimulationPool pool) {
        this.pool = pool;
        this.bottom = bottom;

        setPrefWidth(300);
        setMinWidth(250);
        getStyleClass().add("side-pane");

        // Add all contents to this pane.
        getChildren().addAll(setUpTabs(), contentArea);

        // Request focus on the pane when mouse is clicked on it (allows for user to
        // switch between UI and simulation camera)
        this.setOnMouseClicked(_ -> this.requestFocus());
    }

    /**
     * Sets up the tabs to be used in this SideBarPane.
     * 
     * @return the tabs that were set up.
     */
    private HBox setUpTabs() {
        // Set up tab buttons
        celestialTab = tabButton("Celestial body");
        satellitesTab = tabButton("Satellites");

        HBox tabs = new HBox(celestialTab, satellitesTab);
        tabs.getStyleClass().add("tabs");

        // Set up Body list
        bodyListVBox = new VBox(4);
        bodyListVBox.setPadding(new Insets(6));

        bodyScroll = new ScrollPane(bodyListVBox);
        bodyScroll.setFitToWidth(true);
        bodyScroll.getStyleClass().add("scroll");
        VBox.setVgrow(bodyScroll, Priority.ALWAYS);

        VBox bodyView = new VBox(bodyScroll);
        VBox.setVgrow(bodyView, Priority.ALWAYS);

        VBox satelliteView = new VBox();
        VBox.setVgrow(satelliteView, Priority.ALWAYS);

        // Content area is a stackPane that switches between body/satellite view
        contentArea = new StackPane(bodyView, satelliteView);
        satelliteView.setVisible(false);
        VBox.setVgrow(contentArea, Priority.ALWAYS);

        // Set up tab switching
        celestialTab.setOnAction(e -> {
            contentArea.getChildren().get(0).setVisible(true);
            contentArea.getChildren().get(1).setVisible(false);
            setTabActive(celestialTab);
            setTabInactive(satellitesTab);
        });
        satellitesTab.setOnAction(e -> {
            contentArea.getChildren().get(0).setVisible(false);
            contentArea.getChildren().get(1).setVisible(true);
            setTabInactive(celestialTab);
            setTabActive(satellitesTab);
        });

        // Set celestial body tab as default active tab.
        setTabActive(celestialTab);
        setTabInactive(satellitesTab);

        return tabs;
    }

    /**
     * Open a new body builder pop-up window.
     * 
     * @param owner      the root stage of the JavaFX application.
     * @param themeStyle the theme to be used when rendering the body builder pop-up
     *                   window.
     */
    public void openNewBodyPopup(Stage owner, String themeStyle) {
        BodyCreatorPopup popup = new BodyCreatorPopup(owner, this, themeStyle, pool);
        popup.showAndWait();
        if (popup.wasConfirmed()) {
            addBodyCard(popup.getBodyName(), popup.getBodyColor(), false,
                    popup.getBodyMass(), popup.getBodyRadius());
        }
    }

    /**
     * Open a new satellite builder pop-up window.
     * 
     * @param owner      the root stage of the JavaFX application.
     * @param themeStyle the theme to be used when rendering the satellite builder
     *                   pop-up window.
     */
    public void openNewSatellitePopup(Stage owner, String themeStyle) {
        SatelliteCreatorPopup popup = new SatelliteCreatorPopup(owner, this, themeStyle, pool);
        popup.showAndWait();
        if (popup.wasConfirmed()) {
            addSatelliteCard(selectedBody, popup.getSatelliteName(), popup.getSatelliteColor());
        }
    }

    /**
     * Adds a new body card.
     * 
     * @param name     name of the body.
     * @param color    colour of the body.
     * @param isPreset boolean that determines whether this body is a preset.
     * @param mass     mass of the body.
     * @param radius   radius of the body.
     */
    public void addBodyCard(String name, Color color, boolean isPreset, double mass, double radius) {
        bodyEntries.put(name, new BodyPreset(name, color, isPreset, mass, radius));

        VBox card = buildBodyCard(name, color);

        // Set the selectedBodyCard ref once card is created
        if (selectedBodyCard == null) {
            selectedBodyCard = card;
        }

        bodyListVBox.getChildren().add(card);
        bodyCards.put(name, card);

        // Auto-focus if this card matches the name being restored
        if (focusedNameToRestore != null && focusedNameToRestore.equals(name) && name.equals(selectedBody)) {
            pool.getRenderer().setFocusObject(name);
        }
    }

    /**
     * Creates a new body card and sets up its buttons' event handlers.
     * 
     * @param name  name of the body.
     * @param color colour of the body.
     * @return the new body's card.
     */
    private VBox buildBodyCard(String name, Color color) {
        // Set up the top row: circle, name, and focus indicator
        Circle circle = new Circle(22, color);

        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("body");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox topRow = new HBox(8, circle, nameLabel, spacer);
        topRow.setAlignment(Pos.CENTER_LEFT);

        // Set up bottom row: Selected/Select and Focus buttons.
        Button toggleButton = new Button("Selected");
        toggleButton.getStyleClass().add("card-button-selected");

        // Selected on start
        toggleButton.setText("Selected");
        toggleButton.getStyleClass().set(0, "card-button-selected");

        Button focusButton = new Button("Focus");
        focusButton.getStyleClass().add("card-button-focus");
        allFocusButtons.add(focusButton);

        HBox buttonRow = new HBox(6, toggleButton, focusButton);
        buttonRow.setAlignment(Pos.CENTER_LEFT);

        // Stack rows atop another to create the card.
        VBox card = new VBox(6, topRow, buttonRow);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(10));
        card.getStyleClass().add("card");

        focusButton.setOnAction(_ -> pool.getRenderer().setFocusObject(selectedBody));
        toggleButton.setOnAction(_ -> toggleBodySelection(circle, nameLabel, toggleButton, focusButton, card));

        // First body to be added; auto-select it.
        if (selectedBody.isEmpty()) {
            createSatelliteList(name);
            celestialTab.fire();
            toggleButton.fire();

            pool.runWorld(name);

            return card;
        }

        createSatelliteList(name);
        celestialTab.fire();

        toggleButton.setText("Select");
        toggleButton.getStyleClass().set(0, "card-button-select");
        focusButton.setDisable(true);
        circle.setOpacity(0.4);
        nameLabel.setOpacity(0.5);

        return card;
    }

    /**
     * Toggles the selection of a given body.
     * 
     * @param circle       the circle that visually represents the body in the UI.
     * @param nameLabel    the label indicating the body's name.
     * @param toggleButton the button that toggles selection of the body.
     * @param focusButton  the button that focuses the camera on the body.
     * @param card         the body's card.
     */
    private void toggleBodySelection(Circle circle, Label nameLabel, Button toggleButton,
            Button focusButton, VBox card) {

        // Already selected — do nothing, can't deselect
        if (nameLabel.getText().equals(selectedBody)) {
            return;
        }

        // Deselect the currently selected body before selecting this new one (if it
        // exists).
        if (selectedBodyToggle != null) {
            deselectBody(selectedBodyToggle, selectedBodyFocus, selectedBodyName, selectedBodyCard);
        }

        // Update toggle button
        toggleButton.setText("Selected");
        toggleButton.getStyleClass().set(0, "card-button-selected");

        // Update focus button
        focusButton.setDisable(false);
        circle.setOpacity(1.0);
        nameLabel.setOpacity(1.0);

        // Track as the selected body
        selectedBody = nameLabel.getText();
        selectedBodyToggle = toggleButton;
        selectedBodyName = nameLabel;
        selectedBodyFocus = focusButton;
        selectedBodyCard = card;

        // Set the satellite view to the selected body's satellite list.
        // Doing this switches to the satellite tab, and so we need to immediately
        // switch back to the celestial body tab to prevent unwanted tab switching.
        contentArea.getChildren().set(1, satelliteLists.get(selectedBody));
        celestialTab.fire();

        // Run the newly selected celestial body.
        pool.runWorld(nameLabel.getText());
    }

    /**
     * Creates a new satellite list for a given body.
     * 
     * @param body the body for which a new list should be created.
     */
    private void createSatelliteList(String body) {
        VBox satelliteListBox = new VBox(4);
        satelliteListBox.setPadding(new Insets(6));

        ScrollPane satelliteScroll = new ScrollPane(satelliteListBox);
        satelliteScroll.setFitToWidth(true);
        satelliteScroll.getStyleClass().add("scroll");
        VBox.setVgrow(satelliteScroll, Priority.ALWAYS);

        VBox satelliteView = new VBox(satelliteScroll);
        VBox.setVgrow(satelliteView, Priority.ALWAYS);

        satelliteListVBoxes.put(body, satelliteListBox);
        satelliteLists.put(body, satelliteView);
        satelliteCards.put(body, new HashMap<>());
    }

    /**
     * Adds a new satellite card for the given body.
     * 
     * @param body  the body to which a new satellite card will be added.
     * @param name  the name of the satellite.
     * @param color the colour of the satellite.
     */
    private void addSatelliteCard(String body, String name, Color color) {
        // If the body does not have a list of satellite entries, create that list.
        if (!satelliteEntries.containsKey(body)) {
            satelliteEntries.put(body, new HashMap<>());
        }

        VBox card = buildSatelliteCard(body, name, color);

        satelliteEntries.get(body).put(name, new SatellitePreset(name, color));

        // Add card to list of cards (to keep track of adding/removing cards).
        satelliteCards.get(body).put(name, card);

        // Add card to VBox of card.
        satelliteListVBoxes.get(body).getChildren().add(card);

        // Update and move to satellite tab upon creation
        contentArea.getChildren().set(1, satelliteLists.get(body));
        satellitesTab.fire();

        // Auto-focus if this card matches the name being restored
        if (focusedNameToRestore != null && focusedNameToRestore.equals(name)) {
            pool.getRenderer().setFocusObject(name);
        }
    }

    /**
     * Creates a new satellite card and sets up its buttons' event handlers.
     * 
     * @param body  name of the body.
     * @param name  name of the satellite.
     * @param color colour of the satellite.
     * @return the new satellite's card.
     */
    private VBox buildSatelliteCard(String body, String name, Color color) {
        // Top row: circle, name, focus indicator
        Circle circle = new Circle(18, color);

        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("body");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Focus indicator (star when focused)
        Label focusIndicatorLabel = new Label("");
        focusIndicatorLabel.getStyleClass().add("focus-indicator");

        HBox topRow = new HBox(8, circle, nameLabel, spacer, focusIndicatorLabel);
        topRow.setAlignment(Pos.CENTER_LEFT);

        // Bottom row: Remove/Add, Focus, and View Data
        Button toggleButton = new Button("Remove");
        toggleButton.getStyleClass().add("card-button-remove");

        Button focusButton = new Button("Focus");
        focusButton.getStyleClass().add("card-button-focus");
        allFocusButtons.add(focusButton);

        Button viewDataButton = new Button("View Data");
        viewDataButton.getStyleClass().add("card-button-focus");
        viewDataButton.setOnAction(e -> bottom.selectSatelliteForView(body, name));

        focusButton.setOnAction(e -> pool.getRenderer().setFocusObject(name));

        toggleButton.setOnAction(e -> removeSatellite(name));
        bodyToggleButtons.put(name, toggleButton);

        HBox buttonRow = new HBox(6, toggleButton, focusButton, viewDataButton);
        buttonRow.setAlignment(Pos.CENTER_LEFT);

        VBox card = new VBox(6, topRow, buttonRow);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(10));
        card.getStyleClass().add("card");

        return card;
    }

    /**
     * Removes a given satellite from the currently selected body.
     * 
     * @param name name of the satellite to remove.
     */
    public void removeSatellite(String name) {
        satelliteEntries.get(selectedBody).remove(name);

        // Remove the satellite card.
        VBox vbox = (VBox) contentArea.getChildren().get(1);
        ScrollPane scrollPane = (ScrollPane) vbox.getChildren().get(0);
        VBox cards = (VBox) scrollPane.getContent();
        cards.getChildren().remove(satelliteCards.get(selectedBody).get(name));

        bottom.selectSatelliteForView(selectedBody, "");

        pool.getRenderer().setFocusObject(selectedBody);
        pool.getCurrentWorld().removeSatellite(name);
    }

    /**
     * Removes a given body from the sidebar.
     * Currently, this is only called when loading a new body that overwrites the
     * one we'd like to remove.
     * 
     * @param bodyName body to remove.
     */
    public void removeBody(String bodyName) {
        // First stop the world if the body to remove is currently selected.
        if (bodyName.equals(selectedBody)) {
            selectedBody = "";

            pool.stopWorld();
        }

        bodyEntries.remove(bodyName);
        satelliteEntries.remove(bodyName);
        satelliteLists.remove(bodyName);
        satelliteListVBoxes.remove(bodyName);

        VBox satelliteListBox = new VBox(4);
        satelliteListBox.setPadding(new Insets(6));

        ScrollPane satelliteScroll = new ScrollPane(satelliteListBox);
        satelliteScroll.setFitToWidth(true);
        satelliteScroll.getStyleClass().add("scroll");
        VBox.setVgrow(satelliteScroll, Priority.ALWAYS);

        VBox satelliteView = new VBox(satelliteScroll);
        VBox.setVgrow(satelliteView, Priority.ALWAYS);

        contentArea.getChildren().set(1, satelliteView);

        bodyListVBox.getChildren().remove(bodyCards.get(bodyName));
    }

    /**
     * Deselects a given body.
     * 
     * @param toggleButton button for toggling selection of the body.
     * @param focusButton  button for focusing on the body.
     * @param nameLabel    label containing the body's name.
     * @param card         the body's card.
     */
    private void deselectBody(Button toggleButton,
            Button focusButton, Label nameLabel, VBox card) {
        toggleButton.setText("Select");
        toggleButton.getStyleClass().set(0, "card-button-select");
        focusButton.setDisable(true);
        nameLabel.setOpacity(0.5);
    }

    /**
     * Selects a body with the given name if it exists.
     * 
     * @param name the name of the body to select.
     */
    public void selectBody(String name) {
        Button toggle = bodyToggleButtons.get(name);
        if (toggle != null) toggle.fire();
    }

    /**
     * Packs selected body's data into a PresetConfiguration object for saving.
     * 
     * @return a new PresetConfiguration with the currently selected body's data.
     */
    public PresetConfiguration toPresetConfiguration() {
        if (selectedBody.isEmpty()) {
            return null;
        }

        HashMap<String, SatellitePreset> entries = new HashMap<>();
        if (satelliteEntries.containsKey(selectedBody)) {
            entries = satelliteEntries.get(selectedBody);
        }

        return new PresetConfiguration(bodyEntries.get(selectedBody), entries,
                bottom.toPresetState());
    }

    /**
     * Applies a PresetConfiguration to this sidebar; adding a new body card with
     * the information found in the PresetConfiguration object.
     * 
     * @param configuration the PresetConfiguration to apply.
     */
    public void applyPresetConfiguration(PresetConfiguration configuration) {

        bottom.applyPresetState(configuration.getBottomPanePreset());

        BodyPreset body = configuration.getBody();
        addBodyCard(body.name(), body.color(), body.preset(), body.mass(), body.radius());

        for (Map.Entry<String, SatellitePreset> satellite : configuration.getSatellites().entrySet()) {
            addSatelliteCard(body.name(), satellite.getValue().name(), satellite.getValue().color());
        }
    }

    /**
     * Applies a WorldConfiguration to this sidebar; adding a new body card and its
     * corresponding satellite cards with the information found in the
     * WorldConfiguration object.
     * 
     * @param config the WorldConfiguration to apply.
     */
    public void applyWorldConfiguration(WorldConfiguration config) {
        // Set the focused name to restore before adding cards
        focusedNameToRestore = config.getFocusedObjectName();

        if (config.getUi() != null) {
            bottom.applyPresetState(new PresetConfiguration.BottomPanePreset(
                    config.getUi().specificTime, config.getUi().timescale, config.getUi().running));
        }

        // Add body cards from sidebar data (source of truth for all UI bodies)
        if (config.getBody() != null) {
            WorldConfiguration.SidebarBody sideBarBody = config.getSidebarBody();
            Color bodyColor = sideBarBody.colorHex != null ? Color.web(sideBarBody.colorHex) : Color.RED;
            addBodyCard(sideBarBody.name, bodyColor, sideBarBody.preset, sideBarBody.mass, sideBarBody.radius);
        }

        // Add satellite cards — uses SatelliteConfig.active as source of truth
        if (config.getSatellites() != null) {
            for (WorldConfiguration.SatelliteConfig sat : config.getSatellites()) {
                org.joml.Vector3f c = sat.color;
                Color satColor = c != null ? Color.color(
                        Math.min(1, Math.max(0, c.x)),
                        Math.min(1, Math.max(0, c.y)),
                        Math.min(1, Math.max(0, c.z))) : Color.RED;
                // Use sidebar color if available
                if (config.getSidebarSatellites() != null) {
                    for (WorldConfiguration.SidebarSatellite ss : config.getSidebarSatellites()) {
                        if (ss.name != null && ss.name.equals(sat.name) && ss.colorHex != null) {
                            satColor = Color.web(ss.colorHex);
                            break;
                        }
                    }
                }
                addSatelliteCard(config.getBody().name, sat.name, satColor);
            }
        }

        // Reset satellite tab to the list of the selected body (since loading
        // satellites automatically sets the satellite tab to contain those loaded
        // satellites, which conflicts with the satellites belonging to the selected
        // body.)
        contentArea.getChildren().set(1, satelliteLists.get(selectedBody));

        focusedNameToRestore = null;
    }

    /**
     * Helper method for creating a new tab button.
     * 
     * @param text the name of the tab.
     * @return a new tab Button.
     */
    private Button tabButton(String name) {
        Button button = new Button(name);
        button.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(button, Priority.ALWAYS);
        button.setPadding(new Insets(6));
        return button;
    }

    /**
     * Sets the given tab to active state.
     * 
     * @param button the tab button to set to active.
     */
    private void setTabActive(Button button) {
        button.getStyleClass().set(0, "tab-active");
    }

    /**
     * Sets the given tab to inactive state.
     * 
     * @param button the tab button to set to inactive.
     */
    private void setTabInactive(Button button) {
        button.getStyleClass().set(0, "tab-inactive");
    }

    /**
     * @return the currently selected body.
     */
    public String getSelectedBody() {
        return selectedBody;
    }

    /**
     * @return the currently focused object's name.
     */
    public String getFocusedObjectName() {
        return focusedObjectName;
    }

    /**
     * Returns all satellite entries for the corresponding body.
     * 
     * @param bodyName name of the body.
     * @return satellite entries.
     */
    public HashMap<String, SatellitePreset> getSatelliteEntries(String bodyName) {
        return satelliteEntries.get(bodyName);
    }

    /**
     * @return All body entries in this SidebarPane.
     */
    public HashMap<String, BodyPreset> getBodyEntries() {
        return bodyEntries;
    }
}