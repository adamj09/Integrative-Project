
package project.UI;

import java.util.ArrayList;
import java.util.List;

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
import project.Presets.PresetConfiguration;
import project.Presets.PresetConfiguration.BodyPreset;
import project.Presets.PresetConfiguration.SatellitePreset;
import project.UI.Popups.BodyCreatorPopup;
import project.UI.Popups.SatelliteCreatorPopup;

public class SidebarPane extends VBox {
    private final Button celestialTab;
    private final Button satellitesTab;

    private final VBox bodyListBox;
    private final VBox satelliteListBox;
    private final ScrollPane bodyScroll;
    private final ScrollPane satelliteScroll;

    private final StackPane contentArea;

    private final BottomPane bottom;

    // Track entries so we can wire delete/visualize
    private final List<String> bodyNames = new ArrayList<>();
    private final List<String> satelliteNames = new ArrayList<>();
    private final List<BodyPreset> bodyEntries = new ArrayList<>();
    private final List<SatellitePreset> satelliteEntries = new ArrayList<>();

    // Track focused card (only one at a time)
    private VBox focusedCard = null;
    private Label focusedIndicator = null;
    private final List<Button> allFocusButtons = new ArrayList<>();

    // Track selected celestial body (only one at a time)
    private Button selectedBodyToggle = null;
    private boolean[] selectedBodyActive = null;
    private Circle selectedBodyCircle = null;
    private Label selectedBodyName = null;
    private Circle selectedBodyIndicator = null;
    private Button selectedBodyFocus = null;
    private VBox selectedBodyCard = null;

    public SidebarPane(BottomPane bottom) {
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

        // Add a placeholder body so it's not empty
        addBodyCard("Earth (default)", Color.RED, true);
    }

    public void openNewBodyPopup(Stage owner) {
        BodyCreatorPopup popup = new BodyCreatorPopup(owner);
        popup.showAndWait();
        if (popup.wasConfirmed()) {
            addBodyCard(popup.getBodyName(), popup.getBodyColor(), false);
        }
    }

    public void openNewSatellitePopup(Stage owner) {
        SatelliteCreatorPopup popup = new SatelliteCreatorPopup(owner);
        popup.showAndWait();
        if (popup.wasConfirmed()) {
            addSatelliteCard(popup.getSatelliteName(), popup.getSatelliteColor());
        }
    }

    private void clearFocus() {
        if (focusedIndicator != null) {
            focusedIndicator.setText("");
        }
        focusedCard = null;
        focusedIndicator = null;
    }

    private void setFocusedCard(VBox card, Label indicator) {
        // Clear previous focus
        clearFocus();
        focusedCard = card;
        focusedIndicator = indicator;
        indicator.setText("\u2605"); // star symbol
        // TODO: center camera on this body/satellite in renderer
    }

