package cz.mik0486.semestralproject.viewer.analyzer;

import cz.mik0486.semestralproject.data.holder.Matrix;
import cz.mik0486.semestralproject.data.holder.Sample;
import cz.mik0486.semestralproject.gui.DropdownSelector;
import cz.mik0486.semestralproject.viewer.Viewer;
import cz.mik0486.semestralproject.viewer.analyzer.gui.ScanViewer;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Vector;

@Slf4j
@Getter
public class Analyzer {
    private final JPanel panel = new JPanel(new BorderLayout());
    private final Viewer viewer;

    // Panel components
    private final ScanViewer scanViewer;
    private final DropdownSelector<Sample> scanSelector;

    private final ScanViewer compareViewer;
    private final DropdownSelector<Sample> compareSelector;

    public Analyzer(Viewer viewer) {
        this.viewer = viewer;

        scanViewer = new ScanViewer(this);
        scanSelector = new DropdownSelector<>("View sample:") {
            @Override
            public void onSelected(@NonNull Sample selected) {
                log.info("Selected scan: {}", selected.getName());
                scanViewer.openSample(selected);
            }
        };

        compareViewer = new ScanViewer(this);
        compareSelector = new DropdownSelector<>("Compare with:") {
            @Override
            public void onSelected(@NonNull Sample selected) {
                log.info("Selected compare: {}", selected.getName());
                compareViewer.openSample(compare(scanViewer.getSample(), selected));
            }
        };

        initUI();
    }

    private void initUI() {
        JPanel controlPanel = new JPanel(new GridLayout());
        controlPanel.add(scanSelector.getPanel());
        controlPanel.add(compareSelector.getPanel());

        JPanel paneContainer = new JPanel(new GridLayout(1, 2, 10, 0));
        paneContainer.add(scanViewer.getPanel());
        paneContainer.add(compareViewer.getPanel());

        panel.add(controlPanel, BorderLayout.NORTH);
        panel.add(paneContainer, BorderLayout.CENTER);
    }

    public void setData(@Nullable List<Sample> samples) {
        if (samples == null) {
            scanSelector.clearItems();
            scanViewer.closeSample();

            compareSelector.clearItems();
            compareViewer.closeSample();
        } else {
            scanSelector.setItems(samples);
            compareSelector.setItems(samples);
        }
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
//                }
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
