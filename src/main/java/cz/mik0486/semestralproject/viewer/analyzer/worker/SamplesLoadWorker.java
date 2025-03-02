package cz.mik0486.semestralproject.viewer.analyzer.worker;

import cz.mik0486.semestralproject.data.exception.ScanLoadException;
import cz.mik0486.semestralproject.data.holder.Matrix;
import cz.mik0486.semestralproject.data.holder.Sample;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

@Slf4j
@AllArgsConstructor
public class SamplesLoadWorker extends SwingWorker<List<Sample>, Void> {
    private final File file;
    private final List<Sample> samplesToLoad;

    @Override
    protected List<Sample> doInBackground() throws Exception {
        int percentPerSample = 100 / samplesToLoad.size();
        int currentPercent = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            // First line is the header
            String line = reader.readLine();

            if (line == null) {
                throw new ScanLoadException("Provided file is empty");
            }

            while ((line = reader.readLine()) != null) {
                Scanner scanner = new Scanner(line);
                String scanName = scanner.next();

                Sample sample = samplesToLoad.stream()
                    .filter(s -> s.getName().equals(scanName))
                    .findFirst()
                    .orElse(null);

                if (sample != null) {
                    Vector<Float> values = new Vector<>();

                    while (scanner.hasNext()) {
                        values.add(Float.parseFloat(scanner.next()));
                    }

                    Matrix matrix = Matrix.createWith(values, 0.0f);
                    sample.setMatrix(matrix);

                    currentPercent += percentPerSample;
                    setProgress(Math.max(0, Math.min(100, currentPercent)));

                    log.trace("Successfully loaded sample: {}", sample.getName());
                }

                if (currentPercent >= 100) {
                    break;
                }
            }
        } catch (IOException e) {
            throw new ScanLoadException("Failed to read the file: " + e.getMessage());
        }

        setProgress(100);
        return samplesToLoad;
    }
}
