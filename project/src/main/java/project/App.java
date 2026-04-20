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

        BottomPane bottom = new BottomPane();
        MainMenuBar menuBar = new MainMenuBar();
        SidebarPane sidebar = new SidebarPane(bottom, pool);


        PresetManager presetManager = new PresetManager();

        // Wire menu bar buttons to sidebar actions
        // TODO: perhaps these lambda functions should be instead methods moved in appropriate classes?
        // This is okay for now, but as we add functionality may get confusing later.
        menuBar.getNewBodyButton().setOnAction(e -> {
            sidebar.openNewBodyPopup(stage,
                    menuBar.getThemeSelector().getValue() != null
                            ? menuBar.getThemeSelector().getValue().toStyleString()
                            : "");
        });
        menuBar.getNewSatelliteButton().setOnAction(e -> {
            if (pool.getCurrentWorld() == null) return;

            Body body = pool.getCurrentWorld().getBody();
            // TODO: this check for whether a body can actually have a satellite orbiting it
            // (sufficient mass) is probably not great, but is good enough for now.
            if (body.getHillRadius() > body.getRadius()) {
                sidebar.openNewSatellitePopup(stage,
                        menuBar.getThemeSelector().getValue() != null
                                ? menuBar.getThemeSelector().getValue().toStyleString()
                                : "");
            }
        });
        menuBar.getSaveAsMenuItem().setOnAction(e -> presetManager.savePresetAs(stage, pool.getCurrentWorld(), sidebar));
        menuBar.getSaveMenuItem().setOnAction(e -> presetManager.savePreset(stage, pool.getCurrentWorld(), sidebar));
        menuBar.getLoadMenuItem().setOnAction(e -> { 
            World newWorld = presetManager.loadPreset(stage, sidebar);

            pool.addWorld(newWorld);
            pool.runWorld(newWorld.getName());

            mainRenderer.setWorld(pool.getCurrentWorld());
        });
        menuBar.getInfoButton().setOnAction(e -> InfoPopup.show("""
                Orbital Motion Simulator

                Use 'New celestial body' to add a central body and 'New satellite' to add orbiting objects.
                Adjust simulation settings in the sidebar and bottom controls.
                """,
                menuBar.getThemeSelector().getValue() != null
                        ? menuBar.getThemeSelector().getValue().toStyleString()
                        : ""));

        presetManager.markCurrentStateSaved(mainRenderer.getWorld(), sidebar);

        // Note: mainRenderer.getWorld() may be null here since the GL context
        // hasn't initialized yet. PresetManager handles null World gracefully.

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
        stage.setOnCloseRequest(event -> {
            if (!presetManager.canClose(stage, mainRenderer.getWorld(), sidebar)) {
                event.consume();
            }
        });
        stage.show();
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
