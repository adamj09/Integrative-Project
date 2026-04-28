package project;

/**
 * Enum containing colours for each UI theme.
 * 
 * @author Ryan Lau
 */
public enum UiTheme {
    MIDNIGHT(
        "Midnight",
        "#1a1a2e",
        "#2a2a4a",
        "#3a3a5a",
        "#444466",
        "#575781",
        "#8888ff",
        "#c0c0e0",
        "#4a4a6a",
        "#e0e0ff",
        "#8888bb",
        "#252540",
        "#555577",
        "#ff6666"
    ),
    FOREST(
        "Forest",
        "#15211c",
        "#22342c",
        "#2e4a3f",
        "#3b5f50",
        "#4f7c68",
        "#7ec7a5",
        "#d7efe4",
        "#355647",
        "#edf8f2",
        "#92bca8",
        "#1b2c25",
        "#4a6d5e",
        "#ff7f6e"
    ),
    SUNSET(
        "Sunset",
        "#2c1a1a",
        "#472625",
        "#6a3630",
        "#7f4a3f",
        "#b6684d",
        "#ffb56b",
        "#ffe2cc",
        "#925648",
        "#fff0de",
        "#e0a184",
        "#3b2221",
        "#91604f",
        "#ff7262"
    ),
    ICEBERG(
        "Iceberg",
        "#13202a",
        "#203744",
        "#2d4e60",
        "#3e6579",
        "#5c8faa",
        "#89d4ff",
        "#dff5ff",
        "#365a70",
        "#f0fbff",
        "#98bfd4",
        "#1b303d",
        "#5f899f",
        "#ff7b7b"
    );

    // These are commented out as they're not being used.
    /*MERCURY(
        "Mercury",
        "#6E6E6E", // main
        "#808080",
        "#9A9A9A",
        "#555555",
        "#707070",
        "#B0B0B0",
        "#C8C8C8",
        "#404040",
        "#D0D0D0",
        "#888888",
        "#B8B8B8",
        "#707070",
        "#CC3333"
    ),
    VENUS(
        "Venus",
        "#6B3608", // main
        "#A85A0A",
        "#CC9900",
        "#7A3A05",
        "#CC8800",
        "#CC9966",
        "#E8C880",
        "#7A3A00",
        "#EDD890",
        "#BB8800",
        "#CC9933",
        "#CC6600",
        "#CC3333"
    ),
    EARTH(
        "Earth",
        "#1A4A8A", // main — ocean blue
        "#2E6BBB", // mid blue
        "#4A8C6A", // ocean-land mix
        "#133870", // deep ocean
        "#3A88CC", // sky blue
        "#4A7A55", // landmass green
        "#7AAD6A", // lighter green
        "#0A2240", // deep sea
        "#A8C8A0", // pale green highlight
        "#2A6E3A", // forest green
        "#6A9A70", // muted green
        "#2A7ACC", // bright ocean
        "#CC4444"  // accent
    ),
    MARS(
        "Mars",
        "#8A2800", // main
        "#B03A10",
        "#CC5520",
        "#6E2200",
        "#CC5533",
        "#CC8855",
        "#E8C0A0",
        "#601800",
        "#E8C090",
        "#CC4422",
        "#BB6644",
        "#CC3311",
        "#CC3333"
    ),
    JUPITER(
        "Jupiter",
        "#7A6A40", // main
        "#AA9060",
        "#C8B880",
        "#666040",
        "#997755",
        "#CCB880",
        "#DDD0A0",
        "#554A20",
        "#EEE0A0",
        "#888844",
        "#BBAA44",
        "#AAAA44",
        "#CC2222"
    ),
    SATURN(
        "Saturn",
        "#8A7A55", // main
        "#AAAA66",
        "#C8B880",
        "#706050",
        "#AA9966",
        "#CCB880",
        "#DDD0A0",
        "#665040",
        "#EEE0A0",
        "#997755",
        "#BBAA44",
        "#AAAA44",
        "#CC2222"
    ),
    URANUS(
        "Uranus",
        "#007799", // main
        "#4499AA",
        "#88CCCC",
        "#006688",
        "#22AAAA",
        "#44AAAA",
        "#66BBBB",
        "#005566",
        "#99DDDD",
        "#007788",
        "#449999",
        "#22AAAA",
        "#CC4433"
    ),
    NEPTUNE(
        "Neptune",
        "#1A3A80", // main
        "#2A5599",
        "#5577AA",
        "#112860",
        "#3355AA",
        "#446688",
        "#6688AA",
        "#112860",
        "#88AACC",
        "#224488",
        "#445577",
        "#2255AA",
        "#CC2222"
    ),
    PLUTO(
        "Pluto",
        "#666666", // main
        "#888888",
        "#AAAAAA",
        "#555555",
        "#777777",
        "#BBBBBB",
        "#CCCCCC",
        "#444444",
        "#C8C8C8",
        "#888888",
        "#BBBBBB",
        "#777777",
        "#CC2222"
    );
    */

