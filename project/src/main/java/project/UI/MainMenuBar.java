package project.UI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class MainMenuBar extends HBox {

    private final Button newBodyButton;
    private final Button newSatelliteButton;
    private final MenuButton fileMenuButton;
    private final MenuItem saveAsMenuItem;
    private final MenuItem saveMenuItem;
    private final MenuItem loadMenuItem;
    private final Button infoButton;

    public MainMenuBar() {
        setPadding(new Insets(6, 10, 6, 10));
        setSpacing(8);
        setAlignment(Pos.CENTER_LEFT);
        getStyleClass().add("main-menu-bar");

        newBodyButton = new Button("New celestial body");
        newBodyButton.getStyleClass().add("style-button");

        newSatelliteButton = new Button("New satellite");
        newSatelliteButton.getStyleClass().add("style-button");

        saveAsMenuItem = new MenuItem("Save As");
        saveMenuItem = new MenuItem("Save");
        loadMenuItem = new MenuItem("Load");

        fileMenuButton = new MenuButton("File");
        fileMenuButton.getItems().addAll(saveAsMenuItem, saveMenuItem, loadMenuItem);
        fileMenuButton.getStyleClass().add("style-button");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        infoButton = new Button("INFO");
        infoButton.getStyleClass().add("style-button");
        
        this.setOnMouseClicked(_ -> this.requestFocus());

        getChildren().addAll(newBodyButton, newSatelliteButton, fileMenuButton, spacer, infoButton);
    }

    public Button getNewBodyButton() {
        return newBodyButton;
    }

    public Button getNewSatelliteButton() {
        return newSatelliteButton;
    }

    public MenuItem getSaveAsMenuItem() {
        return saveAsMenuItem;
    }

    public MenuItem getSaveMenuItem() {
        return saveMenuItem;
    }

    public MenuItem getLoadMenuItem() {
        return loadMenuItem;
    }

    public Button getInfoButton() {
        return infoButton;
    }
}
