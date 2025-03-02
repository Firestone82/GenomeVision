package cz.mik0486.semestralproject.viewer.analyzer;

import cz.mik0486.semestralproject.data.holder.Sample;
import cz.mik0486.semestralproject.gui.BasicTable;
import cz.mik0486.semestralproject.gui.ProgressiveDialog;
import cz.mik0486.semestralproject.gui.selector.ChecklistSelector;
import cz.mik0486.semestralproject.gui.selector.DropdownSelector;
import cz.mik0486.semestralproject.gui.selector.ShiftSelector;
import cz.mik0486.semestralproject.viewer.Viewer;
import cz.mik0486.semestralproject.viewer.analyzer.gui.ScanViewer;
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
    private final BasicTable statisticsTable = new BasicTable();
    private final ScanViewer scanViewer = new ScanViewer(this);
    private final ShiftSelector shifterSelector = new ShiftSelector(0, 100, 50);
    private final DropdownSelector<Sample> scanViewSelector = new DropdownSelector<>();
    private final ChecklistSelector<Sample> scanCompareSelector = new ChecklistSelector<>();
    private final JButton analyzeButton = new JButton("Analyze");

    public Analyzer(Viewer viewer) {
        this.viewer = viewer;

        shifterSelector.setOnShifted(scanViewer::setEpsilon);
        scanViewSelector.setOnSelected(scanCompareSelector::hideItem);

        analyzeButton.addActionListener(e -> {
            if (!scanViewSelector.hasSelected()) {
                JOptionPane.showMessageDialog(
                    viewer,
                    "Please select a sample to analyze.",
                    "No sample selected",
                    JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            if (!scanCompareSelector.hasSelected()) {
                JOptionPane.showMessageDialog(
                    viewer,
                    "Please select at least one sample to compare with.",
                    "No samples selected",
                    JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            analyzeSamples();
        });

        statisticsTable.setValue("Amount above epsilon", "N/A");
        statisticsTable.setValue("Coverage (%)", "N/A");
    }

    public void initUI(JFrame frame) {
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 0));

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(leftPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);

        /*
         * LEFT PANEL
         */
        leftPanel.add(scanViewer.getPanel(), BorderLayout.CENTER);

        /*
         * RIGHT PANEL
         */
        JPanel upperPanel = new JPanel();
        upperPanel.setLayout(new BoxLayout(upperPanel, BoxLayout.Y_AXIS));
        upperPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        rightPanel.add(upperPanel, BorderLayout.NORTH);

        JPanel shifterPanel = shifterSelector.initUI("Epsilon:");
        upperPanel.add(shifterPanel);
        upperPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel dropdownPanel = scanViewSelector.initUI("Select sample:");
        upperPanel.add(dropdownPanel);

        // Create a container to hold the checklist and statistics panels
        JPanel centerContainer = new JPanel();
        centerContainer.setLayout(new BoxLayout(centerContainer, BoxLayout.Y_AXIS));

        // Checklist panel remains unchanged
        JScrollPane checklistPanel = scanCompareSelector.initUI("Compare with:");
        centerContainer.add(checklistPanel);

        // Add spacing between checklist and statistics panels
        centerContainer.add(Box.createRigidArea(new Dimension(0, 10)));

        // Create the StatisticsPanel object
        JPanel statsScrollPane = statisticsTable.initUI("Statistics");
        centerContainer.add(statsScrollPane);

        // Add the container to the center of rightPanel
        rightPanel.add(centerContainer, BorderLayout.CENTER);

        analyzeButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, analyzeButton.getPreferredSize().height));

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        bottomPanel.add(analyzeButton, BorderLayout.CENTER);
        rightPanel.add(bottomPanel, BorderLayout.SOUTH);

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
            scanViewSelector.setItems(samples);
            scanCompareSelector.setItems(samples);

            log.info("Finished loading CSV file in {}ms. Found total {} samples", System.currentTimeMillis() - startTime, samples.size());
        } catch (CancellationException ignored) {
            log.warn("Loading the CSV file was cancelled by the user.");
        } catch (ExecutionException | InterruptedException e) {
            log.error("Failed to load the CSV file: {}", e.getMessage());
        }
    }

    public void closeFile() {
        statisticsTable.clear(false);
        scanViewSelector.clearItems();
        scanCompareSelector.clearItems();
    }

    public void analyzeSamples() {
        log.info("Analyzing samples: {} with {}",
            scanViewSelector.getSelected().getName(),
            scanCompareSelector.getSelected().stream().map(Sample::getName).toList()
        );

        List<Sample> samplesToLoad = new ArrayList<>();
        samplesToLoad.add(scanViewSelector.getSelected());
        samplesToLoad.addAll(scanCompareSelector.getSelected());

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
            scanViewer.show(scanViewSelector.getSelected(), scanCompareSelector.getSelected(), shifterSelector.getValue());
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

                scanViewer.show(scanViewSelector.getSelected(), scanCompareSelector.getSelected(), shifterSelector.getValue());
            } catch (CancellationException ignored) {
                log.warn("Loading the samples was cancelled by the user.");
            } catch (ExecutionException | InterruptedException e) {
                log.error("Failed to load the samples: {}", e.getMessage());
            }
        }
    }
}
