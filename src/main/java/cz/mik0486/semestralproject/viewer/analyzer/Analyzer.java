package cz.mik0486.semestralproject.viewer.analyzer;

import cz.mik0486.semestralproject.data.holder.Sample;
import cz.mik0486.semestralproject.gui.ProgressiveDialog;
import cz.mik0486.semestralproject.gui.Table;
import cz.mik0486.semestralproject.gui.panel.GridPanel;
import cz.mik0486.semestralproject.gui.selector.ChecklistSelector;
import cz.mik0486.semestralproject.gui.selector.DropdownSelector;
import cz.mik0486.semestralproject.gui.selector.ShiftRangeSelector;
import cz.mik0486.semestralproject.viewer.Viewer;
import cz.mik0486.semestralproject.viewer.analyzer.gui.ScanViewer;
import cz.mik0486.semestralproject.viewer.analyzer.method.AverageFilterMethod;
import cz.mik0486.semestralproject.viewer.analyzer.method.CloseAverageFilterMethod;
import cz.mik0486.semestralproject.viewer.analyzer.method.FilterMethod;
import cz.mik0486.semestralproject.viewer.analyzer.worker.FileLoadWorker;
import cz.mik0486.semestralproject.viewer.analyzer.worker.SamplesLoadWorker;
import lombok.Getter;
import lombok.NonNull;
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
public class Analyzer {
    private final Viewer viewer;

    private final List<Sample> samples = new ArrayList<>();
    private File file;

    // Panel components
    private final Table statisticsTable = new Table();
    private final ShiftRangeSelector shifterSelector = new ShiftRangeSelector(0, 100, 25, 75);
    private final ScanViewer scanViewer = new ScanViewer(this, shifterSelector.getValue());
    private final DropdownSelector<FilterMethod> filterMethodSelector = new DropdownSelector<>();
    private final ChecklistSelector<Sample> scanOriginSelector = new ChecklistSelector<>();
    private final ChecklistSelector<Sample> scanTargetSelector = new ChecklistSelector<>();
    private final JButton analyzeButton = new JButton("Analyze");

    public Analyzer(Viewer viewer) {
        this.viewer = viewer;

        shifterSelector.setOnShifted(scanViewer::setEpsilon);

        scanOriginSelector.setOnSelected(samples -> {
            scanViewer.setOriginSamples(samples);
            scanTargetSelector.hideItems(samples);
        });

        scanTargetSelector.setOnSelected(samples -> {
            scanViewer.setTargetSamples(samples);
            scanOriginSelector.hideItems(samples);
        });

        analyzeButton.addActionListener(e -> {
            if (file == null) {
                JOptionPane.showMessageDialog(viewer,
                    "Please load a CSV file to analyze.",
                    "No file loaded",
                    JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            if (!filterMethodSelector.hasSelected()) {
                JOptionPane.showMessageDialog(viewer,
                    "Please select a filter method to use.",
                    "No filter method selected",
                    JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            if (!scanOriginSelector.hasSelected() || !scanTargetSelector.hasSelected()) {
                JOptionPane.showMessageDialog(viewer,
                    "Please select at least one sample to analyze.",
                    "No sample selected",
                    JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            analyzeSamples();
        });

        statisticsTable.setValue("Amount above epsilon", "N/A");
        statisticsTable.setValue("Coverage (%)", "N/A");

        filterMethodSelector.add(new AverageFilterMethod());
        filterMethodSelector.add(new CloseAverageFilterMethod());
    }

    public void initUI(JFrame frame) {
        JPanel mainPanel = new JPanel(new BorderLayout());

        /*
         * LEFT PANEL
         */

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 5));
        mainPanel.add(leftPanel, BorderLayout.CENTER);

        // Scan viewer
        leftPanel.add(scanViewer.getPanel(), BorderLayout.CENTER);

        /*
         * RIGHT PANEL
         */

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 10));
        mainPanel.add(rightPanel, BorderLayout.EAST);

        // Top
        JPanel upperPanel = new JPanel();
        upperPanel.setLayout(new BoxLayout(upperPanel, BoxLayout.Y_AXIS));
        upperPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        rightPanel.add(upperPanel, BorderLayout.NORTH);

        GridPanel gridPanel = new GridPanel(5, 5, BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(" Settings: "),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        gridPanel.add(new JLabel("Method:"), filterMethodSelector.getComponent());
        gridPanel.add(new JLabel("Epsilon:"), shifterSelector.getComponent(true));
        upperPanel.add(gridPanel.getPanel());

        upperPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        upperPanel.add(statisticsTable.initUI("Statistics"));

        // Mid
        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));
        rightPanel.add(middlePanel, BorderLayout.CENTER);

        middlePanel.add(scanOriginSelector.initUI("Select sample:"));
        middlePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        middlePanel.add(scanTargetSelector.initUI("Compare with:"));

        // Bottom
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        rightPanel.add(bottomPanel, BorderLayout.SOUTH);

        analyzeButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, analyzeButton.getPreferredSize().height));
        bottomPanel.add(analyzeButton, BorderLayout.CENTER);

