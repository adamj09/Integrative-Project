package project;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import project.Renderer.Viewport;
import project.Renderer.Renderer;
import project.UI.BottomPane;
import project.UI.MainMenuBar;
import project.UI.SidebarPane;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        setSystemProperties();

        Viewport viewport = new Viewport();

        BottomPane bottom = new BottomPane();
        MainMenuBar menuBar = new MainMenuBar();
        SidebarPane sidebar = new SidebarPane(bottom);

        // Wire menu bar buttons to sidebar actions
        menuBar.getNewBodyButton().setOnAction(e -> sidebar.openNewBodyPopup(stage));
        menuBar.getNewSatelliteButton().setOnAction(e -> sidebar.openNewSatellitePopup(stage));

        BorderPane root = new BorderPane();
        root.setCenter(viewport.getGLCanvas());
        root.setTop(menuBar);
        root.setLeft(sidebar);
        root.setBottom(bottom);

        ControlManager controlManager = new ControlManager(viewport.getGLCanvas());

        new Renderer(viewport, controlManager);

        // The "true" value here indicates the creation of a depth buffer. This is
        // essential to ensure all nodes are placed on appopriate layers (contol nodes
        // above panes for example)
        Scene scene = new Scene(root, 1280, 720, true);

        scene.getStylesheets().add(new StyleSheet().styleSheet);

        stage.setScene(scene);
        stage.setTitle("Orbital Motion Simulator");
        stage.setResizable(true);
        stage.show();
    }

    private void setSystemProperties() {
        System.setProperty("prism.vsync", "false");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
