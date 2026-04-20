
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

public class SidebarPane extends VBox {
    private SimulationPool pool;

    private final Button celestialTab;
    private final Button satellitesTab;

    private final VBox bodyListBox;
    private final VBox satelliteListBox;
    private final ScrollPane bodyScroll;
    private final ScrollPane satelliteScroll;

    private final StackPane contentArea;

    private final BottomPane bottom;

    // Track entries so we can wire delete/visualize
    private final HashMap<String, BodyPreset> bodyEntries = new HashMap<>();
    private final HashMap<String, HashMap<String, SatellitePreset>> satelliteEntries = new HashMap<>();
    private final HashMap<String, Boolean> satelliteActiveStates = new HashMap<>();

    private String selectedBody = "";

    // Track focused card (only one at a time)
    private VBox focusedCard = null;
    private Label focusedIndicator = null;
    private Button focusedButton = null;
    private String focusedItemName = null;
    private String focusedNameToRestore = null;
    private final List<Button> allFocusButtons = new ArrayList<>();

    // Track selected celestial body (only one at a time)
    private Button selectedBodyToggle = null;
    private Circle selectedBodyCircle = null;
    private Label selectedBodyName = null;
    private Circle selectedBodyIndicator = null;
    private Button selectedBodyFocus = null;
    private VBox selectedBodyCard = null;
    private double selectedBodyMass   = 5.972e24; // kg  — defaulting to Earth
    private double selectedBodyRadius = 6371.0;   // km
    private Color  selectedBodyColor  = Color.RED; // defaulting to Earth

    public SidebarPane(BottomPane bottom, SimulationPool pool) {
        this.pool = pool;
        this.bottom = bottom;
        setPrefWidth(300);
        setMinWidth(250);
        getStyleClass().add("side-pane");

        // --- Tab buttons ---
        celestialTab = tabButton("Celestial body");
        satellitesTab = tabButton("Satellites");

        HBox tabs = new HBox(0, celestialTab, satellitesTab);
        tabs.getStyleClass().add("tabs");

        // --- Body list ---
        bodyListBox = new VBox(4);
        bodyListBox.setPadding(new Insets(6));

        bodyScroll = new ScrollPane(bodyListBox);
        bodyScroll.setFitToWidth(true);
        bodyScroll.getStyleClass().add("scroll");
        VBox.setVgrow(bodyScroll, Priority.ALWAYS);

        VBox bodyView = new VBox(bodyScroll);
        VBox.setVgrow(bodyView, Priority.ALWAYS);

        // --- Satellite list ---
        satelliteListBox = new VBox(4);
        satelliteListBox.setPadding(new Insets(6));

        satelliteScroll = new ScrollPane(satelliteListBox);
        satelliteScroll.setFitToWidth(true);
        satelliteScroll.getStyleClass().add("scroll");
        VBox.setVgrow(satelliteScroll, Priority.ALWAYS);

        VBox satelliteView = new VBox(satelliteScroll);
        VBox.setVgrow(satelliteView, Priority.ALWAYS);

        // --- Content area switches between body/satellite view ---
        contentArea = new StackPane(bodyView, satelliteView);
        satelliteView.setVisible(false);
        VBox.setVgrow(contentArea, Priority.ALWAYS);

        // Tab switching
        celestialTab.setOnAction(e -> {
            bodyView.setVisible(true);
            satelliteView.setVisible(false);
            setTabActive(celestialTab);
            setTabInactive(satellitesTab);
        });
        satellitesTab.setOnAction(e -> {
            bodyView.setVisible(false);
            satelliteView.setVisible(true);
            setTabActive(satellitesTab);
            setTabInactive(celestialTab);
        });

        setTabActive(celestialTab);
        setTabInactive(satellitesTab);

        getChildren().addAll(tabs, contentArea);

        // Request focus on the pane when mouse is clicked on it (allows for user to
        // switch between controller UI and simulation camera)
        this.setOnMouseClicked(_ -> this.requestFocus());
    }

    public void openNewBodyPopup(Stage owner, String themeStyle) {
        BodyCreatorPopup popup = new BodyCreatorPopup(owner, themeStyle, pool);
        popup.showAndWait();
        if (popup.wasConfirmed()) {
            addBodyCard(popup.getBodyName(), popup.getBodyColor(), false,
                popup.getBodyMass(), popup.getBodyRadius());
            
        }
    }

