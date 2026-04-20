package project.Presets;

import java.util.HashMap;

import javafx.scene.paint.Color;

public class PresetConfiguration {
    private final BodyPreset body;
    private final HashMap<String, SatellitePreset> satellites;
    private final BottomPanePreset bottomPanePreset;

    public PresetConfiguration(
        BodyPreset body,
        HashMap<String, SatellitePreset> satellites,
        BottomPanePreset bottomPanePreset
    ) {
        this.body = body;
        this.satellites = satellites;
        this.bottomPanePreset = bottomPanePreset;
    }

    public BodyPreset getBody() {
        return body;
    }

    public HashMap<String, SatellitePreset> getSatellites() {
        return satellites;
    }

    public BottomPanePreset getBottomPanePreset() {
        return bottomPanePreset;
    }

    public record BodyPreset(String name, Color color, boolean preset, double mass, double radius) {}

    public record SatellitePreset(String name, Color color) {}

    public record BottomPanePreset(String specificTime, String timescale, boolean running) {}
}
