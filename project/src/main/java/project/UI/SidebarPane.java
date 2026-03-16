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
 
    public SidebarPane(BottomPane bottom) {
        this.bottom = bottom;
        setPrefWidth(230);
        setMinWidth(180);
        setStyle("-fx-background-color: #1a1a2e; -fx-border-color: #444466; -fx-border-width: 0 1 0 0;");
 
        // --- Tab buttons ---
        celestialTab   = tabButton("Celestial body");
        satellitesTab  = tabButton("Satellites");
 
        HBox tabs = new HBox(0, celestialTab, satellitesTab);
        tabs.setStyle("-fx-background-color: #2a2a4a;");
 
        // --- Body list ---
        bodyListBox = new VBox(4);
        bodyListBox.setPadding(new Insets(6));

        bodyScroll = new ScrollPane(bodyListBox);
        bodyScroll.setFitToWidth(true);
        bodyScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        VBox.setVgrow(bodyScroll, Priority.ALWAYS);
 
        VBox bodyView = new VBox(bodyScroll);
        VBox.setVgrow(bodyView, Priority.ALWAYS);
 
        // --- Satellite list ---
        satelliteListBox = new VBox(4);
        satelliteListBox.setPadding(new Insets(6));
 
        satelliteScroll = new ScrollPane(satelliteListBox);
        satelliteScroll.setFitToWidth(true);
        satelliteScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
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
 
        Circle circle = new Circle(22, color);
        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #c0c0e0;");
 
        Button visButton = new Button(isPreset ? "unvisualize" : "visualize");
        visButton.setStyle(smallButtonStyle());
        visButton.setOnAction(e -> {
            boolean active = visButton.getText().equals("unvisualize");
            visButton.setText(active ? "visualize" : "unvisualize");
            // TODO: toggle body visibility in renderer
        });
 
        VBox card = new VBox(4, circle, nameLabel, visButton);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(8));
        card.setStyle("-fx-background-color: #2a2a4a; -fx-border-color: #444466; " +
                      "-fx-border-width: 0 0 1 0; -fx-border-style: dashed;");
 
        if (!isPreset) {
            Button deleteButton = new Button("delete");
            deleteButton.setStyle(smallButtonStyle());
            deleteButton.setOnAction(e -> {
                DeleteWarningPopup warn = new DeleteWarningPopup();
                warn.showAndWait();
                if (warn.wasConfirmed()) {
                    bodyListBox.getChildren().remove(card);
                    bodyNames.remove(name);
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
 
        Circle circle = new Circle(18, color);
        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #c0c0e0;");
 
        Button visButton = new Button("unvisualize");
        visButton.setStyle(smallButtonStyle());
        visButton.setOnAction(e -> {
            boolean active = visButton.getText().equals("unvisualize");
            visButton.setText(active ? "visualize" : "unvisualize");
            // TODO: toggle satellite visibility in renderer
        });
 
        VBox card = new VBox(4, circle, nameLabel, visButton);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(8));
        card.setStyle("-fx-background-color: #2a2a4a; -fx-border-color: #444466; " +
                      "-fx-border-width: 0 0 1 0; -fx-border-style: dashed;");
 
        satelliteListBox.getChildren().add(card);
        bottom.addSatelliteColumn(name);
    }
 
    private Button tabButton(String text) {
        Button b = new Button(text);
        b.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(b, Priority.ALWAYS);
        b.setPadding(new Insets(6));
        return b;
    }
 
    private void setTabActive(Button b) {
        b.setStyle("-fx-background-color: #3a3a5a; -fx-font-weight: bold; " +
                   "-fx-border-color: #444466; -fx-border-width: 0 1 0 0; -fx-font-size: 12px; -fx-text-fill: #c0c0e0;");
    }
 
    private void setTabInactive(Button b) {
        b.setStyle("-fx-background-color: #2a2a4a; -fx-font-weight: normal; " +
                   "-fx-border-color: #444466; -fx-border-width: 0 1 0 0; -fx-font-size: 12px; -fx-text-fill: #c0c0e0;");
    }
 
    private Label sectionLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size: 11px; -fx-text-fill: #c0c0e0; -fx-padding: 4 0 2 0;");
        return l;
    }
 
    private String smallButtonStyle() {
        return "-fx-background-color: #4a4a6a; -fx-text-fill: #c0c0e0; -fx-font-size: 11px; " +
               "-fx-padding: 2 8 2 8; -fx-background-radius: 3; -fx-cursor: hand;";
    }
}