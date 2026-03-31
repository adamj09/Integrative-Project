package project;

// TODO: document class
// this is needed to make the stylesheet accessible everywhere in the project
public class StyleSheet {
    public String styleSheet = getClass().getResource("resources/css/MainStyle.css").toExternalForm();
}
