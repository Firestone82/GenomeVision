package cz.mik0486.semestralproject.viewer.analyzer.worker;

import cz.mik0486.semestralproject.data.holder.Matrix;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

@Slf4j
@AllArgsConstructor
public class ImageMatrixLoadWorker extends SwingWorker<BufferedImage, Void> {
    private final Matrix matrix;
    private final int width;
    private final int height;

    @Override
    protected BufferedImage doInBackground() {
        BufferedImage bufferedImage;
        int imageWidth = matrix.getColumns() * (width + 1);
        int imageHeight = matrix.getRows() * (height + 1);

        try {
            bufferedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        } catch (IllegalArgumentException ex) {
            log.error("Failed to create BufferedImage with dimensions {}x{}", imageWidth, imageHeight, ex);
            return null;
        }

        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        int totalCells = matrix.getRows() * matrix.getColumns();
        int currentCellCount = 0;

        for (int i = 0; i < matrix.getRows(); i++) {
            for (int j = 0; j < matrix.getColumns(); j++) {
                float value = matrix.getValue(i, j);

                if (value == 0) {
                    g2d.setColor(new Color(1.f, 0, 0, 0.2f));
                } else {
                    g2d.setColor(new Color(0.f, 1.0f - value, 1.0f));
                }

                g2d.fillRect(j * (width + 1), i * (height + 1), width + 1, height + 1);

                // Report progress
                int progressPercent = (int) ((currentCellCount++ / (double) totalCells) * 100);
                setProgress(progressPercent);
            }
        }

        g2d.dispose();
        setProgress(100);

        return bufferedImage;
    }
}
