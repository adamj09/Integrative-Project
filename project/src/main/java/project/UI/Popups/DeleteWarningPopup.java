package project.UI.Popups;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import project.StyleSheet;

public class DeleteWarningPopup extends Stage {

    private boolean confirmed = false;

    public DeleteWarningPopup() {
        initModality(Modality.APPLICATION_MODAL);
        setTitle("Delete warning");
        setResizable(false);

        Label title = new Label("Delete warning");
        title.getStyleClass().add("subheading");

        Label message = new Label("Are you sure you want to delete?");
        message.getStyleClass().add("heading");

        Button yesBtn    = new Button("YES");
        Button cancelBtn = new Button("CANCEL");
        yesBtn.getStyleClass().add("style-button");
        cancelBtn.getStyleClass().add("style-button");

        yesBtn.setOnAction(e -> { confirmed = true; close(); });
        cancelBtn.setOnAction(e -> close());

        HBox buttons = new HBox(8, yesBtn, cancelBtn);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        VBox root = new VBox(10, title, message, buttons);
        root.setPadding(new Insets(16));
        root.getStyleClass().add("small-pane");

        Scene scene = new Scene(root);
        scene.getStylesheets().add(new StyleSheet().styleSheet);

        setScene(scene);
    }

    public boolean wasConfirmed() { return confirmed; }
}
 