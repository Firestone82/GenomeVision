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

    private final Color emptyColor;
    private final Color gradientStartColor;
    private final Color gradientEndColor;

    @Override
    protected BufferedImage doInBackground() {
        int imageWidth = matrix.getColumns() * (width + 1);
        int imageHeight = matrix.getRows() * (height + 1);

        BufferedImage bufferedImage;
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

        // Precompute start and end RGBA components
        float startR = gradientStartColor.getRed() / 255f;
        float startG = gradientStartColor.getGreen() / 255f;
        float startB = gradientStartColor.getBlue() / 255f;
        float startA = gradientStartColor.getAlpha() / 255f;

        float endR = gradientEndColor.getRed() / 255f;
        float endG = gradientEndColor.getGreen() / 255f;
        float endB = gradientEndColor.getBlue() / 255f;
        float endA = gradientEndColor.getAlpha() / 255f;

        for (int i = 0; i < matrix.getRows(); i++) {
            for (int j = 0; j < matrix.getColumns(); j++) {
                float value = matrix.getValue(i, j);
                Color cellColor;

                if (value == 0f) {
                    cellColor = emptyColor;
                } else {
                    // Clamp value between 0 and 1
                    float fraction = Math.max(0f, Math.min(1f, value));
                    float r = startR + fraction * (endR - startR);
                    float g = startG + fraction * (endG - startG);
                    float b = startB + fraction * (endB - startB);
                    float a = startA + fraction * (endA - startA);
                    cellColor = new Color(r, g, b, a);
                }

                g2d.setColor(cellColor);
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
