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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import project.Presets.PresetConfiguration;
import project.Presets.PresetConfiguration.BodyPreset;
import project.Presets.PresetConfiguration.SatellitePreset;
import project.UI.Popups.BodyCreatorPopup;
import project.UI.Popups.DeleteWarningPopup;
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

    public SidebarPane(BottomPane bottom) {
        this.bottom = bottom;
        setPrefWidth(230);
        setMinWidth(180);
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

    private void addBodyCard(String name, Color color, boolean isPreset) {
        bodyNames.add(name);
        BodyPreset entry = new BodyPreset(name, color, isPreset);
        bodyEntries.add(entry);

        Circle circle = new Circle(22, color);
        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("body");

        Button visButton = new Button(isPreset ? "unvisualize" : "visualize");
        visButton.getStyleClass().add("style-button-small");
        visButton.setOnAction(e -> {
            boolean active = visButton.getText().equals("unvisualize");
            visButton.setText(active ? "visualize" : "unvisualize");
            // TODO: toggle body visibility in renderer
        });

        VBox card = new VBox(4, circle, nameLabel, visButton);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(8));
        card.getStyleClass().add("card");

        if (!isPreset) {
            Button deleteButton = new Button("delete");
            deleteButton.getStyleClass().add("style-button-small");
            deleteButton.setOnAction(e -> {
                DeleteWarningPopup warn = new DeleteWarningPopup();
                warn.showAndWait();
                if (warn.wasConfirmed()) {
                    bodyListBox.getChildren().remove(card);
                    bodyNames.remove(name);
                    bodyEntries.remove(entry);
                }
            });
            HBox btns = new HBox(4, visButton, deleteButton);
            btns.setAlignment(Pos.CENTER);
            card.getChildren().remove(visButton);
            card.getChildren().add(btns);
        }

        bodyListBox.getChildren().add(card);
    }

    private void addSatelliteCard(String name, Color color) {
        satelliteNames.add(name);
        satelliteEntries.add(new SatellitePreset(name, color));

        Circle circle = new Circle(18, color);
        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("body");

        Button visButton = new Button("unvisualize");
        visButton.getStyleClass().add("style-button-small");
        visButton.setOnAction(e -> {
            boolean active = visButton.getText().equals("unvisualize");
            visButton.setText(active ? "visualize" : "unvisualize");
            // TODO: toggle satellite visibility in renderer
        });

        VBox card = new VBox(4, circle, nameLabel, visButton);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(8));
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