        frame.add(mainPanel);
    }

    public void loadFile(@NonNull File file) {
        long startTime = System.currentTimeMillis();
        this.file = file;

        log.info("Loading CSV file '{}'", file.getPath());
        samples.clear();

        FileLoadWorker worker = new FileLoadWorker(file);
        worker.execute();

        new ProgressiveDialog<List<Sample>>(
            viewer,
            "Load file: " + file.getName(),
            "Loading file, please wait..."
        ).open(worker);

        try {
            List<Sample> samples = worker.get();
            this.samples.addAll(samples);

            statisticsTable.clear(false);
            scanOriginSelector.setItems(samples);
            scanTargetSelector.setItems(samples);

            log.info("Finished loading CSV file in {}ms. Found total {} samples", System.currentTimeMillis() - startTime, samples.size());
        } catch (CancellationException ignored) {
            log.warn("Loading the CSV file was cancelled by the user.");
        } catch (ExecutionException | InterruptedException e) {
            log.error("Failed to load the CSV file: {}", e.getMessage());
        }
    }

    public void closeFile() {
        statisticsTable.clear(false);
        scanViewer.closeSample();
        scanOriginSelector.clearItems();
        scanTargetSelector.clearItems();
    }

    public void analyzeSamples() {
        log.info("Analyzing samples: {} with {}",
            scanOriginSelector.getSelected().stream().map(Sample::getName).toList(),
            scanTargetSelector.getSelected().stream().map(Sample::getName).toList()
        );

        List<Sample> samplesToLoad = new ArrayList<>();
        samplesToLoad.addAll(scanOriginSelector.getSelected());
        samplesToLoad.addAll(scanTargetSelector.getSelected());

        // Unload the samples that are not needed
        samples.stream()
            .filter(sample -> !samplesToLoad.contains(sample) && sample.isLoaded())
            .forEach(sample -> {
                log.debug("- Unloading sample: {}. Not required in cache.", sample.getName());
                sample.setMatrix(null);
            });

        // Ignore already loaded samples
        samplesToLoad.removeIf(sample -> {
            if (sample.isLoaded()) {
                log.debug("- Skipping loading for: {}. Sample is already loaded.", sample.getName());
                return true;
            }

            return false;
        });

        if (samplesToLoad.isEmpty()) {
            log.info("All samples are already loaded, skipping the loading process.");
            scanViewer.generate();
        } else {
            long startTime = System.currentTimeMillis();
            log.info(" - Found samples to load: {}", samplesToLoad.stream().map(Sample::getName).toList());

            SamplesLoadWorker worker = new SamplesLoadWorker(file, samplesToLoad);
            worker.execute();

            new ProgressiveDialog<List<Sample>>(
                viewer,
                "Loading samples: " + file.getName(),
                "Loading samples, please wait..."
            ).open(worker);

            try {
                List<Sample> loadedSamples = worker.get();
                log.info("Finished loading samples in {}ms", System.currentTimeMillis() - startTime);
                scanViewer.generate();
            } catch (CancellationException ignored) {
                log.warn("Loading the samples was cancelled by the user.");
            } catch (ExecutionException | InterruptedException e) {
                log.error("Failed to load the samples: {}", e.getMessage());
            }
        }
    }
}
