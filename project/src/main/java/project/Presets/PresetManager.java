package project.Presets;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import project.Presets.PresetConfiguration.BodyPreset;
import project.Presets.PresetConfiguration.SatellitePreset;
import project.Presets.WorldConfiguration.BodyConfig;
import project.Renderer.World.World;
import project.UI.Popups.UnsavedChangesPopup;
import project.UI.Popups.WarningPopup;
import project.Math.Body;
import project.UI.SidebarPane;

/**
 * @author Ryan Lau
 */
public class PresetManager {

    private final PresetFileService presetFileService = new PresetFileService();
    private Path currentPresetPath;
    private WorldConfiguration lastSavedSnapshot;

    public void markCurrentStateSaved(World world, SidebarPane sidebar) {
        if (world == null) {
            lastSavedSnapshot = null;
            return;
        }
        lastSavedSnapshot = world.toWorldConfiguration(getUIConfig(sidebar));
    }

    public void savePresetAs(Stage stage, World world, SidebarPane sidebar) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save preset");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Preset Files", "*.preset"));
        chooser.setInitialFileName(world.getName() + ".preset");

        File selectedFile = chooser.showSaveDialog(stage);
        if (selectedFile == null) {
            return;
        }

        Path selectedPath = selectedFile.toPath();
        if (!selectedPath.toString().toLowerCase().endsWith(".preset")) {
            selectedPath = Path.of(selectedPath.toString() + ".preset");
        }

        saveToPath(selectedPath, world, sidebar);
    }

    public void savePreset(Stage stage, World world, SidebarPane sidebar) {
        if (currentPresetPath == null) {
            savePresetAs(stage, world, sidebar);
            return;
        }

        saveToPath(currentPresetPath, world, sidebar);
    }

    public World loadPreset(Stage stage, SidebarPane sidebar) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Load preset");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Preset Files", "*.preset"));

        File selectedFile = chooser.showOpenDialog(stage);
        if (selectedFile == null) {
            WarningPopup.show("Could not load preset file.");
            return null;
        }

        Path path = selectedFile.toPath();

        try {
            WorldConfiguration configuration = presetFileService.load(path);

            BodyConfig bodyConfig = configuration.getBody();
            Body body = new Body(bodyConfig.name, bodyConfig.mass, bodyConfig.radius, bodyConfig.distanceToSun, bodyConfig.eccentricity, bodyConfig.massOfSun);

            World world = new World(body, bodyConfig.color);

            if(sidebar.getBodyEntries().containsKey(body.getName())) {
                if(!UnsavedChangesPopup.confirm("A preset with this name is already loaded! Continue and overwrite that preset's data with this one?")) {
                    return null;
                }

                sidebar.removeBody(body.getName());
            }
        
            world.setPendingWorldConfiguration(configuration);
            // This loads bodies, initializes renderer buffers and starts the simulation
            world.applyPendingConfiguration();
                
            // Restore sidebar UI selection states AFTER world is initialized
            // This restores add/remove button states exactly as they were saved
            sidebar.applyWorldConfiguration(configuration);

            currentPresetPath = path;
            lastSavedSnapshot = configuration;

            return world;
        } catch (IOException ex) {
            WarningPopup.show("Could not load preset file.");
            return null;
        }
    }

    public boolean canClose(Stage stage, World world, SidebarPane sidebar) {
        if (!hasUnsavedChanges(world, sidebar)) {
            return true;
        }

        return UnsavedChangesPopup.confirm(
            "Current changes are not saved. Closing will discard them. Continue?");
    }

    private void saveToPath(Path path, World world, SidebarPane sidebar) {
        WorldConfiguration configuration = world.toWorldConfiguration(getUIConfig(sidebar));

        // Capture sidebar state so UI customizations are preserved
        PresetConfiguration presetConfig = sidebar.toPresetConfiguration();

        if(presetConfig == null) {
            // World does not exist, cancel.
            return;
        }

        HashMap<String, Boolean> satelliteActiveStates = sidebar.getSatelliteActiveStates();

        BodyPreset body = presetConfig.getBody();
        WorldConfiguration.SidebarBody sidebarBody = new WorldConfiguration.SidebarBody(body.name(), colorToHex(body.color()), body.preset(), body.mass(), body.radius());
        
        List<WorldConfiguration.SidebarSatellite> sidebarSatellites = new ArrayList<>();
        HashMap<String, SatellitePreset> sats = presetConfig.getSatellites();

        // Build complete satellite config list from sidebar (source of truth for what exists)
        // matching with World simulation data for orbital info
        List<WorldConfiguration.SatelliteConfig> worldSatConfigs = configuration.getSatellites();
        List<WorldConfiguration.SatelliteConfig> allSatConfigs = new ArrayList<>();

        for (Map.Entry<String, SatellitePreset> entry : sats.entrySet()) {
            SatellitePreset sp = entry.getValue();

            boolean active = satelliteActiveStates.get(entry.getKey());
            sidebarSatellites.add(new WorldConfiguration.SidebarSatellite(sp.name(), colorToHex(sp.color()), active));

            // Find matching satellite data from World simulation
            WorldConfiguration.SatelliteConfig matchingConfig = null;
            if (worldSatConfigs != null) {
                for (WorldConfiguration.SatelliteConfig sc : worldSatConfigs) {
                    if (sc.name != null && sc.name.equals(sp.name())) {
                        matchingConfig = sc;
                        break;
                    }
                }
            }

            if (matchingConfig != null) {
                matchingConfig.active = active;
                allSatConfigs.add(matchingConfig);
            } 
        }
        configuration.setSatellites(allSatConfigs);

        configuration.setSidebarBody(sidebarBody);
        configuration.setSidebarSatellites(sidebarSatellites);
        configuration.setFocusedItemName(sidebar.getFocusedItemName());

        try {
            presetFileService.save(path, configuration);
            currentPresetPath = path;
            lastSavedSnapshot = configuration;
        } catch (IOException ex) {
            WarningPopup.show("Could not save preset file.");
        }
    }

    private static String colorToHex(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    private boolean hasUnsavedChanges(World world, SidebarPane sidebar) {
        if (lastSavedSnapshot == null) {
            return false;
        }
        if (world == null) {
            return false;
        }
        // Compare only setup-level state, not simulation runtime data
        // (satellite positions, velocities, anomalies, time change every tick)
        return !setupEqual(lastSavedSnapshot, world, sidebar);
    }

    private boolean setupEqual(WorldConfiguration saved, World world, SidebarPane sidebar) {
        com.google.gson.Gson gson = GsonFactory.create();

        // Compare sidebar bodies (names, colors, selected states, mass, radius)
        String savedSideBarBody = gson.toJson(saved.getSidebarBody());
        String currentSideBarBody = gson.toJson(buildCurrentSidebarBodies(sidebar));
        if (!savedSideBarBody.equals(currentSideBarBody)) return false;

        // Compare sidebar satellites (names, colors, active states)
        String savedSats = gson.toJson(saved.getSidebarSatellites());
        String currentSats = gson.toJson(buildCurrentSidebarSatellites(sidebar));
        if (!savedSats.equals(currentSats)) return false;

        // Compare focused item
        String savedFocus = saved.getFocusedItemName();
        String currentFocus = sidebar.getFocusedItemName();
        if (savedFocus == null ? currentFocus != null : !savedFocus.equals(currentFocus)) return false;

        // Compare UI config
        WorldConfiguration.UIConfig currentUI = getUIConfig(sidebar);
        String savedUIJson = gson.toJson(saved.getUi());
        String currentUIJson = gson.toJson(currentUI);
        if (!savedUIJson.equals(currentUIJson)) return false;

        // Compare body physics setup
        String savedBody = gson.toJson(saved.getBody());
        WorldConfiguration currentConfig = world.toWorldConfiguration(currentUI);
        String currentBody = gson.toJson(currentConfig.getBody());
        if (!savedBody.equals(currentBody)) return false;

        return true;
    }

    private List<WorldConfiguration.SidebarBody> buildCurrentSidebarBodies(SidebarPane sidebar) {
        PresetConfiguration presetConfig = sidebar.toPresetConfiguration();

        List<WorldConfiguration.SidebarBody> result = new ArrayList<>();
        BodyPreset body = presetConfig.getBody();

        result.add(new WorldConfiguration.SidebarBody(body.name(), colorToHex(body.color()), body.preset(), body.mass(), body.radius()));

        return result;
    }

    private List<WorldConfiguration.SidebarSatellite> buildCurrentSidebarSatellites(SidebarPane sidebar) {
        PresetConfiguration presetConfig = sidebar.toPresetConfiguration();
        HashMap<String, Boolean> satelliteActiveStates = sidebar.getSatelliteActiveStates();
        List<WorldConfiguration.SidebarSatellite> result = new ArrayList<>();


        HashMap<String, SatellitePreset> sats = presetConfig.getSatellites();


        for (Map.Entry<String, SatellitePreset> entry : sats.entrySet()) {
            SatellitePreset preset = entry.getValue();

            boolean active = satelliteActiveStates.get(entry.getKey());

            result.add(new WorldConfiguration.SidebarSatellite(preset.name(), colorToHex(preset.color()), active));
        }
        return result;
    }

    private WorldConfiguration.UIConfig getUIConfig(SidebarPane sidebar) {
        var presetConfig = sidebar.toPresetConfiguration();
        var bottom = presetConfig.getBottomPanePreset();
        return new WorldConfiguration.UIConfig(bottom.specificTime(), bottom.timescale(), bottom.running());
    }
}
