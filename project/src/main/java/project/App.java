package project;

import java.util.prefs.Preferences;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import project.Presets.PresetManager;
import project.Renderer.Renderer;
import project.UI.BottomPane;
import project.UI.MainMenuBar;
import project.UI.Popups.InfoPopup;
import project.UI.SidebarPane;

public class App extends Application {
    private static final String THEME_PREFERENCE_KEY = "ui.theme";

    @Override
    public void start(Stage stage) {
        setSystemProperties();

        BottomPane bottom = new BottomPane();
        MainMenuBar menuBar = new MainMenuBar();
        SidebarPane sidebar = new SidebarPane(bottom);
        PresetManager presetManager = new PresetManager();

        Renderer mainRenderer = new Renderer();

        // Wire menu bar buttons to sidebar actions
        menuBar.getNewBodyButton().setOnAction(e -> sidebar.openNewBodyPopup(stage));
        menuBar.getNewSatelliteButton().setOnAction(e -> sidebar.openNewSatellitePopup(stage));
        menuBar.getSaveAsMenuItem().setOnAction(e -> presetManager.savePresetAs(stage, sidebar));
        menuBar.getSaveMenuItem().setOnAction(e -> presetManager.savePreset(stage, sidebar));
        menuBar.getLoadMenuItem().setOnAction(e -> presetManager.loadPreset(stage, sidebar));
        menuBar.getInfoButton().setOnAction(e -> InfoPopup.show("""
            Orbital Motion Simulator

            Use 'New celestial body' to add a central body and 'New satellite' to add orbiting objects.
            Adjust simulation settings in the sidebar and bottom controls.
            """));

        presetManager.markCurrentStateSaved(sidebar);

        Preferences preferences = Preferences.userNodeForPackage(App.class);
        UiTheme selectedTheme = UiTheme.fromStoredValue(
            preferences.get(THEME_PREFERENCE_KEY, UiTheme.MIDNIGHT.name()));

        BorderPane root = new BorderPane();
        root.setStyle(selectedTheme.toStyleString());
        root.setCenter(mainRenderer.getViewport().getGLCanvas());
        root.setTop(menuBar);
        root.setLeft(sidebar);
        root.setBottom(bottom);

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
            if (!presetManager.canClose(stage, sidebar)) {
                event.consume();
            }
        });
        stage.show();
    }

    private void setSystemProperties() {
        System.setProperty("prism.vsync", "false");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
