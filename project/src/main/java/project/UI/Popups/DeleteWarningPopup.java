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

public class DeleteWarningPopup extends Stage {

    private boolean confirmed = false;

    public DeleteWarningPopup() {
        initModality(Modality.APPLICATION_MODAL);
        setTitle("Delete warning");
        setResizable(false);

        Label title = new Label("Delete warning");
        title.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #c0c0e0;");

        Label message = new Label("Are you sure you want to delete?");
        message.setStyle("-fx-font-size: 12px; -fx-text-fill: #8888bb; -fx-padding: 8 0 8 0;");

        Button yesBtn    = new Button("YES");
        Button cancelBtn = new Button("CANCEL");
        yesBtn.setStyle(btnStyle());
        cancelBtn.setStyle(btnStyle());

        yesBtn.setOnAction(e -> { confirmed = true; close(); });
        cancelBtn.setOnAction(e -> close());

        HBox buttons = new HBox(8, yesBtn, cancelBtn);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        VBox root = new VBox(10, title, message, buttons);
        root.setPadding(new Insets(16));
        root.setStyle("-fx-background-color: #1a1a2e; -fx-border-color: #444466; -fx-border-width: 1;");

        setScene(new Scene(root));
    }

    public boolean wasConfirmed() { return confirmed; }

    private String btnStyle() {
        return "-fx-background-color: #4a4a6a; -fx-text-fill: #c0c0e0; -fx-font-size: 12px; " +
               "-fx-padding: 4 16 4 16; -fx-background-radius: 3; -fx-cursor: hand;";
    }
}
 