    private void addBodyCard(String name, Color color, boolean isPreset) {
        bodyNames.add(name);
        BodyPreset entry = new BodyPreset(name, color, isPreset);
        bodyEntries.add(entry);

        // Active state tracking
        final boolean[] active = {true};

        // -- Top row: circle + name + active indicator + focus indicator --
        Circle circle = new Circle(22, color);

        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("body");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Active/live indicator (green = active, red = inactive)
        Circle activeIndicator = new Circle(6, Color.LIMEGREEN);
        activeIndicator.getStyleClass().add("active-indicator");

        // Focus indicator (star when focused)
        Label focusIndicatorLabel = new Label("");
        focusIndicatorLabel.getStyleClass().add("focus-indicator");

        HBox topRow = new HBox(8, circle, nameLabel, spacer, focusIndicatorLabel, activeIndicator);
        topRow.setAlignment(Pos.CENTER_LEFT);

        // -- Bottom row: Selected/Select + Focus --
        Button toggleButton = new Button("Selected");
        toggleButton.getStyleClass().add("card-button-selected");

        Button focusButton = new Button("Focus");
        focusButton.getStyleClass().add("card-button-focus");
        allFocusButtons.add(focusButton);

        focusButton.setOnAction(e -> {
            if (!active[0]) return;
            if (focusedCard != null && focusedCard == (VBox) focusButton.getParent().getParent()) {
                clearFocus();
            } else {
                setFocusedCard((VBox) focusButton.getParent().getParent(), focusIndicatorLabel);
            }
        });

        toggleButton.setOnAction(e -> {
            VBox card = (VBox) toggleButton.getParent().getParent();
            if (active[0]) {
                // Already selected — do nothing, can't deselect
                return;
            } else {
                // Deselect the previously selected body first
                if (selectedBodyToggle != null && selectedBodyActive != null && selectedBodyActive[0]) {
                    deselectBody(selectedBodyActive, selectedBodyIndicator, selectedBodyToggle,
                            selectedBodyFocus, selectedBodyCircle, selectedBodyName, selectedBodyCard);
                }
                // Select this body
                active[0] = true;
                activeIndicator.setFill(Color.LIMEGREEN);
                toggleButton.setText("Selected");
                toggleButton.getStyleClass().set(0, "card-button-selected");
                focusButton.setDisable(false);
                circle.setOpacity(1.0);
                nameLabel.setOpacity(1.0);
                // Track as the selected body
                selectedBodyToggle = toggleButton;
                selectedBodyActive = active;
                selectedBodyCircle = circle;
                selectedBodyName = nameLabel;
                selectedBodyIndicator = activeIndicator;
                selectedBodyFocus = focusButton;
                selectedBodyCard = card;
                // TODO: add body back to renderer visualization
            }
        });

        // New non-preset bodies start as unselected
        if (selectedBodyToggle == null) {
            // First body ever — auto-select it
            selectedBodyToggle = toggleButton;
            selectedBodyActive = active;
            selectedBodyCircle = circle;
            selectedBodyName = nameLabel;
            selectedBodyIndicator = activeIndicator;
            selectedBodyFocus = focusButton;
        } else if (!isPreset) {
            // Additional bodies start deselected
            active[0] = false;
            activeIndicator.setFill(Color.RED);
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
    }

    private void deselectBody(boolean[] active, Circle activeIndicator, Button toggleButton,
            Button focusButton, Circle circle, Label nameLabel, VBox card) {
        active[0] = false;
        activeIndicator.setFill(Color.RED);
        toggleButton.setText("Select");
        toggleButton.getStyleClass().set(0, "card-button-select");
        focusButton.setDisable(true);
        circle.setOpacity(0.4);
        nameLabel.setOpacity(0.5);
        if (focusedCard == card) {
            clearFocus();
        }
        // TODO: remove body from renderer visualization
    }

    private void addSatelliteCard(String name, Color color) {
        satelliteNames.add(name);
        satelliteEntries.add(new SatellitePreset(name, color));

        // Active state tracking
        final boolean[] active = {true};

        // -- Top row: circle + name + active indicator + focus indicator --
        Circle circle = new Circle(18, color);

        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("body");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Active/live indicator
        Circle activeIndicator = new Circle(6, Color.LIMEGREEN);
        activeIndicator.getStyleClass().add("active-indicator");

        // Focus indicator (star when focused)
        Label focusIndicatorLabel = new Label("");
        focusIndicatorLabel.getStyleClass().add("focus-indicator");

        HBox topRow = new HBox(8, circle, nameLabel, spacer, focusIndicatorLabel, activeIndicator);
        topRow.setAlignment(Pos.CENTER_LEFT);

        // -- Bottom row: Remove/Add + Focus --
        Button toggleButton = new Button("Remove");
        toggleButton.getStyleClass().add("card-button-remove");

        Button focusButton = new Button("Focus");
        focusButton.getStyleClass().add("card-button-focus");
        allFocusButtons.add(focusButton);

        focusButton.setOnAction(e -> {
            if (!active[0]) return;
            VBox card = (VBox) focusButton.getParent().getParent();
            if (focusedCard == card) {
                clearFocus();
            } else {
                setFocusedCard(card, focusIndicatorLabel);
            }
        });

        toggleButton.setOnAction(e -> {
            VBox card = (VBox) toggleButton.getParent().getParent();
            if (active[0]) {
                // Deactivate: remove from visualization
                active[0] = false;
                activeIndicator.setFill(Color.RED);
                toggleButton.setText("Add");
                toggleButton.getStyleClass().set(0, "card-button-add");
                focusButton.setDisable(true);
                circle.setOpacity(0.4);
                nameLabel.setOpacity(0.5);
                if (focusedCard == card) {
                    clearFocus();
                }
                // TODO: remove satellite from renderer visualization
            } else {
                // Reactivate: add back to visualization
                active[0] = true;
                activeIndicator.setFill(Color.LIMEGREEN);
                toggleButton.setText("Remove");
                toggleButton.getStyleClass().set(0, "card-button-remove");
                focusButton.setDisable(false);
                circle.setOpacity(1.0);
                nameLabel.setOpacity(1.0);
                // TODO: add satellite back to renderer visualization
            }
        });

        HBox buttonRow = new HBox(6, toggleButton, focusButton);
        buttonRow.setAlignment(Pos.CENTER_LEFT);

        VBox card = new VBox(6, topRow, buttonRow);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(10));
        card.getStyleClass().add("card");

        satelliteListBox.getChildren().add(card);
        bottom.addSatelliteColumn(name);
    }

    public PresetConfiguration toPresetConfiguration() {
        return new PresetConfiguration(bodyEntries, satelliteEntries, bottom.toPresetState());
    }

    public void applyPresetConfiguration(PresetConfiguration configuration) {
        bodyListBox.getChildren().clear();
        satelliteListBox.getChildren().clear();
        bodyNames.clear();
        satelliteNames.clear();
        bodyEntries.clear();
        satelliteEntries.clear();
        allFocusButtons.clear();
        focusedCard = null;
        focusedIndicator = null;
        selectedBodyToggle = null;
        selectedBodyActive = null;
        selectedBodyCircle = null;
        selectedBodyName = null;
        selectedBodyIndicator = null;
        selectedBodyFocus = null;
        selectedBodyCard = null;
        bottom.clearSatelliteColumns();
        bottom.applyPresetState(configuration.getBottomPanePreset());

        for (BodyPreset body : configuration.getBodies()) {
            addBodyCard(body.name(), body.color(), body.preset());
        }
        for (SatellitePreset satellite : configuration.getSatellites()) {
            addSatelliteCard(satellite.name(), satellite.color());
        }
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
}