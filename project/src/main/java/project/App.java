package project;

import java.util.prefs.Preferences;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import project.Math.Body;
import project.Presets.PresetManager;
import project.Renderer.Renderer;
import project.Renderer.World.World;
import project.UI.BottomPane;
import project.UI.MainMenuBar;
import project.UI.Popups.InfoPopup;
import project.UI.SidebarPane;

/**
 * The main application class for the Orbital Motion Simulator.
 * 
 * @author Adam Johnston
 * @author Ryan Lau
 * @author Maxime Gauthier
 */
public class App extends Application {
    private static final String THEME_PREFERENCE_KEY = "ui.theme";

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

        Renderer mainRenderer = new Renderer();

        SimulationPool pool = new SimulationPool(mainRenderer);

        BottomPane bottom = new BottomPane(pool);
        MainMenuBar menuBar = new MainMenuBar();
        SidebarPane sidebar = new SidebarPane(bottom, pool);

        PresetManager presetManager = new PresetManager();

        // Wire menu bar buttons to sidebar actions
        menuBar.getNewBodyButton().setOnAction(_ -> openBodyBuilder(stage, menuBar, sidebar));
        menuBar.getNewSatelliteButton().setOnAction(_ -> openSatelliteBuilder(stage, pool, menuBar, sidebar));
        menuBar.getSaveAsMenuItem()
                .setOnAction(_ -> presetManager.savePresetAs(stage, pool.getCurrentWorld(), sidebar));
        menuBar.getSaveMenuItem().setOnAction(_ -> presetManager.savePreset(stage, pool.getCurrentWorld(), sidebar));
        menuBar.getLoadMenuItem().setOnAction(_ -> loadWorld(stage, mainRenderer, pool, sidebar, presetManager));
        menuBar.getInfoButton().setOnAction(_ -> showInfo(menuBar));

        // Note: mainRenderer.getWorld() may be null here since the GL context
        // hasn't initialized yet. PresetManager handles null World gracefully.
        presetManager.markCurrentStateSaved(mainRenderer.getWorld(), sidebar);

        Preferences preferences = Preferences.userNodeForPackage(App.class);
        UiTheme selectedTheme = UiTheme.fromStoredValue(
                preferences.get(THEME_PREFERENCE_KEY, UiTheme.MIDNIGHT.name()));

        BorderPane root = new BorderPane();
        root.setStyle(selectedTheme.toStyleString());
        root.setCenter(mainRenderer.getViewport().getGLCanvas());
        root.setTop(menuBar);
        root.setLeft(sidebar);
        root.setBottom(bottom);

        root.setStyle("-fx-background-color: #000000");

        menuBar.getThemeSelector().setValue(selectedTheme);

        menuBar.getThemeSelector().setOnAction(e -> {
            UiTheme theme = menuBar.getThemeSelector().getValue();
            if (theme != null) {
                root.setStyle(theme.toStyleString());
                preferences.put(THEME_PREFERENCE_KEY, theme.name());
            }
        });

        // The "true" value here indicates the creation of a depth buffer. This is
        // essential to ensure all nodes are placed on appopriate layers (contol nodes
        // above panes for example)
        Scene scene = new Scene(root, 1280, 720, true);

        scene.getStylesheets().add(new StyleSheet().styleSheet);

        stage.setScene(scene);
        stage.setTitle("Orbital Motion Simulator");
        stage.setResizable(true);
        // stage.setOnCloseRequest(event -> {
        // if (!presetManager.canClose(stage, mainRenderer.getWorld(), sidebar)) {
        // event.consume();
        // }
        // });
        stage.show();

        bottom.updateLoop();
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
        pool.runWorld(newWorld.getName());

        mainRenderer.setWorld(pool.getCurrentWorld());
    }

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
