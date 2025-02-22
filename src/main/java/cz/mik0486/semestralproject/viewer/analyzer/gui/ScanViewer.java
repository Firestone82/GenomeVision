package cz.mik0486.semestralproject.viewer.analyzer.gui;

import cz.mik0486.semestralproject.data.holder.Matrix2D;
import cz.mik0486.semestralproject.data.holder.Sample;
import cz.mik0486.semestralproject.gui.ZoomableGrabbablePane;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.image.BufferedImage;

@Slf4j
@Getter
public class ScanViewer extends ZoomableGrabbablePane {
    private Sample sample;
    private BufferedImage cachedImage;

    private static final int CELL_WIDTH = 30;
    private static final int CELL_HEIGHT = 30;

    public ScanViewer() {
        super(false, false, false);
    }

    @Override
    public void paint(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (cachedImage != null) {
            g2.drawImage(cachedImage, 0, 0, null);
        } else {
            g2.setColor(Color.DARK_GRAY);
            g2.setFont(new Font("Arial", Font.BOLD, 18));
            g2.drawString("No sample loaded", 20, 40);
        }
    }

    private void updateCachedImage() {
        if (sample == null) {
            cachedImage = null;
            return;
        }

        log.info("Caching scan: {}", sample.getName());

        Matrix2D<Double> matrix = sample.getMatrix2D();
        int rows = matrix.getRows();
        int cols = matrix.getColumns();

        // Now safely convert to int.
        int width = 30 * cols;
        int height = 30 * rows;

        try {
            cachedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        } catch (IllegalArgumentException ex) {
            log.error("Failed to create BufferedImage with dimensions {}x{}", width, height, ex);
            cachedImage = null;
            return;
        }

        Graphics2D g2d = cachedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Use a slightly larger font for clarity.
        g2d.setFont(new Font("Arial", Font.PLAIN, 14));

        // Draw each cell.
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Double value = matrix.getValue(i, j);
                int x = j * CELL_WIDTH;
                int y = i * CELL_HEIGHT;

                if (value != null) {
                    // Scale the intensity based on the value.
                    g2d.setColor(new Color(1.0f - value.floatValue(), 1.0f - value.floatValue(), 1.0f));
                    g2d.fillRect(x, y, CELL_WIDTH, CELL_HEIGHT);
                }

                // Draw cell border.
                g2d.setColor(Color.GRAY);
                g2d.drawRect(x, y, CELL_WIDTH, CELL_HEIGHT);

                // Draw the value as text if available.
                if (value != null) {
                    String text = String.format("%.1f", value);
                    FontMetrics fm = g2d.getFontMetrics();
                    int textWidth = fm.stringWidth(text);
                    int textHeight = fm.getAscent();
                    g2d.setColor(Color.BLACK);
                    g2d.drawString(text, x + (CELL_WIDTH - textWidth) / 2, y + (CELL_HEIGHT + textHeight) / 2);
                }
            }
        }
        g2d.dispose();
    }

    public void setScan(Sample sample) {
        this.sample = sample;

        if (sample != null) {
            updateCachedImage();
            enable();
        } else {
            disable();
            cachedImage = null;
        }

        panel.repaint();
    }

    public void enable() {
        setGrabbable(true);
        setZoomable(true);
    }

    public void disable() {
        setGrabbable(false);
        setZoomable(false);
    }
}
