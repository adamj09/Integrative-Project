module project {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.desktop;
    requires openglfx.lwjgl;
    requires org.lwjgl;

    opens project to javafx.fxml;
    exports project;
}