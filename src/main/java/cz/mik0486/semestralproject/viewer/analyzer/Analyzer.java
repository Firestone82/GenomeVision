package cz.mik0486.semestralproject.viewer.analyzer;

import cz.mik0486.semestralproject.data.holder.Matrix;
import cz.mik0486.semestralproject.data.holder.Sample;
import cz.mik0486.semestralproject.gui.ChecklistSelector;
import cz.mik0486.semestralproject.gui.DropdownSelector;
import cz.mik0486.semestralproject.gui.ShiftSelector;
import cz.mik0486.semestralproject.viewer.Viewer;
import cz.mik0486.semestralproject.viewer.analyzer.gui.ScanViewer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

@Slf4j
@Getter
public class Analyzer {
    private final Viewer viewer;

    // Panel components
    private final ScanViewer scanViewer;
    private final ShiftSelector shifterSelector;
    private final DropdownSelector<Sample> scanSelector;
    private final ChecklistSelector<Sample> checklistSelector;

    public Analyzer(Viewer viewer) {
        this.viewer = viewer;

        scanViewer = new ScanViewer(this);

        shifterSelector = new ShiftSelector(0, 100, 50);
        shifterSelector.setOnShifted(shifted -> {
            log.info("Shifted by: {}", shifted);
        });

        scanSelector = new DropdownSelector<>();
        scanSelector.setOnSelected(selected -> {
            log.info("Selected scan: {}", selected.getName());
            scanViewer.openSample(selected);
        });

        checklistSelector = new ChecklistSelector<>();
        checklistSelector.setOnSelected(selected -> {
            log.info("Selected checks: {}", selected.stream().map(Sample::getName).collect(Collectors.joining(", ")));
        });
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
         * LEFT  PANEL
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

        JPanel dropdownPanel = scanSelector.initUI("Select sample:");
        upperPanel.add(dropdownPanel);

        JScrollPane checklistPanel = checklistSelector.initUI("Compare with:");
        rightPanel.add(checklistPanel, BorderLayout.CENTER);

        JButton analyzeButton = new JButton("Analyze");
        analyzeButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, analyzeButton.getPreferredSize().height));

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        bottomPanel.add(analyzeButton, BorderLayout.CENTER);
        rightPanel.add(bottomPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
    }

    public void setData(List<Sample> samples) {
        scanSelector.setItems(samples);
        checklistSelector.setItems(samples);
    }

    public void clearData() {
        scanSelector.clearItems();
        checklistSelector.clearItems();
    }

    public Sample compare(Sample origin, Sample target) {
        String name = origin.getName() + " vs. " + target.getName();

        Matrix originMatrix = origin.getMatrix2D();
        Matrix targetMatrix = target.getMatrix2D();

        Vector<Float> newValues = new Vector<>();

        for (int y = 0; y < originMatrix.getRows(); y++) {
            for (int x = 0; x < originMatrix.getColumns(); x++) {
                float originValue = originMatrix.getValue(y, x);
                float targetValue = targetMatrix.getValue(y, x);

                if (Math.abs(originValue - targetValue) > 0.1) {
                    newValues.add(originValue);
                } else {
                    newValues.add(0.0f);
                }
            }
        }

        Matrix newMatrix = Matrix.createBySize(newValues.size(), 0.0f);
        newMatrix.setData(newValues);

        return new Sample(name, newMatrix);
    }
}
