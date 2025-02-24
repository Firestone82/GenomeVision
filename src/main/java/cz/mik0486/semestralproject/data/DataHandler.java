package cz.mik0486.semestralproject.data;

import cz.mik0486.semestralproject.data.exception.ScanLoadException;
import cz.mik0486.semestralproject.data.holder.Matrix;
import cz.mik0486.semestralproject.data.holder.Sample;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
public class DataHandler {

    public static Sample loadSample(String line) throws ScanLoadException {
        String[] tokens = line.split("\\s+");
        List<Float> values = new ArrayList<>();

        for (int i = 1; i < tokens.length; i++) {
            String token = tokens[i].trim();

            try {
                float value = Float.parseFloat(token);
                values.add(value);
            } catch (NumberFormatException ex) {
                throw new ScanLoadException("Invalid value in the scan data: " + token);
            }
        }

        String scanName = tokens[0];
        Matrix matrix = Matrix.createBySize(values.size(), 0.0f);

        for (int i = 0; i < values.size(); i++) {
            int x = i % matrix.getColumns();
            int y = i / matrix.getColumns();

            matrix.setValue(y, x, values.get(i));
        }

        return new Sample(scanName, matrix);
    }

    public static List<Sample> loadFile(File file, Consumer<Integer> processListener) throws ScanLoadException {
        List<Sample> samples = new ArrayList<>();

        // Get the total file size in bytes.
        long totalBytes = file.length();
        long bytesRead = 0;

        try (LineIterator it = FileUtils.lineIterator(file, "UTF-8")) {
            String header = it.next();
            bytesRead += calculateBytes(header);

            while (it.hasNext()) {
                String line = it.next();

                if (Thread.currentThread().isInterrupted()) {
                    return null;
                }

                Sample sample = loadSample(line);
                samples.add(sample);

                log.trace("Loaded sample: {}", sample.getName());
                bytesRead += calculateBytes(line);

                // Report progress
                int progress = (int) (bytesRead * 100 / totalBytes);
                processListener.accept(Math.max(0, Math.min(100, progress)));
            }
        } catch (IOException e) {
            throw new ScanLoadException("Failed to read the file: " + e.getMessage());
        }

        processListener.accept(100);
        return samples;
    }

    private static int calculateBytes(String line) {
        return line.getBytes(StandardCharsets.UTF_8).length + System.lineSeparator().getBytes(StandardCharsets.UTF_8).length;
    }

    public static BufferedImage loadImage(Sample sample, int width, int height, Consumer<Integer> processListener) {
        if (sample == null) {
            return null;
        }

        BufferedImage bufferedImage;
        Matrix matrix = sample.getMatrix2D();
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
                processListener.accept(progressPercent);
            }
        }

        try {
            File outputDir = new File("output");
            if (!outputDir.exists()) {
                outputDir.mkdir();
            }

            ImageIO.write(bufferedImage, "png", new File("output/" + sample.getName() + ".png"));
        } catch (IOException e) {
            log.error("Failed to save the image to file: {}", e.getMessage());
        }

        processListener.accept(100);
        g2d.dispose();

        return bufferedImage;
    }

}
