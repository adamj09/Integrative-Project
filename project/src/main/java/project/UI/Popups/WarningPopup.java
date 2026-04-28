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

/**
 * UI element used to display warnings an errors as a pop-up window.
 * 
 * @author Ryan Lau
 */
public class WarningPopup extends Stage {

    /**
     * Creates a new pop-up.
     * 
     * @param warningText the text to be display within the pop-up.
     */
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
        root.getStyleClass().add("small-pane");
        root.setPrefWidth(300);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(new StyleSheet().styleSheet);
        setScene(scene);
    }

    /**
     * Creates a new pop-up and shows it.
     * 
     * @param warningText the text to be display within the pop-up.
     */
    public static void show(String warningText) {
        new WarningPopup(warningText).showAndWait();
    }
}
