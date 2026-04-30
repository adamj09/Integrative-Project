package oms.Presets;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;

/**
 * Class for saving and loading WorldConfigurations.
 * 
 * @author Ryan Lau
 */
public class PresetFileService {
    /**
     * JSON file to save data to, or load data from.
     */
    private final Gson gson = GsonFactory.create();

    /**
     * Save data to JSON file.
     * 
     * @param filePath      path to save file to.
     * @param configuration data to save.
     * @throws IOException if the JSON file cannot be written to.
     */
    public void save(Path filePath, WorldConfiguration configuration) throws IOException {
        try (Writer writer = Files.newBufferedWriter(filePath)) {
            gson.toJson(configuration, writer);
        }
    }

    /**
     * Load data from JSON file.
     * 
     * @param filePath path to load file from.
     * @return loaded data.
     * @throws IOException if the JSON file cannot be read.
     */
    public WorldConfiguration load(Path filePath) throws IOException {
        try (Reader reader = Files.newBufferedReader(filePath)) {
            WorldConfiguration config = gson.fromJson(reader, WorldConfiguration.class);
            if (config == null) {
                throw new IOException("Empty or invalid preset file.");
            }
            return config;
        }
    }
}