    /**
     * Name of the theme.
     */
    private final String label;

    /**
     * Hexadecimal UI colours.
     */
    private final String bgColor;
    private final String lightBgColor;
    private final String brightBgColor;
    private final String darkAccentColor;
    private final String lightAccentColor;
    private final String brightAccentColor;
    private final String textColor;
    private final String buttonColor;
    private final String keyLabelColor;
    private final String keyColor;
    private final String dataBoxBgColor;
    private final String dataBoxBorderColor;
    private final String errorColor;

    /**
     * Builds a UI theme with 
     * @param label colour of label text.
     * @param bgColor mid-brightness background colour.
     * @param lightBgColor lighter background colour.
     * @param brightBgColor lightest background colour.
     * @param darkAccentColor darker accent colour.
     * @param lightAccentColor lighter accent colour.
     * @param brightAccentColor lightest accent colour.
     * @param textColor colour of all text (except labels)
     * @param buttonColor background colour of buttons
     * @param keyLabelColor colour used for live data pane labels.
     * @param keyColor colour used for data in live data pane.
     * @param dataBoxBgColor background colour of live data pane.
     * @param dataBoxBorderColor border colour of live data pane.
     * @param errorColor colour of text in error pop-ups/warnings
     */
    UiTheme(
        String label,
        String bgColor,
        String lightBgColor,
        String brightBgColor,
        String darkAccentColor,
        String lightAccentColor,
        String brightAccentColor,
        String textColor,
        String buttonColor,
        String keyLabelColor,
        String keyColor,
        String dataBoxBgColor,
        String dataBoxBorderColor,
        String errorColor
    ) {
        this.label = label;
        this.bgColor = bgColor;
        this.lightBgColor = lightBgColor;
        this.brightBgColor = brightBgColor;
        this.darkAccentColor = darkAccentColor;
        this.lightAccentColor = lightAccentColor;
        this.brightAccentColor = brightAccentColor;
        this.textColor = textColor;
        this.buttonColor = buttonColor;
        this.keyLabelColor = keyLabelColor;
        this.keyColor = keyColor;
        this.dataBoxBgColor = dataBoxBgColor;
        this.dataBoxBorderColor = dataBoxBorderColor;
        this.errorColor = errorColor;
    }

    /**
     * Converts the UiTheme to a JavaFX CSS formatted string.
     * @return
     */
    public String toStyleString() {
        return String.join(" ",
            "-bg-color: " + bgColor + ";",
            "-light-bg-color: " + lightBgColor + ";",
            "-bright-bg-color: " + brightBgColor + ";",
            "-dark-accent-color: " + darkAccentColor + ";",
            "-light-accent-color: " + lightAccentColor + ";",
            "-bright-accent-color: " + brightAccentColor + ";",
            "-text-color: " + textColor + ";",
            "-button-color: " + buttonColor + ";",
            "-key-label-color: " + keyLabelColor + ";",
            "-key-color: " + keyColor + ";",
            "-data-box-bg-color: " + dataBoxBgColor + ";",
            "-data-box-border-color: " + dataBoxBorderColor + ";",
            "-error-color: " + errorColor + ";"
        );
    }

    /**
     * Gets a specific theme.
     * @param theme the name of the desired theme.
     * @return the theme matching the name provided, or the default theme (MIDNIGHT) if the desired theme cannot be found.
     */
    public static UiTheme fromStoredValue(String theme) {
        if (theme == null || theme.isBlank()) {
            return MIDNIGHT;
        }

        try {
            return UiTheme.valueOf(theme);
        } catch (IllegalArgumentException ex) {
            return MIDNIGHT;
        }
    }

    /**
     * @return name of the theme.
     */
    @Override
    public String toString() {
        return label;
    }
}