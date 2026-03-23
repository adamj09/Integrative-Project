package project.Presets;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.paint.Color;

public class PresetConfiguration {
    private final List<BodyPreset> bodies;
    private final List<SatellitePreset> satellites;
    private final BottomPanePreset bottomPanePreset;

    public PresetConfiguration(
        List<BodyPreset> bodies,
        List<SatellitePreset> satellites,
        BottomPanePreset bottomPanePreset
    ) {
        this.bodies = new ArrayList<>(bodies);
        this.satellites = new ArrayList<>(satellites);
        this.bottomPanePreset = bottomPanePreset;
    }

    public List<BodyPreset> getBodies() {
        return new ArrayList<>(bodies);
    }

    public List<SatellitePreset> getSatellites() {
        return new ArrayList<>(satellites);
    }

    public BottomPanePreset getBottomPanePreset() {
        return bottomPanePreset;
    }

    public record BodyPreset(String name, Color color, boolean preset) {}

    public record SatellitePreset(String name, Color color) {}

    public record BottomPanePreset(String specificTime, String timescale, boolean running) {}
}
