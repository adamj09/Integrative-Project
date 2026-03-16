package project.UI.Popups;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class WarningPopup extends Stage {

    public WarningPopup(String warningText) {
        initModality(Modality.APPLICATION_MODAL);
        setTitle("Warning");
        setResizable(false);

        Label title = new Label("Warning");
        title.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #c0c0e0;");

        Label content = new Label(warningText);
        content.setStyle("-fx-font-size: 12px; -fx-text-fill: #8888bb; -fx-padding: 8 0 8 0;");
        content.setWrapText(true);
        content.setMaxWidth(280);

        Button closeBtn = new Button("CLOSE");
        closeBtn.setStyle(
            "-fx-background-color: #4a4a6a; -fx-text-fill: #c0c0e0; -fx-font-size: 12px; " +
            "-fx-padding: 4 16 4 16; -fx-background-radius: 3; -fx-cursor: hand;"
        );
        closeBtn.setOnAction(e -> close());

        VBox root = new VBox(10, title, content, closeBtn);
        root.setPadding(new Insets(16));
        root.setAlignment(Pos.CENTER_LEFT);
        root.setStyle("-fx-background-color: #1a1a2e; -fx-border-color: #444466; -fx-border-width: 1;");
        root.setPrefWidth(300);

        setScene(new Scene(root));
    }

    public static void show(String text) {
        new WarningPopup(text).showAndWait();
    }
}
