package cz.mik0486.semestralproject.viewer.analyzer.gui;

import cz.mik0486.semestralproject.data.DataHandler;
import cz.mik0486.semestralproject.data.holder.Pair;
import cz.mik0486.semestralproject.data.holder.Sample;
import cz.mik0486.semestralproject.gui.ZoomableGrabbablePane;
import cz.mik0486.semestralproject.viewer.analyzer.Analyzer;
import cz.mik0486.semestralproject.viewer.analyzer.dialog.ProgressiveDialog;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

@Slf4j
@Getter
public class ScanViewer extends ZoomableGrabbablePane {
    private final Analyzer analyzer;

    private static final int CELL_WIDTH = 2;
    private static final int CELL_HEIGHT = 2;

    private Sample sample;
    private BufferedImage cachedImage;

    private Pair<Integer, Integer> selectedCell;

    public ScanViewer(Analyzer analyzer) {
        super(false, false, false);
        this.analyzer = analyzer;

        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (sample == null) {
                    return;
                }

                int col = (int) ((e.getX() - getTranslateX()) / getScale() / (CELL_WIDTH + 1));
                int row = (int) ((e.getY() - getTranslateY()) / getScale() / (CELL_HEIGHT + 1));

                if (row >= 0 && row < sample.getMatrix2D().getRows() && col >= 0 && col < sample.getMatrix2D().getColumns()) {
                    if (selectedCell != null && selectedCell.first() == row && selectedCell.second() == col) {
                        return;
                    }

                    selectedCell = new Pair<>(row, col);

                    float value = sample.getMatrix2D().getValue(row, col);
                    System.out.println("Hovering over cell at row: " + row + ", col: " + col + " with value: " + value);
                } else {
                    selectedCell = null;
                }
            }
        });
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

    public void openSample(@NonNull Sample sample) {
        long startTime = System.currentTimeMillis();
        this.sample = sample;

        SwingWorker<BufferedImage, Void> worker = new SwingWorker<>() {

            @Override
            protected BufferedImage doInBackground() {
                return DataHandler.loadImage(sample, CELL_WIDTH, CELL_HEIGHT, this::setProgress);
            }
        };

        log.info("Loading image for scan: {}", sample.getName());
        worker.execute();

        new ProgressiveDialog<BufferedImage>(
            analyzer.getViewer(),
            "Caching scan: " + sample.getName(),
            "Caching scan data, please wait..."
        ).open(worker);

        try {
            cachedImage = worker.get();

            log.info("Finished caching scan in {}ms", System.currentTimeMillis() - startTime);
        } catch (CancellationException ignored) {
            log.warn("Loading of scan data was cancelled");
        } catch (ExecutionException | InterruptedException e) {
            log.error("Failed to load scan data: {}", e.getMessage());
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
