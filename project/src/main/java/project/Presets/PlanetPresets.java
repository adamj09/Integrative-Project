package project.Presets;

import org.joml.Vector3f;

import javafx.scene.paint.Color;
import project.Math.Body;
import project.SimulationPool;
import project.UI.SidebarPane;

/**
 * Planet preset configuration with accurate values from the Planetary Fact Sheet
 * Handles creation and loading of solar system planet bodies
 */
public class PlanetPresets {

    public static void loadMercury(SimulationPool pool, SidebarPane sidebar) {
        javafx.application.Platform.runLater(() -> {
            Body mercury = new Body(
                    "Mercury",
                    0.330e24,
                    4879 / 2.0,
                    57.9e6,
                    0.205
            );
            pool.createWorld("Mercury", mercury, new Vector3f(0.7f, 0.5f, 0.3f));
            pool.runWorld("Mercury");
            sidebar.addBodyCard("Mercury", Color.rgb(179, 128, 77), true, 0.330e24, 4879 / 2.0, true);
        });
    }

    public static void loadVenus(SimulationPool pool, SidebarPane sidebar) {
        javafx.application.Platform.runLater(() -> {
            Body venus = new Body(
                    "Venus",
                    4.87e24,
                    12104 / 2.0,
                    108.2e6,
                    0.007
            );
            pool.createWorld("Venus", venus, new Vector3f(0.9f, 0.8f, 0.5f));
            pool.runWorld("Venus");
            sidebar.addBodyCard("Venus", Color.rgb(230, 204, 128), true, 4.87e24, 12104 / 2.0, true);
        });
    }

    public static void loadEarth(SimulationPool pool, SidebarPane sidebar) {
        javafx.application.Platform.runLater(() -> {
            Body earth = new Body(
                    "Earth",
                    5.97e24,
                    12756 / 2.0,
                    149.6e6,
                    0.017
            );
            pool.createWorld("Earth", earth, new Vector3f(0.2f, 0.5f, 0.8f));
            pool.runWorld("Earth");
            sidebar.addBodyCard("Earth", Color.rgb(51, 128, 204), true, 5.97e24, 12756 / 2.0, true);
        });
    }

    public static void loadMars(SimulationPool pool, SidebarPane sidebar) {
        javafx.application.Platform.runLater(() -> {
            Body mars = new Body(
                    "Mars",
                    0.642e24,
                    6792 / 2.0,
                    227.9e6,
                    0.094
            );
            pool.createWorld("Mars", mars, new Vector3f(0.8f, 0.3f, 0.2f));
            pool.runWorld("Mars");
            sidebar.addBodyCard("Mars", Color.rgb(204, 77, 51), true, 0.642e24, 6792 / 2.0, true);
        });
    }

    public static void loadJupiter(SimulationPool pool, SidebarPane sidebar) {
        javafx.application.Platform.runLater(() -> {
            Body jupiter = new Body(
                    "Jupiter",
                    1898e24,
                    142984 / 2.0,
                    778.6e6,
                    0.049
            );
            pool.createWorld("Jupiter", jupiter, new Vector3f(0.9f, 0.7f, 0.5f));
            pool.runWorld("Jupiter");
            sidebar.addBodyCard("Jupiter", Color.rgb(230, 179, 128), true, 1898e24, 142984 / 2.0, true);
        });
    }

    public static void loadSaturn(SimulationPool pool, SidebarPane sidebar) {
        javafx.application.Platform.runLater(() -> {
            Body saturn = new Body(
                    "Saturn",
                    568e24,
                    120536 / 2.0,
                    1433.5e6,
                    0.057
            );
            pool.createWorld("Saturn", saturn, new Vector3f(0.95f, 0.85f, 0.6f));
            pool.runWorld("Saturn");
            sidebar.addBodyCard("Saturn", Color.rgb(242, 217, 153), true, 568e24, 120536 / 2.0, true);
        });
    }

    public static void loadUranus(SimulationPool pool, SidebarPane sidebar) {
        javafx.application.Platform.runLater(() -> {
            Body uranus = new Body(
                    "Uranus",
                    86.8e24,
                    51118 / 2.0,
                    2872.5e6,
                    0.046
            );
            pool.createWorld("Uranus", uranus, new Vector3f(0.6f, 0.9f, 0.9f));
            pool.runWorld("Uranus");
            sidebar.addBodyCard("Uranus", Color.rgb(153, 230, 230), true, 86.8e24, 51118 / 2.0, true);
        });
    }

    public static void loadNeptune(SimulationPool pool, SidebarPane sidebar) {
        javafx.application.Platform.runLater(() -> {
            Body neptune = new Body(
                    "Neptune",
                    102e24,
                    49528 / 2.0,
                    4495.1e6,
                    0.011
            );
            pool.createWorld("Neptune", neptune, new Vector3f(0.2f, 0.4f, 0.8f));
            pool.runWorld("Neptune");
            sidebar.addBodyCard("Neptune", Color.rgb(51, 102, 204), true, 102e24, 49528 / 2.0, true);
        });
    }
}