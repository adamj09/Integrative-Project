module project {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.desktop;
    requires openglfx.jogl;

    opens project to javafx.fxml;
    exports project;
}