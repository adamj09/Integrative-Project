package oms.UI.Popups;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import oms.StyleSheet;

/**
 * UI element used to display a warning when an unsaved change is made.
 * 
 * @author Ryan Lau
 */
public class UnsavedChangesPopup extends Stage {

    /**
     * Boolean that determines whether to close the pop-up, and if the action it suggests is confirmed.
     */
    private boolean confirmed;

    /**
     * Creates a new pop-up.
     * 
     * @param message the message to be displayed within the pop-up.
     */
    public UnsavedChangesPopup(String message) {
        initModality(Modality.APPLICATION_MODAL);
        setTitle("Unsaved changes");
        setResizable(false);

        Label title = new Label("Unsaved changes");
        title.getStyleClass().addAll("subheading", "unsaved-warning-title");

        Label content = new Label(message);
        content.getStyleClass().addAll("body", "unsaved-warning-message");
        content.setWrapText(true);
        content.setMaxWidth(360);

        Button continueBtn = new Button("CONTINUE");
        continueBtn.getStyleClass().addAll("style-button", "warning-confirm-button");
        continueBtn.setOnAction(e -> {
            confirmed = true;
            close();
        });

        Button cancelBtn = new Button("CANCEL");
        cancelBtn.getStyleClass().add("style-button");
        cancelBtn.setOnAction(e -> close());

        HBox buttons = new HBox(8, continueBtn, cancelBtn);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        VBox root = new VBox(10, title, content, buttons);
        root.setPadding(new Insets(16));
        root.getStyleClass().addAll("small-pane", "unsaved-warning-pane");
        root.setPrefWidth(420);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(new StyleSheet().styleSheet);

        setScene(scene);
    }

    /**
     * @return confirmed boolean.
     */
    public boolean wasConfirmed() {
        return confirmed;
    }

    /**
     *  
     * Creates a new pop-up and shows it until it is confirmed.
     * 
     * @param message the text to be display within the pop-up.
     */
    public static boolean confirm(String message) {
        UnsavedChangesPopup popup = new UnsavedChangesPopup(message);
        popup.showAndWait();
        return popup.wasConfirmed();
    }
}
