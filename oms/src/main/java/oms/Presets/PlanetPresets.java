package oms.Presets;

import org.joml.Vector3f;

import javafx.scene.paint.Color;
import oms.Math.Body;
import oms.Renderer.World.World;
import oms.SimulationPool;
import oms.UI.SidebarPane;
import oms.UI.Popups.UnsavedChangesPopup;

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

    /**
     * Loads a preset into the SimulationPool.
     * 
     * @param pool            SimulationPool to load this preset into.
     * @param sidebar         the sidebar altered by this loading.
     * @param name            the name of the preset.
     * @param mass            the mass of the preset's body.
     * @param diameter        the diameter of the preset's body.
     * @param semiMajorAxisKm the semi-major axis (kilometers) of the preset's
     *                        body's orbit.
     * @param eccentricity    the eccentricity of the preset's body's orbit.
     * @param renderColor     the colour of the preset's 3D body.
     * @param uiColor         the colour used in the UI to represent the preset's
     *                        body.
     */
    private static void load(SimulationPool pool, SidebarPane sidebar,
            String name, double mass, double diameter,
            double semiMajorAxisKm, double eccentricity,
            Vector3f renderColor, Color uiColor) {

        Body body = new Body(name, mass, diameter / 2.0, semiMajorAxisKm, eccentricity);
        
        World currentWorld = pool.getCurrentWorld();
        boolean wasWorldRunning = false;
        if (currentWorld != null) {
            wasWorldRunning = currentWorld.isWorldRunning();
        }

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

        pool.createWorld(name, body, renderColor);

        sidebar.addBodyCard(name, uiColor, true, mass, diameter / 2.0);

        if(wasWorldRunning) {
            pool.startWorld();
        }
    }

    /**
     * Loads the Mercury preset.
     * 
     * @param pool    the SimulationPool to add this preset to.
     * @param sidebar the SidebarPane bar to add this preset to.
     */
    public static void loadMercury(SimulationPool pool, SidebarPane sidebar) {
        load(pool, sidebar,
                "Mercury", 0.330e24, 4879,
                57.9e6, 0.206,
                new Vector3f(0.7f, 0.5f, 0.3f), Color.rgb(179, 128, 77));
    }

    /**
     * Loads the Venus preset.
     * 
     * @param pool    the SimulationPool to add this preset to.
     * @param sidebar the SidebarPane bar to add this preset to.
     */
    public static void loadVenus(SimulationPool pool, SidebarPane sidebar) {
        load(pool, sidebar,
                "Venus", 4.87e24, 12104,
                108.2e6, 0.007,
                new Vector3f(0.9f, 0.8f, 0.5f), Color.rgb(230, 204, 128));
    }

    /**
     * Loads the Earth preset.
     * 
     * @param pool    the SimulationPool to add this preset to.
     * @param sidebar the SidebarPane bar to add this preset to.
     */
    public static void loadEarth(SimulationPool pool, SidebarPane sidebar) {
        load(pool, sidebar,
                "Earth", 5.97e24, 12756,
                149.6e6, 0.017,
                new Vector3f(0.2f, 0.5f, 0.8f), Color.rgb(51, 128, 204));
    }

    /**
     * Loads the Mars preset.
     * 
     * @param pool    the SimulationPool to add this preset to.
     * @param sidebar the SidebarPane bar to add this preset to.
     */
    public static void loadMars(SimulationPool pool, SidebarPane sidebar) {
        load(pool, sidebar,
                "Mars", 0.642e24, 6792,
                227.9e6, 0.094,
                new Vector3f(0.8f, 0.3f, 0.2f), Color.rgb(204, 77, 51));
    }

    /**
     * Loads the Jupiter preset.
     * 
     * @param pool    the SimulationPool to add this preset to.
     * @param sidebar the SidebarPane bar to add this preset to.
     */
    public static void loadJupiter(SimulationPool pool, SidebarPane sidebar) {
        load(pool, sidebar,
                "Jupiter", 1898e24, 142984,
                778.6e6, 0.049,
                new Vector3f(0.9f, 0.7f, 0.5f), Color.rgb(230, 179, 128));
    }

    /**
     * Loads the Saturn preset.
     * 
     * @param pool    the SimulationPool to add this preset to.
     * @param sidebar the SidebarPane bar to add this preset to.
     */
    public static void loadSaturn(SimulationPool pool, SidebarPane sidebar) {
        load(pool, sidebar,
                "Saturn", 568e24, 120536,
                1433.5e6, 0.057,
                new Vector3f(0.95f, 0.85f, 0.6f), Color.rgb(242, 217, 153));
    }

    /**
     * Loads the Uranus preset.
     * 
     * @param pool    the SimulationPool to add this preset to.
     * @param sidebar the SidebarPane bar to add this preset to.
     */
    public static void loadUranus(SimulationPool pool, SidebarPane sidebar) {
        load(pool, sidebar,
                "Uranus", 86.8e24, 51118,
                2872.5e6, 0.046,
                new Vector3f(0.6f, 0.9f, 0.9f), Color.rgb(153, 230, 230));
    }

    /**
     * Loads the Neptune preset.
     * 
     * @param pool    the SimulationPool to add this preset to.
     * @param sidebar the SidebarPane bar to add this preset to.
     */
    public static void loadNeptune(SimulationPool pool, SidebarPane sidebar) {
        load(pool, sidebar,
                "Neptune", 102e24, 49528,
                4495.1e6, 0.010,
                new Vector3f(0.2f, 0.4f, 0.8f), Color.rgb(51, 102, 204));
    }
}
