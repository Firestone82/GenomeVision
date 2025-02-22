package cz.mik0486.semestralproject.viewer;

import com.formdev.flatlaf.FlatLightLaf;
import cz.mik0486.semestralproject.data.DataHandler;
import cz.mik0486.semestralproject.data.exception.ScanLoadException;
import cz.mik0486.semestralproject.data.holder.Sample;
import cz.mik0486.semestralproject.viewer.analyzer.Analyzer;
import cz.mik0486.semestralproject.viewer.analyzer.dialog.FileLoadDialog;
import cz.mik0486.semestralproject.viewer.menu.Menu;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

@Slf4j
@Getter
public class Viewer extends JFrame {

    // Data
    private final List<Sample> samples = new ArrayList<>();

    // Windows
    private final Analyzer analyzer;

    public Viewer() {
        super("DNA Viewer: Semestral Project");

        // Change look and feel
        FlatLightLaf.setup();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Initialize menu
        Menu menu = new Menu(this);
        this.setJMenuBar(menu);

        // Initialize scan selector
        this.analyzer = new Analyzer(this);

        this.add(analyzer.getPanel());
    }

    public void openFile(File file) {
        long startTime = System.currentTimeMillis();

        SwingWorker<List<Sample>, Void> worker = new SwingWorker<>() {

            @Override
            protected List<Sample> doInBackground() throws ScanLoadException {
                return DataHandler.loadFile(file, this::setProgress);
            }
        };

        log.info("Loading CSV file '{}'", file.getPath());
        worker.execute();

        JDialog loadingDialog = new FileLoadDialog(this, worker);
        loadingDialog.setVisible(true);

        try {
            List<Sample> data = worker.get();

            samples.clear();
            samples.addAll(data);
            analyzer.setData(data);

            log.info("Finished loading CSV file in {}ms. Found total {} samples", System.currentTimeMillis() - startTime, data.size());
        } catch (CancellationException ignored) {
            log.warn("Loading the CSV file was cancelled by the user.");
        } catch (ExecutionException | InterruptedException e) {
            log.error("Failed to load the CSV file: {}", e.getMessage());
        }
    }

    public void closeFile() {
        samples.clear();
        analyzer.setData(null);
    }

    public void exit() {
        System.exit(0);
    }
}
