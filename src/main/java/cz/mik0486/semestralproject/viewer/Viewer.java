package cz.mik0486.semestralproject.viewer;

import com.formdev.flatlaf.FlatLightLaf;
import cz.mik0486.semestralproject.viewer.analyzer.Analyzer;
import cz.mik0486.semestralproject.viewer.menu.Menu;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

@Slf4j
@Getter
public class Viewer extends JFrame {

    private final Analyzer analyzer;

    public Viewer() {
        super("DNA Viewer: Semestral Project");

        // Change look and feel
        FlatLightLaf.setup();
        log.info("Setting up FlatLightLaf look and feel");

        // Get monitor max resolution
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        log.info("Screen resolution: {}x{}", screenSize.width, screenSize.height);

        // Set up window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(screenSize.width / 2, (int) (screenSize.height / 1.5));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setJMenuBar(new Menu(this));

        // Set application icon
        try {
            String path = "src/main/resources/icons/app-icon-5.png";
            setIconImage(ImageIO.read(new File(path)));
        } catch (IOException e) {
            log.error("Failed to set application icon.", e);
        }

        // Initialize scan selector
        this.analyzer = new Analyzer(this);
        this.analyzer.initUI(this);

        // Load file
        analyzer.loadFile(new File("src/main/resources/data/aml_ratios_trans_small.csv"));

        // Call exit method when window is closed
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                exit();
            }
        });
    }

    public void exit() {
        log.info("Exiting application.");

        analyzer.closeFile();
        System.exit(0);
    }
}
