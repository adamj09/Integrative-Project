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