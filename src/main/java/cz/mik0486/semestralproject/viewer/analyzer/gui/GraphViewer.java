package cz.mik0486.semestralproject.viewer.analyzer.gui;

import cz.mik0486.semestralproject.data.holder.Matrix;
import cz.mik0486.semestralproject.data.holder.Sample;
import cz.mik0486.semestralproject.gui.panel.graph.GraphPanel;
import cz.mik0486.semestralproject.gui.panel.graph.HighlightRegion;
import cz.mik0486.semestralproject.gui.selector.range.Range;
import cz.mik0486.semestralproject.viewer.analyzer.Analyzer;
import cz.mik0486.semestralproject.viewer.analyzer.method.FilterMethod;
import lombok.Getter;

import javax.swing.*;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.List;

@Getter
public class GraphViewer {
    private final Analyzer analyzer;

    private final JPanel panel = new JPanel();
    private final GraphPanel originGraphViewer;
    private final GraphPanel targetGraphViewer;

    public GraphViewer(Analyzer analyzer) {
        this.analyzer = analyzer;

        this.originGraphViewer = new GraphPanel();
        this.targetGraphViewer = new GraphPanel();

        initUI();
    }

    public void initUI() {
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(originGraphViewer.getPanel());
        panel.add(Box.createVerticalStrut(5));
        panel.add(targetGraphViewer.getPanel());
    }

    private List<Point2D> calculateGraphPoints(List<Sample> samples) {
        HashMap<Float, Integer> xMap = new HashMap<>();

        FilterMethod filterMethod = analyzer.getFilterMethodSelector().getSelected();
        Matrix filteredMatrix = filterMethod.process(samples);

        for (float val : filteredMatrix.getData()) {
            float roundedValue = Math.round(val * 100.0f) / 100.0f;
            xMap.put(roundedValue, xMap.getOrDefault(roundedValue, 0) + 1);
        }

        return xMap.keySet().stream()
            .sorted()
            .map(x -> (Point2D) new Point2D.Float(x, xMap.get(x)))
            .toList();
    }

    public void update() {
        Range originBoundaryRange = analyzer.getCompareOriginRangeSelector().getValue();
        originGraphViewer.setVerticalLine("thresholdMin", originBoundaryRange.lower() / 100.0f, HighlightRegion.BEFORE);
        originGraphViewer.setVerticalLine("thresholdMax", originBoundaryRange.upper() / 100.0f, HighlightRegion.AFTER);

        Range targetBoundaryRange = analyzer.getCompareTargetRangeSelector().getValue();
        targetGraphViewer.setVerticalLine("thresholdMin", targetBoundaryRange.lower() / 100.0f, HighlightRegion.BEFORE);
        targetGraphViewer.setVerticalLine("thresholdMax", targetBoundaryRange.upper() / 100.0f, HighlightRegion.AFTER);
    }

    public void show() {
        List<Sample> originSamples = analyzer.getCompareOriginScanSelector().getSelected();
        originGraphViewer.setPoints(calculateGraphPoints(originSamples));

        List<Sample> targetSamples = analyzer.getCompareTargetScanSelector().getSelected();
        targetGraphViewer.setPoints(calculateGraphPoints(targetSamples));
    }
}
