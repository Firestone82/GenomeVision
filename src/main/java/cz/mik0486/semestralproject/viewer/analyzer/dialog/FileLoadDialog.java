package cz.mik0486.semestralproject.viewer.analyzer.dialog;

import cz.mik0486.semestralproject.data.holder.Sample;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class FileLoadDialog extends JDialog {
    private final JProgressBar progressBar;
    private final SwingWorker<List<Sample>, Void> worker;

    public FileLoadDialog(JFrame parent, SwingWorker<List<Sample>, Void> worker) {
        super(parent, "Load file", true);
        setSize(300, 150);
        setLocationRelativeTo(parent);

        // Cancel the worker when the dialog is closed.
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                worker.cancel(true);
                dispose();
            }
        });

        this.progressBar = new JProgressBar(0, 100);
        this.progressBar.setStringPainted(true);

        // Listen to the worker's progress.
        this.worker = worker;
        this.worker.addPropertyChangeListener(evt -> {
            if ("progress".equals(evt.getPropertyName())) {
                int progress = (Integer) evt.getNewValue();
                progressBar.setValue(progress);

                if (progress == 100) {
                    dispose();
                }
            }
        });

        initUI();
    }

    void initUI() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(panel);

        JLabel label = new JLabel("Loading file, please wait...", JLabel.CENTER);
        panel.add(label, BorderLayout.NORTH);
        panel.add(progressBar, BorderLayout.CENTER);

        // Create a Cancel button.
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e2 -> {
            worker.cancel(true);
            dispose();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
    }
}