    public void openNewSatellitePopup(Stage owner, String themeStyle) {
        SatelliteCreatorPopup popup = new SatelliteCreatorPopup(owner, themeStyle, pool);
        popup.showAndWait();
        if (popup.wasConfirmed()) {
            addSatelliteCard(selectedBody, popup.getSatelliteName(), popup.getSatelliteColor());
        }
    }

    private void clearFocus() {
        if (focusedIndicator != null) {
            focusedIndicator.setText("");
        }
        if (focusedButton != null) {
            focusedButton.setText("Focus");
        }
        focusedCard = null;
        focusedIndicator = null;
        focusedButton = null;
        focusedItemName = null;
    }

    private void addBodyCard(String name, Color color, boolean isPreset, double mass, double radius) {
        bodyEntries.put(name, new BodyPreset(name, color, isPreset, mass, radius));

        final double[] bodyProps = {mass, radius};
        final Color capturedColor  = color;

        // -- Top row: circle + name + active indicator + focus indicator --
        Circle circle = new Circle(22, color);

        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("body");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Focus indicator (star when focused)
        Label focusIndicatorLabel = new Label("");
        focusIndicatorLabel.getStyleClass().add("focus-indicator");

        HBox topRow = new HBox(8, circle, nameLabel, spacer, focusIndicatorLabel);
        topRow.setAlignment(Pos.CENTER_LEFT);

        // -- Bottom row: Selected/Select + Focus --
        Button toggleButton = new Button("Selected");
        toggleButton.getStyleClass().add("card-button-selected");

        // Selected on start
        toggleButton.setText("Selected");
        toggleButton.getStyleClass().set(0, "card-button-selected");

        Button focusButton = new Button("Focus");
        focusButton.getStyleClass().add("card-button-focus");
        allFocusButtons.add(focusButton);

        focusButton.setOnAction(e -> {
            if (!name.equals(selectedBody)) return;
            
            pool.getRenderer().setFocusObject(name);
        });

        toggleButton.setOnAction(e -> {
            VBox card = (VBox) toggleButton.getParent().getParent();
            if (name.equals(selectedBody)) {
                // Already selected — do nothing, can't deselect
                return;
            } else {
                deselectBody(selectedBodyIndicator, selectedBodyToggle,
                            selectedBodyFocus, selectedBodyCircle, selectedBodyName, selectedBodyCard);

                // Select this body
                selectedBody = name;
                toggleButton.setText("Selected");
                toggleButton.getStyleClass().set(0, "card-button-selected");
                focusButton.setDisable(false);
                circle.setOpacity(1.0);
                nameLabel.setOpacity(1.0);
                // Track as the selected body
                selectedBodyToggle = toggleButton;
                selectedBodyCircle = circle;
                selectedBodyName = nameLabel;
                selectedBodyFocus = focusButton;
                selectedBodyCard = card;
                selectedBodyMass   = bodyProps[0];
                selectedBodyRadius = bodyProps[1];
                selectedBodyColor  = capturedColor;

                // TODO: update the satellites list to only contain those of the selected body

                pool.runWorld(name);
            }
        });

        // New non-preset bodies start as unselected
        if (selectedBody.isEmpty() && !bodyEntries.isEmpty()) {
            // First selected body — auto-select it
            selectedBody = name;
            selectedBodyToggle = toggleButton;
            selectedBodyCircle = circle;
            selectedBodyName = nameLabel;
            selectedBodyFocus = focusButton;
            selectedBodyMass   = bodyProps[0];
            selectedBodyRadius = bodyProps[1];
            selectedBodyColor  = capturedColor;

            pool.runWorld(name);
        } else if (name.equals(selectedBody) && selectedBodyToggle != null) {
            deselectBody(selectedBodyIndicator, selectedBodyToggle,
                            selectedBodyFocus, selectedBodyCircle, selectedBodyName, selectedBodyCard);

            // Select this body
            selectedBody = name;

            toggleButton.setText("Selected");
            toggleButton.getStyleClass().set(0, "card-button-selected");
            focusButton.setDisable(false);
            circle.setOpacity(1.0);
            nameLabel.setOpacity(1.0);
            selectedBodyToggle = toggleButton;
            selectedBodyCircle = circle;
            selectedBodyName = nameLabel;
            selectedBodyFocus = focusButton;
            selectedBodyMass   = bodyProps[0];
            selectedBodyRadius = bodyProps[1];
            selectedBodyColor  = capturedColor;

        } else if (!name.equals(selectedBody)) {
            // Additional bodies start deselected
            toggleButton.setText("Select");
            toggleButton.getStyleClass().set(0, "card-button-select");
            focusButton.setDisable(true);
            circle.setOpacity(0.4);
            nameLabel.setOpacity(0.5);
        }

        HBox buttonRow = new HBox(6, toggleButton, focusButton);
        buttonRow.setAlignment(Pos.CENTER_LEFT);

        VBox card = new VBox(6, topRow, buttonRow);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(10));
        card.getStyleClass().add("card");

