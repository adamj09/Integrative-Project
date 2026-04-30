package oms;

import java.util.prefs.Preferences;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import oms.Math.Body;
import oms.Presets.PlanetPresets;
import oms.Presets.PresetManager;
import oms.Renderer.Renderer;
import oms.Renderer.World.World;
import oms.UI.BottomPane;
import oms.UI.MainMenuBar;
import oms.UI.Popups.InfoPopup;
import oms.UI.SidebarPane;

/**
 * The main application class for the Orbital Motion Simulator.
 * 
 * @author Adam Johnston
 * @author Ryan Lau
 * @author Maxime Gauthier
 */
public class App extends Application {
    /**
     * Key used to index into Preferences for the UI theme.
     */
    private static final String THEME_PREFERENCE_KEY = "ui.theme";

    /**
     * The SimulationPool containing all the application's currently loaded worlds.
     */
    private SimulationPool pool;

    /**
     * Starts the JavaFX application, setting up the main window, UI components, and
     * event handlers.
     * 
     * @param stage The primary stage for this application, onto which the
     *              application scene can be set.
     */
    @Override
    public void start(Stage stage) {
        setSystemProperties();

        // Create a new simulation pool.
        pool = new SimulationPool(new Renderer());

        // Set up the main window's scene.
        Scene scene = setUpScene(stage);
        scene.getStylesheets().add(new StyleSheet().styleSheet);

        // Set up the stage.
        stage.setScene(scene);
        stage.setTitle("Orbital Motion Simulator");
        stage.setResizable(true);
        stage.show();
    }

    /**
     * Sets up the main scene.
     * 
     * @param stage the root stage of the JavaFX application.
     * @return the scene that was set up.
     */
    private Scene setUpScene(Stage stage) {
        // Create the main application's panes.
        BottomPane bottom = new BottomPane(pool);
        MainMenuBar menuBar = new MainMenuBar();
        SidebarPane sidebar = new SidebarPane(bottom, pool);

        // Get the currently selected theme.
        Preferences preferences = Preferences.userNodeForPackage(App.class);
        UiTheme selectedTheme = UiTheme.fromStoredValue(
                preferences.get(THEME_PREFERENCE_KEY, UiTheme.MIDNIGHT.name()));

        // Set up the root pane with all the main application's panes.
        BorderPane root = new BorderPane();
        root.setStyle(selectedTheme.toStyleString());
        root.setCenter(pool.getRenderer().getViewport().getGLCanvas());
        root.setTop(menuBar);
        root.setLeft(sidebar);
        root.setBottom(bottom);
        root.getStyleClass().add("border-pane");

        // Set the theme, and add handler for changing theme.
        menuBar.getThemeSelector().setValue(selectedTheme);
        menuBar.getThemeSelector().setOnAction(_ -> setTheme(menuBar, preferences, root));

        menuBar.getMercuryPreset().setOnAction(e -> PlanetPresets.loadMercury(pool, sidebar));
        menuBar.getVenusPreset().setOnAction(e -> PlanetPresets.loadVenus(pool, sidebar));
        menuBar.getEarthPreset().setOnAction(e -> PlanetPresets.loadEarth(pool, sidebar));
        menuBar.getMarsPreset().setOnAction(e -> PlanetPresets.loadMars(pool, sidebar));
        menuBar.getJupiterPreset().setOnAction(e -> PlanetPresets.loadJupiter(pool, sidebar));
        menuBar.getSaturnPreset().setOnAction(e -> PlanetPresets.loadSaturn(pool, sidebar));
        menuBar.getUranusPreset().setOnAction(e -> PlanetPresets.loadUranus(pool, sidebar));
        menuBar.getNeptunePreset().setOnAction(e -> PlanetPresets.loadNeptune(pool, sidebar));

        PresetManager presetManager = new PresetManager();

        // Wire menu bar buttons to sidebar actions
        setUpMenuBarEventHandlers(stage, menuBar, presetManager, sidebar);

        // Run the live data pane's update loop.
        bottom.updateLoop();

        // The "true" value here indicates the creation of a depth buffer. This is
        // essential to ensure all nodes are placed on appopriate layers (contol nodes
        // above panes for example)
        return new Scene(root, 1280, 720, true);
    }

    /**
     * Updates the theme based on what is currently selected in the theme selection
     * ComboBox.
     * 
     * @param menuBar     the MainMenuBar containing the theme selection ComboBox.
     * @param preferences Preferences object to save the currently selected theme
     *                    to.
     * @param root        the BorderPane
     *                    o which the updated theme should be applied.
     */
    private void setTheme(MainMenuBar menuBar, Preferences preferences, BorderPane root) {
        UiTheme theme = menuBar.getThemeSelector().getValue();
        if (theme != null) {
            root.setStyle(theme.toStyleString());
            preferences.put(THEME_PREFERENCE_KEY, theme.name());
        }
    }

