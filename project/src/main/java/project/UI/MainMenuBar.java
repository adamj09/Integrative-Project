package project.UI;
 
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
 
public class MainMenuBar extends HBox {
 
    private Button newBodyButton;
    private Button newSatelliteButton;
    private Button infoButton;
 
    public MainMenuBar() {
        setPadding(new Insets(6, 10, 6, 10));
        setSpacing(8);
        setAlignment(Pos.CENTER_LEFT);
        setStyle("-fx-background-color: #2a2a4a; -fx-border-color: #444466; -fx-border-width: 0 0 1 0;");
 
        Label title = new Label("Orbital motion");
        title.setStyle("-fx-text-fill: #c0c0e0; -fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 0 16 0 0;");
 
        newBodyButton = styledButton("New celestial body");
        newSatelliteButton = styledButton("New satellite");
 
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
 
        infoButton = styledButton("INFO");
 
        getChildren().addAll(title, newBodyButton, newSatelliteButton, spacer, infoButton);
    }
 
    private Button styledButton(String text) {
        Button b = new Button(text);
        b.setStyle(
            "-fx-background-color: #4a4a6a; " +
            "-fx-text-fill: #c0c0e0; " +
            "-fx-font-size: 12px; " +
            "-fx-padding: 3 10 3 10; " +
            "-fx-background-radius: 3; " +
            "-fx-cursor: hand;"
        );
        return b;
    }
 
    public Button getNewBodyButton()      { return newBodyButton; }
    public Button getNewSatelliteButton() { return newSatelliteButton; }
    public Button getInfoButton()         { return infoButton; }
}
 