package cz.mik0486.semestralproject.viewer.analyzer.dialog;

import javax.swing.*;
import java.awt.*;

public class ProgressiveDialog<T> extends JDialog {
    private final JProgressBar progressBar;
    private SwingWorker<T, Void> worker;

    public ProgressiveDialog(JFrame parent, String title, String message) {
        super(parent, title, true);
        setSize(300, 150);
        setLocationRelativeTo(parent);

        this.progressBar = new JProgressBar(0, 100);
        this.progressBar.setStringPainted(true);

        initUI(message);
    }

    public void open(SwingWorker<T, Void> worker) {
        this.worker = worker;

        this.worker.addPropertyChangeListener(evt -> {
            if ("progress".equals(evt.getPropertyName())) {
                int progress = (Integer) evt.getNewValue();
                progressBar.setValue(progress);

                if (progress >= 100) {
                    dispose();
                }
            }
        });

        setVisible(true);
    }

    private void initUI(String message) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(panel);

        JLabel label = new JLabel(message, JLabel.CENTER);
        panel.add(label, BorderLayout.NORTH);
        panel.add(progressBar, BorderLayout.CENTER);

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
