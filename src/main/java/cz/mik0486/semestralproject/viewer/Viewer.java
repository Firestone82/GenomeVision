package cz.mik0486.semestralproject.viewer;

import com.formdev.flatlaf.FlatLightLaf;
import cz.mik0486.semestralproject.viewer.analyzer.Analyzer;
import cz.mik0486.semestralproject.viewer.menu.Menu;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;

@Slf4j
@Getter
public class Viewer extends JFrame {

    private final Analyzer analyzer;

    public Viewer() {
        super("DNA Viewer: Semestral Project");

        // Change look and feel
        FlatLightLaf.setup();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setJMenuBar(new Menu(this));

        // Initialize scan selector
        this.analyzer = new Analyzer(this);
        this.analyzer.initUI(this);
    }

    public void exit() {
        analyzer.closeFile();
        System.exit(0);
    }
}
