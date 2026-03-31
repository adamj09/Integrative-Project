Need Java 25 or higher. 

The JavaFX SDK (version 25.0.2) is required to run the project. 
Install the SDK at any location (not in the project) and set the module-path in the below VM arguments to the lib folder of the SDK. Note that the entire SDK is needed (only having the contents of the lib folder will not work).
This program does not use Java 8+ modules. To run, add the following VM arguments:

```
--module-path path/to/javafx/sdk/lib
--add-modules javafx.controls,javafx.graphics,javafx.base,javafx.fxml
--add-exports=javafx.graphics/com.sun.prism=ALL-UNNAMED
--add-exports=javafx.graphics/com.sun.javafx.scene.layout=ALL-UNNAMED
--add-exports=javafx.graphics/com.sun.javafx.scene=ALL-UNNAMED
--add-exports=javafx.graphics/com.sun.javafx.sg.prism=ALL-UNNAMED
--add-exports=javafx.graphics/com.sun.scenario=ALL-UNNAMED
--add-exports=javafx.graphics/com.sun.javafx.tk=ALL-UNNAMED
--add-exports=javafx.graphics/com.sun.glass.ui=ALL-UNNAMED
--enable-native-access=ALL-UNNAMED
--enable-native-access=javafx.graphics
```

Example launch.json file for VSCode:
(note that vmArgs is a string all on one line containing the above arguments separated by spaces)
```{
    "version": "0.2.0",
    "configurations": [
    {
        "type": "java",
        "name": "Launch App",
        "request": "launch",
        "mainClass": "project.Main",
        "vmArgs": "--module-path C:/Programming/Libraries/openjfx-25.0.2_windows-x64_bin-sdk/javafx-sdk-25.0.2/lib --add-modules javafx.controls,javafx.graphics,javafx.base,javafx.fxml --add-exports=javafx.graphics/com.sun.prism=ALL-UNNAMED --add-exports=javafx.graphics/com.sun.javafx.scene.layout=ALL-UNNAMED --add-exports=javafx.graphics/com.sun.javafx.scene=ALL-UNNAMED --add-exports=javafx.graphics/com.sun.javafx.sg.prism=ALL-UNNAMED --add-exports=javafx.graphics/com.sun.scenario=ALL-UNNAMED --add-exports=javafx.graphics/com.sun.javafx.tk=ALL-UNNAMED --add-exports=javafx.graphics/com.sun.glass.ui=ALL-UNNAMED --enable-native-access=ALL-UNNAMED --enable-native-access=javafx.graphics"
    }
    ]
}
```
