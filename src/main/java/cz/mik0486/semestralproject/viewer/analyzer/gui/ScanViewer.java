package cz.mik0486.semestralproject.viewer.analyzer.gui;

import cz.mik0486.semestralproject.data.holder.Matrix;
import cz.mik0486.semestralproject.gui.ProgressiveDialog;
import cz.mik0486.semestralproject.gui.panel.ZoomableGrabbablePanel;
import cz.mik0486.semestralproject.utils.MathUtils;
import cz.mik0486.semestralproject.viewer.analyzer.Analyzer;
import cz.mik0486.semestralproject.viewer.analyzer.worker.ImageMatrixLoadWorker;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.image.BufferedImage;

@Slf4j
@Getter
public class ScanViewer extends ZoomableGrabbablePanel {
    private final Analyzer analyzer;

    private BufferedImage cachedImage;
    private static final int CELL_WIDTH = 2;
    private static final int CELL_HEIGHT = 2;

    public ScanViewer(Analyzer analyzer) {
        super(false, false, false);
        this.analyzer = analyzer;
    }

    @Override
    public void paint(Graphics2D g2) {
        if (cachedImage != null) {
            g2.drawImage(cachedImage, 0, 0, null);
        } else {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(Color.DARK_GRAY);
            g2.setFont(new Font("Arial", Font.BOLD, 18));
            g2.drawString("No sample loaded", 20, 40);
        }
    }

    public void update() {
        if (cachedImage != null) {
            show();
        }
    }

    public void show() {
        long startTime = System.currentTimeMillis();

        Matrix matrix = analyzer.getFilterMethodSelector().getSelected().calculate(
            analyzer.getCompareOriginScanSelector().getSelected(),
            analyzer.getCompareTargetScanSelector().getSelected(),
            analyzer.getCompareOriginRangeSelector().getValue(),
            analyzer.getCompareTargetRangeSelector().getValue(),
            analyzer.getEpsilonShifterSelector().getValue()
        );

        int amountAboveEps = matrix.getData().stream().mapToInt(value -> value > 0.0f ? 1 : 0).sum();
        analyzer.getStatisticsTable().setValue("Amount above epsilon", amountAboveEps);

        float percentage = MathUtils.round(100 - (amountAboveEps / (float) matrix.size() * 100), 3);
        analyzer.getStatisticsTable().setValue("Coverage (%)", percentage);

        ImageMatrixLoadWorker worker = new ImageMatrixLoadWorker(matrix, CELL_WIDTH, CELL_HEIGHT);
        worker.execute();

        new ProgressiveDialog<BufferedImage>(
            analyzer.getViewer(),
            "Generating scan",
            "Generating scan data, please wait..."
        ).open(worker);

        try {
            cachedImage = worker.get();
            log.info("Finished generating scan in {}ms", System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            log.error("Failed to generate scan: {}", e.getMessage());
        }

        setGrabbable(true);
        setZoomable(true);

        panel.repaint();
    }

    public void closeSample() {
        cachedImage = null;
        setGrabbable(false);
        setZoomable(false);

        panel.repaint();
    }
}
