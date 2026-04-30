package project.Presets;

import java.util.HashMap;

import org.joml.Vector3f;

import javafx.application.Platform;
import javafx.scene.paint.Color;
import project.Math.Body;
import project.SimulationPool;
import project.UI.SidebarPane;
import project.UI.Popups.UnsavedChangesPopup;

/**
 * Planet preset configuration with accurate values from the NASA Planetary Fact
 * Sheet.
 * Each method creates a world for the given planet and adds it to the sidebar.
 *
 * Data sources (NASA Planetary Fact Sheet):
 * Mass (kg), Diameter (km), Semi-major axis (10^6 km), Orbital eccentricity
 * 
 * @author Ryan Lau
 */
public class PlanetPresets {

    private static void load(SimulationPool pool, SidebarPane sidebar,
            String name, double mass, double diameter,
            double semiMajorAxisKm, double eccentricity,
            Vector3f renderColor, Color uiColor) {

        Body body = new Body(name, mass, diameter / 2.0, semiMajorAxisKm, eccentricity);

        // Stop the current world before adding preset
        pool.stopWorld();

        // Remove the body (to overwrite) if it already exists.
        if (sidebar.getBodyEntries().containsKey(body.getName())) {
            if (!UnsavedChangesPopup.confirm(
                    "A preset with this name is already loaded! Continue and overwrite that preset's data with this one?")) {
                return;
            }

            sidebar.removeBody(body.getName());
        }

        sidebar.applyPresetConfiguration(
                new PresetConfiguration(
                        new PresetConfiguration.BodyPreset(name, uiColor, true, mass, diameter / 2.0),
                        new HashMap<>(),
                        new PresetConfiguration.BottomPanePreset("", "1x", false)));
        sidebar.selectBody(name);

        pool.createWorld(name, body, renderColor);
        pool.runWorld(name);
    }

    public static void loadMercury(SimulationPool pool, SidebarPane sidebar) {
        load(pool, sidebar,
                "Mercury", 0.330e24, 4879,
                57.9e6, 0.206,
                new Vector3f(0.7f, 0.5f, 0.3f), Color.rgb(179, 128, 77));
    }

    public static void loadVenus(SimulationPool pool, SidebarPane sidebar) {
        load(pool, sidebar,
                "Venus", 4.87e24, 12104,
                108.2e6, 0.007,
                new Vector3f(0.9f, 0.8f, 0.5f), Color.rgb(230, 204, 128));
    }

    public static void loadEarth(SimulationPool pool, SidebarPane sidebar) {
        load(pool, sidebar,
                "Earth", 5.97e24, 12756,
                149.6e6, 0.017,
                new Vector3f(0.2f, 0.5f, 0.8f), Color.rgb(51, 128, 204));
    }

    public static void loadMars(SimulationPool pool, SidebarPane sidebar) {
        load(pool, sidebar,
                "Mars", 0.642e24, 6792,
                227.9e6, 0.094,
                new Vector3f(0.8f, 0.3f, 0.2f), Color.rgb(204, 77, 51));
    }

    public static void loadJupiter(SimulationPool pool, SidebarPane sidebar) {
        load(pool, sidebar,
                "Jupiter", 1898e24, 142984,
                778.6e6, 0.049,
                new Vector3f(0.9f, 0.7f, 0.5f), Color.rgb(230, 179, 128));
    }

    public static void loadSaturn(SimulationPool pool, SidebarPane sidebar) {
        load(pool, sidebar,
                "Saturn", 568e24, 120536,
                1433.5e6, 0.057,
                new Vector3f(0.95f, 0.85f, 0.6f), Color.rgb(242, 217, 153));
    }

    public static void loadUranus(SimulationPool pool, SidebarPane sidebar) {
        load(pool, sidebar,
                "Uranus", 86.8e24, 51118,
                2872.5e6, 0.046,
                new Vector3f(0.6f, 0.9f, 0.9f), Color.rgb(153, 230, 230));
    }

    public static void loadNeptune(SimulationPool pool, SidebarPane sidebar) {
        load(pool, sidebar,
                "Neptune", 102e24, 49528,
                4495.1e6, 0.010,
                new Vector3f(0.2f, 0.4f, 0.8f), Color.rgb(51, 102, 204));
    }
}
