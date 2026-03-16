package project;
 
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import project.Renderer.Renderer;
import project.UI.BottomPane;
import project.UI.MainMenuBar;
import project.UI.SidebarPane;
 
public class Main extends Application {
        // TODO: should probably make a class to hold these values (not Renderer since
    // that object will have to be recreated if the user decides to changes mssa or
    // swapbuffers variables)
    public static double fps = 60;
    public static int msaa = 4;
    public static int swapBuffers = 2;
 
    @Override
    public void start(Stage stage) {
        setSystemProperties();
 
        SimRenderer renderer = new SimRenderer(fps, msaa, swapBuffers);
 
        BottomPane  bottom    = new BottomPane();
        MainMenuBar menuBar   = new MainMenuBar();
        SidebarPane sidebar   = new SidebarPane(bottom);
 
        // Wire menu bar buttons to sidebar actions
        menuBar.getNewBodyButton().setOnAction(e -> sidebar.openNewBodyPopup(stage));
        menuBar.getNewSatelliteButton().setOnAction(e -> sidebar.openNewSatellitePopup(stage));
 
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1a1a2e;");
        root.setTop(menuBar);
        root.setLeft(sidebar);
        root.setCenter(renderer.getCanvas());
        root.setBottom(bottom);
 
        stage.setScene(new Scene(root, 1280, 720));

        StackPane rootPane = new StackPane();

        Scene scene = new Scene(rootPane, 1280, 720);

        Renderer renderer = new Renderer(fps, msaa, swapBuffers);
        rootPane.getChildren().add(renderer.getCanvas());

        stage.setScene(scene);
        stage.setTitle("Orbital Motion Simulator");
        stage.setMaxWidth(1280);
        stage.setMaxHeight(720);
        stage.setResizable(true);
        stage.show();
    }
 
    private void setSystemProperties() {
        System.setProperty("prism.vsync", "false");
    }
 
    public static void main(String[] args) {
        launch(args);;
    }
}
