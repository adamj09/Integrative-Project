package project;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import project.Renderer.Viewport;
import project.Renderer.ControlManager;
import project.Renderer.Renderer;
import project.UI.BottomPane;
import project.UI.MainMenuBar;
import project.UI.SidebarPane;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        setSystemProperties();

        Viewport viewport = new Viewport();

        StackPane top = new StackPane();

        BottomPane bottom = new BottomPane();
        MainMenuBar menuBar = new MainMenuBar();
        SidebarPane sidebar = new SidebarPane(bottom);

        // Wire menu bar buttons to sidebar actions
        menuBar.getNewBodyButton().setOnAction(e -> sidebar.openNewBodyPopup(stage));
        menuBar.getNewSatelliteButton().setOnAction(e -> sidebar.openNewSatellitePopup(stage));

        BorderPane root = new BorderPane();
        root.setTop(menuBar);
        root.setLeft(sidebar);
        root.setCenter(viewport.getGLCanvas());
        root.setBottom(bottom);

        ControlManager controlManager = new ControlManager(root.getCenter());

        new Renderer(viewport, controlManager);

        top.getChildren().add(root);

        bottom.setOpacity(0.99999);
        menuBar.setOpacity(0.99999);
        sidebar.setOpacity(0.99999);

        Scene scene = new Scene(top, 1280, 720);

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
