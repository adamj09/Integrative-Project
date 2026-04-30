package oms.Presets;

import java.util.HashMap;

import javafx.scene.paint.Color;

/**
 * Holds all the data required for a preset.
 * 
 * @author Ryan Lau
 */
public class PresetConfiguration {
    /**
     * The preset body's data.
     */
    private final BodyPreset body;

    /**
     * A HashMap containing each satellite's data, keyed by satellite name.
     */
    private final HashMap<String, SatellitePreset> satellites;

    /**
     * The preset for the BottomPane, used to save simulation settings.
     */
    private final BottomPanePreset bottomPanePreset;

    /**
     * Creates a new PresetConfiguration with all the required data.
     * 
     * @param body             the preset body's data.
     * @param satellites       a HashMap containing each satellite's data, keyed by
     *                         satellite name.
     * @param bottomPanePreset the preset for the BottomPane, used to save
     *                         simulation settings.
     */
    public PresetConfiguration(BodyPreset body, HashMap<String, SatellitePreset> satellites,
            BottomPanePreset bottomPanePreset) {
        this.body = body;
        this.satellites = satellites;
        this.bottomPanePreset = bottomPanePreset;
    }

    /**
     * @return the body preset.
     */
    public BodyPreset getBody() {
        return body;
    }

    /**
     * @return all satellite presets.
     */
    public HashMap<String, SatellitePreset> getSatellites() {
        return satellites;
    }

    /**
     * @return the bottom pane preset.
     */
    public BottomPanePreset getBottomPanePreset() {
        return bottomPanePreset;
    }

    /**
     * Definitions of data held by a BodyPreset.
     */
    public record BodyPreset(String name, Color color, boolean preset, double mass, double radius) {
    }

    /**
     * Definitions of data held by a SatellitePreset.
     */
    public record SatellitePreset(String name, Color color) {
    }

    /**
     * Definitions of data held by a BottomPanePreset.
     */
    public record BottomPanePreset(String specificTime, String timescale, boolean running) {
    }
}
