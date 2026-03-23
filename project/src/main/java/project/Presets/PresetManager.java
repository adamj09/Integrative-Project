package project.Presets;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import project.Presets.PresetConfiguration.BodyPreset;
import project.Presets.PresetConfiguration.SatellitePreset;
import project.UI.Popups.UnsavedChangesPopup;
import project.UI.Popups.WarningPopup;
import project.UI.SidebarPane;

public class PresetManager {

    private final PresetFileService presetFileService = new PresetFileService();
    private Path currentPresetPath;
    private PresetConfiguration lastSavedSnapshot;

    public void markCurrentStateSaved(SidebarPane sidebar) {
        lastSavedSnapshot = sidebar.toPresetConfiguration();
    }

    public void savePresetAs(Stage stage, SidebarPane sidebar) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save preset");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Preset Files", "*.preset"));
        chooser.setInitialFileName("simulation-preset.preset");

        File selectedFile = chooser.showSaveDialog(stage);
        if (selectedFile == null) {
            return;
        }

        Path selectedPath = selectedFile.toPath();
        if (!selectedPath.toString().toLowerCase().endsWith(".preset")) {
            selectedPath = Path.of(selectedPath.toString() + ".preset");
        }

        saveToPath(selectedPath, sidebar);
    }

    public void savePreset(Stage stage, SidebarPane sidebar) {
        if (currentPresetPath == null) {
            savePresetAs(stage, sidebar);
            return;
        }

        saveToPath(currentPresetPath, sidebar);
    }

    public void loadPreset(Stage stage, SidebarPane sidebar) {
        if (hasUnsavedChanges(sidebar)) {
            boolean shouldContinue = UnsavedChangesPopup.confirm(
                "Current changes are not saved. Loading a preset will discard them. Continue?");
            if (!shouldContinue) {
                return;
            }
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Load preset");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Preset Files", "*.preset"));

        File selectedFile = chooser.showOpenDialog(stage);
        if (selectedFile == null) {
            return;
        }

        Path path = selectedFile.toPath();

        try {
            PresetConfiguration configuration = presetFileService.load(path);
            sidebar.applyPresetConfiguration(configuration);
            currentPresetPath = path;
            lastSavedSnapshot = configuration;
        } catch (IOException ex) {
            WarningPopup.show("Could not load preset file.");
        }
    }

    public boolean canClose(Stage stage, SidebarPane sidebar) {
        if (!hasUnsavedChanges(sidebar)) {
            return true;
        }

        return UnsavedChangesPopup.confirm(
            "Current changes are not saved. Closing will discard them. Continue?");
    }

    private void saveToPath(Path path, SidebarPane sidebar) {
        PresetConfiguration configuration = sidebar.toPresetConfiguration();

        try {
            presetFileService.save(path, configuration);
            currentPresetPath = path;
            lastSavedSnapshot = configuration;
        } catch (IOException ex) {
            WarningPopup.show("Could not save preset file.");
        }
    }

    private boolean hasUnsavedChanges(SidebarPane sidebar) {
        if (lastSavedSnapshot == null) {
            return true;
        }

        PresetConfiguration current = sidebar.toPresetConfiguration();
        return !areConfigurationsEqual(lastSavedSnapshot, current);
    }

    private boolean areConfigurationsEqual(PresetConfiguration left, PresetConfiguration right) {
        return areBodiesEqual(left.getBodies(), right.getBodies())
            && areSatellitesEqual(left.getSatellites(), right.getSatellites())
            && left.getBottomPanePreset().equals(right.getBottomPanePreset());
    }

    private boolean areBodiesEqual(java.util.List<BodyPreset> left, java.util.List<BodyPreset> right) {
        if (left.size() != right.size()) {
            return false;
        }

        for (int index = 0; index < left.size(); index++) {
            if (!left.get(index).equals(right.get(index))) {
                return false;
            }
        }

        return true;
    }

    private boolean areSatellitesEqual(java.util.List<SatellitePreset> left, java.util.List<SatellitePreset> right) {
        if (left.size() != right.size()) {
            return false;
        }

        for (int index = 0; index < left.size(); index++) {
            if (!left.get(index).equals(right.get(index))) {
                return false;
            }
        }

        return true;
    }
}
