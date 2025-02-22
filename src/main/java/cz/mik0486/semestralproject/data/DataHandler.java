package cz.mik0486.semestralproject.data;

import cz.mik0486.semestralproject.data.exception.ScanLoadException;
import cz.mik0486.semestralproject.data.holder.Matrix2D;
import cz.mik0486.semestralproject.data.holder.Sample;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
public class DataHandler {

    public static Sample loadSample(String line) throws ScanLoadException {
        String[] tokens = line.split("\\s+");
        List<Double> values = new ArrayList<>();

        for (int i = 1; i < tokens.length; i++) {
            String token = tokens[i].trim();

            try {
                double value = Double.parseDouble(token);
                values.add(value);
            } catch (NumberFormatException ex) {
                throw new ScanLoadException("Invalid value in the scan data: " + token);
            }
        }

        String scanName = tokens[0];
        Matrix2D<Double> matrix2D = Matrix2D.createBySize(values.size(), 0.0);

        for (int i = 0; i < values.size(); i++) {
            int x = i % matrix2D.getColumns();
            int y = i / matrix2D.getColumns();

            matrix2D.setValue(y, x, values.get(i));
        }

        return new Sample(scanName, matrix2D);
    }

    public static List<Sample> loadFile(File file, Consumer<Integer> processListener) throws ScanLoadException {
        List<Sample> samples = new ArrayList<>();

        // Get the total file size in bytes.
        long totalBytes = file.length();
        long bytesRead = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String header = reader.readLine();
            bytesRead += calculateBytes(header);

            String line;

            while ((line = reader.readLine()) != null) {
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
        } catch (FileNotFoundException e) {
            throw new ScanLoadException("File not found: " + file.getPath());
        } catch (IOException e) {
            throw new ScanLoadException("Failed to read the file: " + e.getMessage());
        }

        return samples;
    }

    private static int calculateBytes(String line) {
        return line.getBytes(StandardCharsets.UTF_8).length + System.lineSeparator().getBytes(StandardCharsets.UTF_8).length;
    }
}