        // Set the selectedBodyCard ref once card is created
        if (selectedBodyCard == null) {
            selectedBodyCard = card;
        }

        bodyListBox.getChildren().add(card);

        // Auto-focus if this card matches the name being restored
        if (focusedNameToRestore != null && focusedNameToRestore.equals(name) && name.equals(selectedBody)) {
            pool.getRenderer().setFocusObject(name);
        }
    }

    private void addSatelliteCard(String currentlySelectedBody, String name, Color color) {
        addSatelliteCard(currentlySelectedBody, name, color, true);
    }

    private void addSatelliteCard(String currentlySelectedBody, String name, Color color, boolean startActive) {
        satelliteEntries.get(currentlySelectedBody).put(name, new SatellitePreset(name, color));
        satelliteActiveStates.put(name, startActive);

        // Active state tracking
        final boolean[] active = {startActive};

        // -- Top row: circle + name + active indicator + focus indicator --
        Circle circle = new Circle(18, color);

        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("body");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Active/live indicator
        Circle activeIndicator = new Circle(6, startActive ? Color.LIMEGREEN : Color.RED);
        activeIndicator.getStyleClass().add("active-indicator");

        // Focus indicator (star when focused)
        Label focusIndicatorLabel = new Label("");
        focusIndicatorLabel.getStyleClass().add("focus-indicator");

        HBox topRow = new HBox(8, circle, nameLabel, spacer, focusIndicatorLabel, activeIndicator);
        topRow.setAlignment(Pos.CENTER_LEFT);

        // -- Bottom row: Remove/Add + Focus + View Data --
        Button toggleButton = new Button(startActive ? "Remove" : "Add");
        toggleButton.getStyleClass().add(startActive ? "card-button-remove" : "card-button-add");

        Button focusButton = new Button("Focus");
        focusButton.getStyleClass().add("card-button-focus");
        allFocusButtons.add(focusButton);

        Button viewDataButton = new Button("View Data");
        viewDataButton.getStyleClass().add("card-button-focus");
        viewDataButton.setOnAction(e -> {
            if (active[0]) {
                bottom.selectSatelliteForView(name);
            }
        });

        focusButton.setOnAction(e -> {
            if (!active[0]) return;
            VBox card = (VBox) focusButton.getParent().getParent();
            if (focusedCard == card) {
                clearFocus();
            } else {
                pool.getRenderer().setFocusObject(name);
            }
        });

        toggleButton.setOnAction(e -> {
            VBox card = (VBox) toggleButton.getParent().getParent();
            if (active[0]) {
                // Deactivate: remove from visualization
                active[0] = false;
                satelliteActiveStates.put(name, false);
                activeIndicator.setFill(Color.RED);
                toggleButton.setText("Add");
                toggleButton.getStyleClass().set(0, "card-button-add");
                focusButton.setDisable(true);
                viewDataButton.setDisable(true);
                circle.setOpacity(0.4);
                nameLabel.setOpacity(0.5);
                if (focusedCard == card) {
                    clearFocus();
                }
                // TODO: remove satellite from renderer visualization
            } else {
                // Reactivate: add back to visualization
                active[0] = true;
                satelliteActiveStates.put(name, true);
                activeIndicator.setFill(Color.LIMEGREEN);
                toggleButton.setText("Remove");
                toggleButton.getStyleClass().set(0, "card-button-remove");
                focusButton.setDisable(false);
                viewDataButton.setDisable(false);
                circle.setOpacity(1.0);
                nameLabel.setOpacity(1.0);
                // TODO: add satellite back to renderer visualization
            }
        });

        HBox buttonRow = new HBox(6, toggleButton, focusButton, viewDataButton);
        buttonRow.setAlignment(Pos.CENTER_LEFT);

        VBox card = new VBox(6, topRow, buttonRow);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(10));
        card.getStyleClass().add("card");

        satelliteListBox.getChildren().add(card);
        bottom.addSatelliteColumn(name);

        // Apply initial inactive visual state
        if (!startActive) {
            focusButton.setDisable(true);
            viewDataButton.setDisable(true);
            circle.setOpacity(0.4);
            nameLabel.setOpacity(0.5);
        }

        // Auto-focus if this card matches the name being restored
        if (focusedNameToRestore != null && focusedNameToRestore.equals(name) && startActive) {
            pool.getRenderer().setFocusObject(name);
        }
    }

    private void deselectBody(Circle activeIndicator, Button toggleButton,
            Button focusButton, Circle circle, Label nameLabel, VBox card) {
        toggleButton.setText("Select");
        toggleButton.getStyleClass().set(0, "card-button-select");
        focusButton.setDisable(true);
        circle.setOpacity(0.4);
        nameLabel.setOpacity(0.5);
        if (focusedCard == card) {
            clearFocus();
        }
    }


    public PresetConfiguration toPresetConfiguration() {
        return new PresetConfiguration(bodyEntries.get(selectedBody), satelliteEntries.get(selectedBody), bottom.toPresetState());
    }

    public void applyPresetConfiguration(PresetConfiguration configuration) {
        bodyListBox.getChildren().clear();
        satelliteListBox.getChildren().clear();
        bodyEntries.clear();
        satelliteEntries.clear();
        satelliteActiveStates.clear();
        allFocusButtons.clear();
        focusedCard = null;
        focusedIndicator = null;
        selectedBodyToggle = null;
        selectedBodyCircle = null;
        selectedBodyName = null;
        selectedBodyIndicator = null;
        selectedBodyFocus = null;
        selectedBodyCard = null;
        bottom.clearSatelliteColumns();
        bottom.applyPresetState(configuration.getBottomPanePreset());

        BodyPreset body = configuration.getBody();
        addBodyCard(body.name(), body.color(), body.preset(), body.mass(), body.radius());

        for (Map.Entry<String, SatellitePreset> satellite : configuration.getSatellites().entrySet()) {
            addSatelliteCard(body.name(), satellite.getValue().name(), satellite.getValue().color());
        }
    }

    public void applyWorldConfiguration(WorldConfiguration config) {
        bodyListBox.getChildren().clear();
        satelliteListBox.getChildren().clear();
        bodyEntries.clear();
        satelliteEntries.clear();
        satelliteActiveStates.clear();
        allFocusButtons.clear();
        focusedCard = null;
        focusedIndicator = null;
        selectedBodyToggle = null;
        selectedBodyCircle = null;
        selectedBodyName = null;
        selectedBodyIndicator = null;
        selectedBodyFocus = null;
        selectedBodyCard = null;
        bottom.clearSatelliteColumns();

        // Set the focused name to restore before adding cards
        focusedNameToRestore = config.getFocusedItemName();

        if (config.getUi() != null) {
            bottom.applyPresetState(new PresetConfiguration.BottomPanePreset(
                    config.getUi().specificTime, config.getUi().timescale, config.getUi().running));
        }

        // Add body cards from sidebar data (source of truth for all UI bodies)
        if (config.getBody() != null) {
            WorldConfiguration.SidebarBody sideBarBody = config.getSidebarBody();
            Color bodyColor = sideBarBody.colorHex != null ? Color.web(sideBarBody.colorHex) : Color.RED;
                addBodyCard(sideBarBody.name, bodyColor, sideBarBody.preset, sideBarBody.mass, sideBarBody.radius);
       


       
        } else if (config.getBody() != null) {
            // Fallback for old format with no sidebarBodies
            org.joml.Vector3f c = config.getBody().color;
            Color bodyColor = c != null ? Color.color(
                    Math.min(1, Math.max(0, c.x)),
                    Math.min(1, Math.max(0, c.y)),
                    Math.min(1, Math.max(0, c.z))) : Color.RED;
            addBodyCard(config.getBody().name, bodyColor, true,
                    config.getBody().mass, config.getBody().radius);
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
                addSatelliteCard(selectedBody, sat.name, satColor, sat.active);
            }
        }

        focusedNameToRestore = null;
    }

    private Button tabButton(String text) {
        Button b = new Button(text);
        b.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(b, Priority.ALWAYS);
        b.setPadding(new Insets(6));
        return b;
    }

    private void setTabActive(Button b) {
        b.getStyleClass().set(0, "tab-active");
    }

    private void setTabInactive(Button b) {
        b.getStyleClass().set(0, "tab-inactive");
    }

    public String getSelectedBody() {
        return selectedBody;
    }

    public HashMap<String, Boolean> getSatelliteActiveStates() {
        return satelliteActiveStates;
    }

    public String getFocusedItemName() {
        return focusedItemName;
    }

    public HashMap<String, SatellitePreset> getSatelliteEntries(String bodyName) {
        return satelliteEntries.get(bodyName);
    }
}