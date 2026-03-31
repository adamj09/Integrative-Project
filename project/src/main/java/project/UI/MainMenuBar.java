package project.UI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import project.UiTheme;

public class MainMenuBar extends HBox {

    private final Button newBodyButton;
    private final Button newSatelliteButton;
    private final MenuBar menuBar;
    private final Menu configLoaderMenu;
    private final MenuItem saveAsMenuItem;
    private final MenuItem saveMenuItem;
    private final MenuItem loadMenuItem;
    private final Menu presetsMenu;
    private final MenuItem mercuryPreset;
    private final MenuItem venusPreset;
    private final MenuItem earthPreset;
    private final MenuItem marsPreset;
    private final MenuItem jupiterPreset;
    private final MenuItem saturnPreset;
    private final MenuItem uranusPreset;
    private final MenuItem neptunePreset;
    private final ComboBox<UiTheme> themeSelector;
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

        // Presets submenu (as Menu)
        mercuryPreset = new MenuItem("Mercury");
        venusPreset = new MenuItem("Venus");
        earthPreset = new MenuItem("Earth");
        marsPreset = new MenuItem("Mars");
        jupiterPreset = new MenuItem("Jupiter");
        saturnPreset = new MenuItem("Saturn");
        uranusPreset = new MenuItem("Uranus");
        neptunePreset = new MenuItem("Neptune");
        presetsMenu = new Menu("Presets");
        presetsMenu.getItems().addAll(
            mercuryPreset, venusPreset, earthPreset, marsPreset,
            jupiterPreset, saturnPreset, uranusPreset, neptunePreset
        );

        // Configuration Loader menu (as Menu)
        configLoaderMenu = new Menu("Configuration Loader");
        configLoaderMenu.getItems().addAll(
            presetsMenu, saveAsMenuItem, saveMenuItem, loadMenuItem
        );

        // Top-level MenuBar
        menuBar = new MenuBar();
        menuBar.getMenus().add(configLoaderMenu);
        menuBar.setStyle("-fx-background-color: transparent;");

        themeSelector = new ComboBox<>();
        themeSelector.getItems().addAll(UiTheme.values());
        themeSelector.setValue(UiTheme.MIDNIGHT);
        themeSelector.setPrefWidth(140);
        themeSelector.getStyleClass().addAll("combo-box", "theme-selector");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        infoButton = new Button("INFO");
        infoButton.getStyleClass().add("style-button");
        
        this.setOnMouseClicked(_ -> this.requestFocus());

        getChildren().addAll(newBodyButton, newSatelliteButton, menuBar, themeSelector, spacer, infoButton);
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

    public MenuBar getMenuBar() {
        return menuBar;
    }
    public Menu getConfigLoaderMenu() {
        return configLoaderMenu;
    }
    public Menu getPresetsMenu() {
        return presetsMenu;
    }

    public MenuItem getMercuryPreset() { return mercuryPreset; }
    public MenuItem getVenusPreset() { return venusPreset; }
    public MenuItem getEarthPreset() { return earthPreset; }
    public MenuItem getMarsPreset() { return marsPreset; }
    public MenuItem getJupiterPreset() { return jupiterPreset; }
    public MenuItem getSaturnPreset() { return saturnPreset; }
    public MenuItem getUranusPreset() { return uranusPreset; }
    public MenuItem getNeptunePreset() { return neptunePreset; }
    public ComboBox<UiTheme> getThemeSelector() {
        return themeSelector;
    }

    public Button getInfoButton() {
        return infoButton;
    }
}
