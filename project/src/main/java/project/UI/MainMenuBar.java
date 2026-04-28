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

/**
 * Class that handles controls that modify the SidebarPane. This serves as a
 * gateway to features such as saving/loading, adding bodies/satellites, etc.
 * 
 * @author Ryan Lau
 */
public class MainMenuBar extends HBox {
    /**
     * Buttons for launching the body and satellite builders, respectively.
     */
    private Button newBodyButton, newSatelliteButton;

    /**
     * Top level MenuBar.
     */
    private final MenuBar menuBar;

    /**
     * UI elements for saving/loading worlds.
     */
    private Menu configLoaderMenu;
    private MenuItem saveAsMenuItem, saveMenuItem, loadMenuItem;

    /**
     * UI elements for loading presets.
     */
    private Menu presetsMenu;
    private MenuItem mercuryPreset, venusPreset, earthPreset, marsPreset, jupiterPreset, saturnPreset, uranusPreset,
            neptunePreset;

    /**
     * ComboBox for selecting themes.
     */
    private final ComboBox<UiTheme> themeSelector;

    /**
     * Information button.
     */
    private final Button infoButton;

    public MainMenuBar() {
        setPadding(new Insets(6, 10, 6, 10));
        setSpacing(8);
        setAlignment(Pos.CENTER_LEFT);
        getStyleClass().add("main-menu-bar");

        setUpBuilderButtons();
        setUpConfigLoaderMenu();

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

        getChildren().addAll(newBodyButton, newSatelliteButton, menuBar, spacer, themeSelector, infoButton);
    }

    /**
     * Sets up the body and satellite builder buttons.
     */
    private void setUpBuilderButtons() {
        newBodyButton = new Button("New celestial body");
        newBodyButton.getStyleClass().add("style-button");

        newSatelliteButton = new Button("New satellite");
        newSatelliteButton.getStyleClass().add("style-button");
    }

    /**
     * Sets up the menu for saving/loading worlds.
     */
    private void setUpConfigLoaderMenu() {
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
                jupiterPreset, saturnPreset, uranusPreset, neptunePreset);

        // Configuration Loader menu (as Menu)
        configLoaderMenu = new Menu("Configuration Loader");
        configLoaderMenu.getItems().addAll(presetsMenu, saveAsMenuItem, saveMenuItem, loadMenuItem);
    }

    /**
     * @return the newBodyButton.
     */
    public Button getNewBodyButton() {
        return newBodyButton;
    }

    /**
     * @return the newSatelliteButton.
     */
    public Button getNewSatelliteButton() {
        return newSatelliteButton;
    }

    /**
     * @return the saveAsMenuItem.
     */
    public MenuItem getSaveAsMenuItem() {
        return saveAsMenuItem;
    }

    /**
     * @return the saveMenuItem.
     */
    public MenuItem getSaveMenuItem() {
        return saveMenuItem;
    }

    /**
     * @return the loadMenuItem.
     */
    public MenuItem getLoadMenuItem() {
        return loadMenuItem;
    }

    /**
     * @return the top-level menuBar.
     */
    public MenuBar getMenuBar() {
        return menuBar;
    }

    /**
     * @return the configLoaderMenu.
     */
    public Menu getConfigLoaderMenu() {
        return configLoaderMenu;
    }

    /**
     * @return the presetsMenu.
     */
    public Menu getPresetsMenu() {
        return presetsMenu;
    }

    /**
     * @return the mercuryPreset.
     */
    public MenuItem getMercuryPreset() {
        return mercuryPreset;
    }

    /**
     * @return the venusPreset.
     */
    public MenuItem getVenusPreset() {
        return venusPreset;
    }

    /**
     * @return the earthPreset.
     */
    public MenuItem getEarthPreset() {
        return earthPreset;
    }

    /**
     * @return the marsPreset.
     */
    public MenuItem getMarsPreset() {
        return marsPreset;
    }

    /**
     * @return the jupiterPreset.
     */
    public MenuItem getJupiterPreset() {
        return jupiterPreset;
    }

    /**
     * @return the saturnPreset.
     */
    public MenuItem getSaturnPreset() {
        return saturnPreset;
    }

    /**
     * @return the uranusPreset.
     */
    public MenuItem getUranusPreset() {
        return uranusPreset;
    }

    /**
     * @return the neptunePreset.
     */
    public MenuItem getNeptunePreset() {
        return neptunePreset;
    }

    /**
     * @return the themeSelector ComboBox.
     */
    public ComboBox<UiTheme> getThemeSelector() {
        return themeSelector;
    }

    /**
     * @return the infoButton.
     */
    public Button getInfoButton() {
        return infoButton;
    }
}
