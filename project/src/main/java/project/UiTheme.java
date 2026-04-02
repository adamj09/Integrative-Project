package project;

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
    ),
    MERCURY(
        "Mercury",
        "#A9A9A9", // main
        "#C0C0C0",
        "#D3D3D3",
        "#808080",
        "#B0B0B0",
        "#E0E0E0",
        "#FFFFFF",
        "#6E6E6E",
        "#F5F5F5",
        "#BEBEBE",
        "#E8E8E8",
        "#B0B0B0",
        "#FF5C5C"
    ),
    VENUS(
        "Venus",
        "#e78121e4", // main
        "#d67616",
        "#FFD180",
        "#b6520b",
        "#FFB84D",
        "#FFE0B2",
        "#FFF3E0",
        "#B25C00",
        "#FFF8E1",
        "#FFB300",
        "#FFECB3",
        "#FF9800",
        "#FF7262"
    ),
    EARTH(
        "Earth",
        "#3A7BD5", // main
        "#5CA9FA",
        "#A7C7E7",
        "#2C5AA0",
        "#6EC6FF",
        "#B3E5FC",
        "#E1F5FE",
        "#1B3A5B",
        "#F0F8FF",
        "#1976D2",
        "#B0C4DE",
        "#64B5F6",
        "#FF7F6E"
    ),
    MARS(
        "Mars",
        "#C1440E", // main
        "#E25822",
        "#FF7F50",
        "#A63A00",
        "#FF8C69",
        "#FFDAB9",
        "#FFF5EE",
        "#8B2C02",
        "#FFF0E1",
        "#FF7043",
        "#E9967A",
        "#FF5722",
        "#FF7262"
    ),
    JUPITER(
        "Jupiter",
        "#C2B280", // main
        "#E5D9B6",
        "#F5F5DC",
        "#A89F68",
        "#D2B48C",
        "#FFF8DC",
        "#FAF0E6",
        "#8B7B3A",
        "#FFF9E3",
        "#BDB76B",
        "#F0E68C",
        "#EEDD82",
        "#FF6666"
    ),
    SATURN(
        "Saturn",
        "#D2C295", // main
        "#EDE3B9",
        "#F5F5DC",
        "#B8A77A",
        "#E6D8AD",
        "#FFF8DC",
        "#FAF0E6",
        "#A89F68",
        "#FFF9E3",
        "#D2B48C",
        "#F0E68C",
        "#EEDD82",
        "#FF5C5C"
    ),
    URANUS(
        "Uranus",
        "#7FDBFF", // main
        "#B2EBF2",
        "#E0FFFF",
        "#00B8D4",
        "#4DD0E1",
        "#B2EBF2",
        "#E0F7FA",
        "#00838F",
        "#F0FFFF",
        "#00ACC1",
        "#B2DFDB",
        "#4DD0E1",
        "#FF7F6E"
    ),
    NEPTUNE(
        "Neptune",
        "#4169E1", // main
        "#5A9BF6",
        "#B0C4DE",
        "#27408B",
        "#6495ED",
        "#B0E0E6",
        "#E6F0FA",
        "#27408B",
        "#F0F8FF",
        "#4682B4",
        "#B0C4DE",
        "#64B5F6",
        "#FF6666"
    ),

    PLUTO(
        "Pluto",
        "#B0B0B0", // main
        "#D3D3D3",
        "#E0E0E0",
        "#A9A9A9",
        "#C0C0C0",
        "#E8E8E8",
        "#F5F5F5",
        "#6E6E6E",
        "#F0F0F0",
        "#BEBEBE",
        "#E8E8E8",
        "#B0B0B0",
        "#FF5C5C"
    );

    private final String label;
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

    public static UiTheme fromStoredValue(String value) {
        if (value == null || value.isBlank()) {
            return MIDNIGHT;
        }

        try {
            return UiTheme.valueOf(value);
        } catch (IllegalArgumentException ex) {
            return MIDNIGHT;
        }
    }

    @Override
    public String toString() {
        return label;
    }
}