package project.UI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class MainMenuBar extends HBox {

    private Button newBodyButton;
    private Button newSatelliteButton;
    private Button infoButton;

    public MainMenuBar() {
        setPadding(new Insets(6, 10, 6, 10));
        setSpacing(8);
        setAlignment(Pos.CENTER_LEFT);
        getStyleClass().add("main-menu-bar");

        newBodyButton = new Button("New celestial body");
        newBodyButton.getStyleClass().add("style-button");

        newSatelliteButton = new Button("New satellite");
        newSatelliteButton.getStyleClass().add("style-button");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        infoButton = new Button("INFO");
        infoButton.getStyleClass().add("style-button");
        
        this.setOnMouseClicked(_ -> this.requestFocus());

        getChildren().addAll(newBodyButton, newSatelliteButton, spacer, infoButton);
    }

    public Button getNewBodyButton() {
        return newBodyButton;
    }

    public Button getNewSatelliteButton() {
        return newSatelliteButton;
    }

    public Button getInfoButton() {
        return infoButton;
    }
}
