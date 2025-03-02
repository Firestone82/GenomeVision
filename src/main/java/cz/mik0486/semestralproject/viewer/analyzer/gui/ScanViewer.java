package cz.mik0486.semestralproject.viewer.analyzer.gui;

import cz.mik0486.semestralproject.data.holder.Matrix;
import cz.mik0486.semestralproject.data.holder.Sample;
import cz.mik0486.semestralproject.gui.ProgressiveDialog;
import cz.mik0486.semestralproject.gui.ZoomableGrabbablePane;
import cz.mik0486.semestralproject.utils.MathUtils;
import cz.mik0486.semestralproject.viewer.analyzer.Analyzer;
import cz.mik0486.semestralproject.viewer.analyzer.worker.ImageMatrixLoadWorker;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

@Slf4j
@Getter
public class ScanViewer extends ZoomableGrabbablePane {
    private final Analyzer analyzer;

    private BufferedImage cachedImage;
    private static final int CELL_WIDTH = 2;
    private static final int CELL_HEIGHT = 2;

    private Sample originSample;
    private List<Sample> compareSamples;
    private int epsilon;

    public ScanViewer(Analyzer analyzer) {
        super(false, false, false);
        this.analyzer = analyzer;
    }

    @Override
    public void paint(Graphics2D g2) {
        if (cachedImage != null) {
            g2.drawImage(cachedImage, 0, 0, null);
        } else {
            g2.setColor(Color.DARK_GRAY);
            g2.setFont(new Font("Arial", Font.BOLD, 18));
            g2.drawString("No sample loaded", 20, 40);
        }
    }

    public void show(Sample originSample, List<Sample> compareSamples, int epsilon) {
        this.originSample = originSample;
        this.compareSamples = compareSamples;
        this.epsilon = epsilon;
        generate();
    }

    public void setEpsilon(int epsilon) {
        if (this.epsilon == epsilon) {
            return;
        }

        this.epsilon = epsilon;
        generate();
    }

    public void generate() {
        long startTime = System.currentTimeMillis();

        Matrix matrix = Matrix.average(compareSamples.stream()
            .map(Sample::getMatrix)
            .toList()
        );

        matrix.apply((x, y, value) -> {
            assert originSample.getMatrix() != null;

            float originValue = originSample.getMatrix().getValue(x, y);
            float val = Math.abs(originValue - value);
            float eps = epsilon / 100.f;

            if (val < eps) {
                return 0.0f;
            }

            return val;
        });

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
