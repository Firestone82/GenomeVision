package cz.mik0486.semestralproject.viewer.analyzer.worker;

import cz.mik0486.semestralproject.data.exception.ScanLoadException;
import cz.mik0486.semestralproject.data.holder.Sample;
import cz.mik0486.semestralproject.utils.StringUtils;
import lombok.AllArgsConstructor;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@AllArgsConstructor
public class FileLoadWorker extends SwingWorker<List<Sample>, Void> {
    private final File file;

    @Override
    protected List<Sample> doInBackground() throws Exception {
        List<Sample> samples = new ArrayList<>();

        long totalBytes = file.length();
        long bytesRead = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String header = reader.readLine();
            bytesRead += StringUtils.calculateBytes(header);

            String line;
            while ((line = reader.readLine()) != null) {
                bytesRead += StringUtils.calculateBytes(line);

                Scanner scanner = new Scanner(line);
                samples.add(new Sample(scanner.next(), null));

                int progress = (int) (bytesRead * 100 / totalBytes);
                setProgress(Math.max(0, Math.min(100, progress)));
            }
        } catch (IOException e) {
            throw new ScanLoadException("Failed to read the file: " + e.getMessage());
        }

        setProgress(100);
        return samples;
    }
}
