package cz.mik0486.semestralproject.viewer.analyzer;

import cz.mik0486.semestralproject.data.holder.Sample;
import cz.mik0486.semestralproject.gui.LabelSeparator;
import cz.mik0486.semestralproject.gui.ProgressiveDialog;
import cz.mik0486.semestralproject.gui.Table;
import cz.mik0486.semestralproject.gui.panel.GridPanel;
import cz.mik0486.semestralproject.gui.selector.ChecklistSelector;
import cz.mik0486.semestralproject.gui.selector.DropdownSelector;
import cz.mik0486.semestralproject.gui.selector.ShiftRangeSelector;
import cz.mik0486.semestralproject.gui.selector.ShiftSelector;
import cz.mik0486.semestralproject.viewer.Viewer;
import cz.mik0486.semestralproject.viewer.analyzer.gui.GraphViewer;
import cz.mik0486.semestralproject.viewer.analyzer.gui.ScanViewer;
import cz.mik0486.semestralproject.viewer.analyzer.method.AverageFilterMethod;
import cz.mik0486.semestralproject.viewer.analyzer.method.CloseAverageFilterMethod;
import cz.mik0486.semestralproject.viewer.analyzer.method.FilterMethod;
import cz.mik0486.semestralproject.viewer.analyzer.worker.FileLoadWorker;
import cz.mik0486.semestralproject.viewer.analyzer.worker.SamplesLoadWorker;
import cz.mik0486.semestralproject.viewer.settings.Settings;
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
    private static final int DEFAULT_EPSILON = 50;

    private final Viewer viewer;
    private final Settings settings;

    private final List<Sample> samples = new ArrayList<>();
    private File file;

    // Viewers
    private final CardLayout cardLayout = new CardLayout();
    private JPanel viewerPanel;
    private final ScanViewer scanViewer;
    private final GraphViewer graphViewer;

    // Sidebar components
    private final DropdownSelector<FilterMethod> filterMethodSelector;
    private final ShiftSelector epsilonShifterSelector;
    private final Table statisticsTable;
    private final ChecklistSelector<Sample> compareOriginScanSelector;
    private final ShiftRangeSelector compareOriginRangeSelector;
    private final ChecklistSelector<Sample> compareTargetScanSelector;
    private final ShiftRangeSelector compareTargetRangeSelector;
    private final JButton analyzeButton;
    private final JButton graphButton;

    public Analyzer(Viewer viewer) {
        this.viewer = viewer;

        this.scanViewer = new ScanViewer(this);
        this.graphViewer = new GraphViewer(this);

        this.epsilonShifterSelector = new ShiftSelector(0, 100, DEFAULT_EPSILON);
        this.epsilonShifterSelector.setOnShifted((epsilon) -> {
            if (viewerPanel.getComponent(0).isShowing()) {
                scanViewer.update();
            }
        });

        this.compareOriginScanSelector = new ChecklistSelector<>();
        this.compareTargetScanSelector = new ChecklistSelector<>();

        this.filterMethodSelector = new DropdownSelector<>();
        this.filterMethodSelector.add(new AverageFilterMethod());
        this.filterMethodSelector.add(new CloseAverageFilterMethod());

        this.compareOriginRangeSelector = new ShiftRangeSelector(0, 100, 0, 100);
        this.compareOriginRangeSelector.setOnShifted((range) -> {
            if (viewerPanel.getComponent(0).isShowing()) {
                scanViewer.update();
            } else {
                graphViewer.update();
            }
        });

        this.compareTargetRangeSelector = new ShiftRangeSelector(0, 100, 0, 100);
        this.compareTargetRangeSelector.setOnShifted((range) -> {
            if (viewerPanel.getComponent(0).isShowing()) {
                scanViewer.update();
            } else {
                graphViewer.update();
            }
        });

        this.statisticsTable = new Table();
        this.statisticsTable.setValue("Amount above epsilon", "N/A");
        this.statisticsTable.setValue("Coverage (%)", "N/A");

        this.analyzeButton = new JButton("Analyze");
        this.analyzeButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, analyzeButton.getPreferredSize().height));
        this.analyzeButton.addActionListener(e -> {
            if (checkSamplesLoaded()) {
                log.info("Analyzing samples: {} with {}",
                    compareOriginScanSelector.getSelected().stream().map(Sample::getName).toList(),
                    compareTargetScanSelector.getSelected().stream().map(Sample::getName).toList()
                );

                loadSamples(() -> {
                    cardLayout.show(viewerPanel, "scan");
                    scanViewer.show();
                });
            }
        });

        this.graphButton = new JButton("Show graph");
        this.graphButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, graphButton.getPreferredSize().height));
        this.graphButton.addActionListener(e -> {
            if (checkSamplesLoaded()) {
                log.info("Generating graphs for samples: {} with {}",
                    compareOriginScanSelector.getSelected().stream().map(Sample::getName).toList(),
                    compareTargetScanSelector.getSelected().stream().map(Sample::getName).toList()
                );

                loadSamples(() -> {
                    cardLayout.show(viewerPanel, "graph");
                    graphViewer.show();
                });
            }
        });

        this.settings = new Settings(this);
        this.settings.setOnChange(this.scanViewer::update);
    }

    public void initUI(JFrame frame) {
        JPanel mainPanel = new JPanel(new BorderLayout());

        /*
         * VIEWER PANEL
         */

        viewerPanel = new JPanel(cardLayout);
        viewerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 5));
        mainPanel.add(viewerPanel, BorderLayout.CENTER);

        viewerPanel.add(scanViewer.getPanel(), "scan");
        viewerPanel.add(graphViewer.getPanel(), "graph");
        cardLayout.show(viewerPanel, "scan");

        /*
         * SIDEBAR PANEL
         */

        JPanel sidebarPanel = new JPanel(new BorderLayout());
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 10));
        mainPanel.add(sidebarPanel, BorderLayout.EAST);

        // Top
        // ===========================

        JPanel sidebarUpperPanel = new JPanel();
        sidebarUpperPanel.setLayout(new BoxLayout(sidebarUpperPanel, BoxLayout.Y_AXIS));
        sidebarPanel.add(sidebarUpperPanel, BorderLayout.NORTH);

        // SettingsData
        GridPanel settingsPanel = new GridPanel(5, 5, BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(" SettingsData: "),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        settingsPanel.add(new JLabel("Method:"), filterMethodSelector.getComponent());
        settingsPanel.add(new JLabel("Epsilon:"), epsilonShifterSelector.getComponent());
        sidebarUpperPanel.add(settingsPanel.getPanel());

        // Statistics
        JPanel statisticsPanel = new JPanel(new BorderLayout());
        statisticsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(" Statistics: "),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        statisticsPanel.add(statisticsTable.getPanel(), BorderLayout.CENTER);
        sidebarUpperPanel.add(statisticsPanel);

        // Mid
        // ===========================

        JPanel sidebarMiddlePanel = new JPanel();
        sidebarMiddlePanel.setLayout(new BoxLayout(sidebarMiddlePanel, BoxLayout.Y_AXIS));
        sidebarPanel.add(sidebarMiddlePanel, BorderLayout.CENTER);

        // Select origin samples
        JPanel selectOriginSamplesPanel = new JPanel();
        selectOriginSamplesPanel.setLayout(new BoxLayout(selectOriginSamplesPanel, BoxLayout.Y_AXIS));
        selectOriginSamplesPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(" Select sample: "),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        sidebarMiddlePanel.add(selectOriginSamplesPanel);
        selectOriginSamplesPanel.add(compareOriginScanSelector.getComponent(true));

        JPanel selectOriginSampleBoundaryPanel = new JPanel(new BorderLayout());
        selectOriginSampleBoundaryPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, selectOriginSampleBoundaryPanel.getPreferredSize().height));
        selectOriginSampleBoundaryPanel.add(new LabelSeparator(" Boundary limiter: ").getComponent(), BorderLayout.NORTH);
        selectOriginSampleBoundaryPanel.add(compareOriginRangeSelector.getComponent(true), BorderLayout.CENTER);
        selectOriginSamplesPanel.add(selectOriginSampleBoundaryPanel);

        // Select target samples
        JPanel selectTargetSamplesPanel = new JPanel();
        selectTargetSamplesPanel.setLayout(new BoxLayout(selectTargetSamplesPanel, BoxLayout.Y_AXIS));
        selectTargetSamplesPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(" Compare with: "),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        sidebarMiddlePanel.add(selectTargetSamplesPanel);
        selectTargetSamplesPanel.add(compareTargetScanSelector.getComponent(true));

        JPanel selectTargetSampleBoundaryPanel = new JPanel(new BorderLayout());
        selectTargetSampleBoundaryPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, selectTargetSampleBoundaryPanel.getPreferredSize().height));
        selectTargetSampleBoundaryPanel.add(new LabelSeparator(" Boundary limiter: ").getComponent(), BorderLayout.NORTH);
        selectTargetSampleBoundaryPanel.add(compareTargetRangeSelector.getComponent(true), BorderLayout.CENTER);
        selectTargetSamplesPanel.add(selectTargetSampleBoundaryPanel);

        // Bottom
        // ===========================

        JPanel sidebarBottomPanel = new JPanel();
        sidebarBottomPanel.setLayout(new BoxLayout(sidebarBottomPanel, BoxLayout.X_AXIS));
        sidebarBottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        sidebarPanel.add(sidebarBottomPanel, BorderLayout.SOUTH);

        sidebarBottomPanel.add(analyzeButton, BorderLayout.CENTER);
        sidebarBottomPanel.add(Box.createHorizontalStrut(10));
        sidebarBottomPanel.add(graphButton, BorderLayout.CENTER);

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
            compareOriginScanSelector.setItems(samples);
            compareTargetScanSelector.setItems(samples);

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
        compareOriginScanSelector.clearItems();
        compareTargetScanSelector.clearItems();
    }

    private boolean checkSamplesLoaded() {
        if (file == null) {
            JOptionPane.showMessageDialog(viewer,
                "Please load a CSV file to analyze.",
                "No file loaded",
                JOptionPane.WARNING_MESSAGE
            );

            return false;
        }

        if (!compareOriginScanSelector.hasSelected() || !compareTargetScanSelector.hasSelected()) {
            JOptionPane.showMessageDialog(viewer,
                "Please select at least one sample to analyze.",
                "No sample selected",
                JOptionPane.WARNING_MESSAGE
            );

            return false;
        }

        if (!filterMethodSelector.hasSelected()) {
            JOptionPane.showMessageDialog(viewer,
                "Please select a filter method to use.",
                "No filter method selected",
                JOptionPane.WARNING_MESSAGE
            );

            return false;
        }

        return true;
    }

    private void loadSamples(Runnable callback) {
        List<Sample> samplesToLoad = new ArrayList<>();
        samplesToLoad.addAll(compareOriginScanSelector.getSelected());
        samplesToLoad.addAll(compareTargetScanSelector.getSelected());

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
            callback.run();
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
                callback.run();
            } catch (CancellationException ignored) {
                log.warn("Loading the samples was cancelled by the user.");
            } catch (ExecutionException | InterruptedException e) {
                log.error("Failed to load the samples: {}", e.getMessage());
            }
        }
    }
}
