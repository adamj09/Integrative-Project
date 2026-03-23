package project.Presets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javafx.scene.paint.Color;
import project.Presets.PresetConfiguration.BodyPreset;
import project.Presets.PresetConfiguration.SatellitePreset;

public class PresetFileService {

    private static final String VERSION_KEY = "preset.version";
    private static final String BODIES_COUNT_KEY = "bodies.count";
    private static final String SATELLITES_COUNT_KEY = "satellites.count";
    private static final String BODIES_PREFIX = "body.";
    private static final String SATELLITES_PREFIX = "satellite.";
    private static final String BOTTOM_SPECIFIC_TIME_KEY = "bottom.specificTime";
    private static final String BOTTOM_TIMESCALE_KEY = "bottom.timescale";
    private static final String BOTTOM_RUNNING_KEY = "bottom.running";

    public void save(Path filePath, PresetConfiguration configuration) throws IOException {
        Properties properties = new Properties();
        properties.setProperty(VERSION_KEY, "1");

        List<BodyPreset> bodies = configuration.getBodies();
        properties.setProperty(BODIES_COUNT_KEY, Integer.toString(bodies.size()));
        for (int index = 0; index < bodies.size(); index++) {
            BodyPreset body = bodies.get(index);
            String prefix = BODIES_PREFIX + index + ".";
            properties.setProperty(prefix + "name", body.name());
            properties.setProperty(prefix + "color", body.color().toString());
            properties.setProperty(prefix + "preset", Boolean.toString(body.preset()));
        }

        List<SatellitePreset> satellites = configuration.getSatellites();
        properties.setProperty(SATELLITES_COUNT_KEY, Integer.toString(satellites.size()));
        for (int index = 0; index < satellites.size(); index++) {
            SatellitePreset satellite = satellites.get(index);
            String prefix = SATELLITES_PREFIX + index + ".";
            properties.setProperty(prefix + "name", satellite.name());
            properties.setProperty(prefix + "color", satellite.color().toString());
        }

        PresetConfiguration.BottomPanePreset bottomPanePreset = configuration.getBottomPanePreset();
        properties.setProperty(BOTTOM_SPECIFIC_TIME_KEY, bottomPanePreset.specificTime());
        properties.setProperty(BOTTOM_TIMESCALE_KEY, bottomPanePreset.timescale());
        properties.setProperty(BOTTOM_RUNNING_KEY, Boolean.toString(bottomPanePreset.running()));

        try (OutputStream outputStream = Files.newOutputStream(filePath)) {
            properties.store(outputStream, "Orbital Motion Simulator Preset");
        }
    }

    public PresetConfiguration load(Path filePath) throws IOException {
        Properties properties = new Properties();
        try (InputStream inputStream = Files.newInputStream(filePath)) {
            properties.load(inputStream);
        }

        int bodiesCount = parseCount(properties, BODIES_COUNT_KEY);
        int satellitesCount = parseCount(properties, SATELLITES_COUNT_KEY);

        List<BodyPreset> bodies = new ArrayList<>();
        for (int index = 0; index < bodiesCount; index++) {
            String prefix = BODIES_PREFIX + index + ".";
            String name = properties.getProperty(prefix + "name", "Body-" + index);
            Color color = parseColor(properties, prefix + "color", Color.TOMATO);
            boolean preset = Boolean.parseBoolean(properties.getProperty(prefix + "preset", "false"));
            bodies.add(new BodyPreset(name, color, preset));
        }

        List<SatellitePreset> satellites = new ArrayList<>();
        for (int index = 0; index < satellitesCount; index++) {
            String prefix = SATELLITES_PREFIX + index + ".";
            String name = properties.getProperty(prefix + "name", "Sat-" + index);
            Color color = parseColor(properties, prefix + "color", Color.CORNFLOWERBLUE);
            satellites.add(new SatellitePreset(name, color));
        }

        PresetConfiguration.BottomPanePreset bottomPanePreset = new PresetConfiguration.BottomPanePreset(
            properties.getProperty(BOTTOM_SPECIFIC_TIME_KEY, ""),
            properties.getProperty(BOTTOM_TIMESCALE_KEY, "1x"),
            Boolean.parseBoolean(properties.getProperty(BOTTOM_RUNNING_KEY, "false"))
        );

        return new PresetConfiguration(bodies, satellites, bottomPanePreset);
    }

    private int parseCount(Properties properties, String key) throws IOException {
        try {
            return Integer.parseInt(properties.getProperty(key, "0"));
        } catch (NumberFormatException ex) {
            throw new IOException("Invalid preset count for key: " + key, ex);
        }
    }

    private Color parseColor(Properties properties, String key, Color defaultColor) throws IOException {
        try {
            return Color.valueOf(properties.getProperty(key, defaultColor.toString()));
        } catch (IllegalArgumentException ex) {
            throw new IOException("Invalid color value for key: " + key, ex);
        }
    }
}
