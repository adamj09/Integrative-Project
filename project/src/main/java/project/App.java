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

        BottomPane bottom = new BottomPane();
        MainMenuBar menuBar = new MainMenuBar();
        SidebarPane sidebar = new SidebarPane(bottom);

        Renderer.init();

        // Wire menu bar buttons to sidebar actions
        menuBar.getNewBodyButton().setOnAction(e -> sidebar.openNewBodyPopup(stage));
        menuBar.getNewSatelliteButton().setOnAction(e -> sidebar.openNewSatellitePopup(stage));

        BorderPane root = new BorderPane();
        root.setCenter(Renderer.viewport.getGLCanvas());
        root.setTop(menuBar);
        root.setLeft(sidebar);
        root.setBottom(bottom);

        // The "true" value here indicates the creation of a depth buffer. This is
        // essential to ensure all nodes are placed on appopriate layers (contol nodes
        // above panes for example)
        Scene scene = new Scene(root, 1280, 720, true);

        scene.getStylesheets().add(new StyleSheet().styleSheet);

        //TODO: make custom menu bar to replace OS default
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
