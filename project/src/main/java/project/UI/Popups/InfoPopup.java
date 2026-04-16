package project.UI.Popups;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import project.StyleSheet;

public class InfoPopup extends Stage {

    public InfoPopup(String infoText, String themeStyle) {
        initModality(Modality.APPLICATION_MODAL);
        setTitle("Information");
        setResizable(false);

        Label title = new Label("Information");
        title.getStyleClass().add("subheading");

        Label content = new Label(infoText);
        content.getStyleClass().add("body");
        content.setWrapText(true);
        content.setMaxWidth(300);

        Button closeBtn = new Button("CLOSE");
        closeBtn.getStyleClass().add("style-button");
        closeBtn.setOnAction(e -> close());

        VBox root = new VBox(10, title, content, closeBtn);
        root.setPadding(new Insets(16));
        root.setAlignment(Pos.CENTER_LEFT);
        root.getStyleClass().add("small-pane");
        root.setPrefWidth(340);
        root.setStyle(themeStyle);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(new StyleSheet().styleSheet);

        setScene(scene);
    }

    public static void show(String text, String themeStyle) {
        new InfoPopup(text, themeStyle).showAndWait();
    }
}
