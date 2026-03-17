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
        title.getStyleClass().add("subheading");

        Label content = new Label(warningText);
        content.getStyleClass().add("body");
        content.setWrapText(true);
        content.setMaxWidth(280);

        Button closeBtn = new Button("CLOSE");
        closeBtn.getStyleClass().add("style-button");
        closeBtn.setOnAction(e -> close());

        VBox root = new VBox(10, title, content, closeBtn);
        root.setPadding(new Insets(16));
        root.setAlignment(Pos.CENTER_LEFT);
        root.getStyleClass().add("small-pane");;
        root.setPrefWidth(300);

        setScene(new Scene(root));
    }

    public static void show(String text) {
        new WarningPopup(text).showAndWait();
    }
}
