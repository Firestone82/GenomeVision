package cz.mik0486.semestralproject.viewer.settings;

import cz.mik0486.semestralproject.utils.ColorUtils;
import cz.mik0486.semestralproject.viewer.analyzer.Analyzer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

import java.awt.*;
import java.io.*;
import java.util.Properties;

@Data
@AllArgsConstructor
public class Settings {
    private final Analyzer analyzer;

    private final File configFile;
    private Color emptyCellColor = Color.LIGHT_GRAY;
    private Color gradientStartColor = new Color(255, 255, 0);
    private Color gradientEndColor = new Color(255, 0, 0);

    private final SettingsDialog settingsDialog;

    @Setter
    private Runnable onChange;

    public Settings(Analyzer analyzer) {
        this.analyzer = analyzer;
        this.configFile = new File("settings.properties");

        try {
            if (!configFile.exists()) {
                save();
            }

            load();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to create/load settings file: " + configFile, e);
        }

        this.settingsDialog = new SettingsDialog(this);
    }

    /**
     * Save current settings to the given file (overwrites if exists).
     */
    public void save() throws IOException {
        Properties props = new Properties();
        props.setProperty("emptyCellColor", ColorUtils.toHex(emptyCellColor));
        props.setProperty("gradientStartColor", ColorUtils.toHex(gradientStartColor));
        props.setProperty("gradientEndColor", ColorUtils.toHex(gradientEndColor));

        try (OutputStream out = new FileOutputStream(configFile)) {
            props.store(out, "Application SettingsData");
        }
    }

    /**
     * Load settings from the given file. Any missing key will leave
     * the corresponding field at its current value.
     */
    public void load() throws IOException {
        Properties props = new Properties();
        try (InputStream in = new FileInputStream(configFile)) {
            props.load(in);
        }

        emptyCellColor = ColorUtils.parseHex(props.getProperty("emptyCellColor", ColorUtils.toHex(emptyCellColor)));
        gradientStartColor = ColorUtils.parseHex(props.getProperty("gradientStartColor", ColorUtils.toHex(gradientStartColor)));
        gradientEndColor = ColorUtils.parseHex(props.getProperty("gradientEndColor", ColorUtils.toHex(gradientEndColor)));
    }
}