    /**
     * Sets up all the event handlers for the MenuBar.
     * 
     * @param stage         the root stage of the JavaFX application.
     * @param menuBar       the MenuBar to set up event handlers for.
     * @param presetManager the PresetManager used for saving/loading worlds.
     * @param sidebar       the application's sidebar (used to retrieve currently
     *                      loaded worlds and satellites).
     */
    private void setUpMenuBarEventHandlers(Stage stage, MainMenuBar menuBar, PresetManager presetManager,
            SidebarPane sidebar) {
        menuBar.getNewBodyButton().setOnAction(_ -> openBodyBuilder(stage, menuBar, sidebar));
        menuBar.getNewSatelliteButton().setOnAction(_ -> openSatelliteBuilder(stage, pool, menuBar, sidebar));
        menuBar.getSaveAsMenuItem()
                .setOnAction(_ -> presetManager.savePresetAs(stage, pool.getCurrentWorld(), sidebar));
        menuBar.getSaveMenuItem().setOnAction(_ -> presetManager.savePreset(stage, pool.getCurrentWorld(), sidebar));
        menuBar.getLoadMenuItem().setOnAction(_ -> loadWorld(stage, pool.getRenderer(), pool, sidebar, presetManager));
        menuBar.getInfoButton().setOnAction(_ -> showInfo(menuBar));
    }

    /**
     * Opens the app's information tab.
     * 
     * @param menuBar the application's menu bar (used to determine theme to be used
     *                by the satellite builder).
     */
    private void showInfo(MainMenuBar menuBar) {
        InfoPopup.show("""
                Orbital Motion Simulator

                Use 'New celestial body' to add a central body and 'New satellite' to add orbiting objects.
                Adjust simulation settings in the sidebar and bottom controls.
                """,
                menuBar.getThemeSelector().getValue() != null
                        ? menuBar.getThemeSelector().getValue().toStyleString()
                        : "");
    }

    /**
     * Opens the satellite builder.
     * 
     * @param stage   the root stage of the JavaFX application.
     * @param pool    the pool of loaded worlds.
     * @param menuBar the application's menu bar (used to determine theme to be used
     *                by the satellite builder).
     * @param sidebar the side bar to which we'd like to add the satellite upon
     *                creation.
     */
    private void openSatelliteBuilder(Stage stage, SimulationPool pool, MainMenuBar menuBar,
            SidebarPane sidebar) {
        if (pool.getCurrentWorld() == null)
            return;

        Body body = pool.getCurrentWorld().getBody();
        // TODO: this check for whether a body can actually have a satellite orbiting it
        // (sufficient mass) is probably not great, but is good enough for now.
        if (body.getHillRadius() > body.getRadius()) {
            sidebar.openNewSatellitePopup(stage,
                    menuBar.getThemeSelector().getValue() != null
                            ? menuBar.getThemeSelector().getValue().toStyleString()
                            : "");
        }
    }

    /**
     * Load a world using the given parameters.
     * 
     * @param stage         the root stage of the JavaFX application.
     * @param mainRenderer  the simulation's renderer.
     * @param pool          the pool of loaded worlds.
     * @param sidebar       the sidebar to which we'd like to add the world upon
     *                      loading.
     * @param presetManager the preset manager used to save and load worlds.
     */
    private void loadWorld(Stage stage, Renderer mainRenderer, SimulationPool pool,
            SidebarPane sidebar, PresetManager presetManager) {
        World newWorld = presetManager.loadPreset(stage, sidebar);

        if (newWorld == null) {
            return;
        }

        pool.addWorld(newWorld);
        if (pool.getWorlds().size() == 1) {
            pool.runWorld(newWorld.getName());

            mainRenderer.setWorld(pool.getCurrentWorld());
        }
    }

    /**
     * Opens the central celestial body builder.
     * 
     * @param stage   the root stage of the JavaFX application.
     * @param menuBar the application's menu bar (used to determine theme to be used
     *                by the Body builder).
     * @param sidebar the side bar to which we'd like to add the Body upon
     *                creation.
     */
    private void openBodyBuilder(Stage stage, MainMenuBar menuBar, SidebarPane sidebar) {
        sidebar.openNewBodyPopup(stage,
                menuBar.getThemeSelector().getValue() != null
                        ? menuBar.getThemeSelector().getValue().toStyleString()
                        : "");
    }

    /**
     * Sets the system properties for the application.
     */
    private void setSystemProperties() {
        // Turn off vsync so that framerate can be manually controlled.
        System.setProperty("prism.vsync", "false");
    }

    /**
     * The main entry point for the application.
     * 
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
