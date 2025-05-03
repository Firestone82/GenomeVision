package cz.mik0486.semestralproject.viewer.settings;

import cz.mik0486.semestralproject.gui.panel.GridPanel;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

@Slf4j
public class SettingsDialog {
    private final Settings settings;
    private final JDialog dialog;

    private final JButton btnEmptyColor;
    private final JButton btnGradientStart;
    private final JButton btnGradientEnd;
    private final JPanel gradientPreview;

    public SettingsDialog(Settings settings) {
        this.settings = settings;
        dialog = new JDialog(settings.getAnalyzer().getViewer(), "Settings", true);

        GridPanel gp = new GridPanel(5, 5, new EmptyBorder(10, 10, 10, 10));

        btnEmptyColor = new JButton();
        btnEmptyColor.setBackground(settings.getEmptyCellColor());
        btnEmptyColor.setPreferredSize(new Dimension(40, 20));
        btnEmptyColor.addActionListener(this::chooseEmptyColor);
        gp.add(new JLabel("Empty Cell Color:"), btnEmptyColor);

        btnGradientStart = new JButton();
        btnGradientStart.setBackground(settings.getGradientStartColor());
        btnGradientStart.setPreferredSize(new Dimension(40, 20));
        btnGradientStart.addActionListener(this::chooseGradientStart);
        gp.add(new JLabel("DNA Methyl Gradient Start:"), btnGradientStart);

        btnGradientEnd = new JButton();
        btnGradientEnd.setBackground(settings.getGradientEndColor());
        btnGradientEnd.setPreferredSize(new Dimension(40, 20));
        btnGradientEnd.addActionListener(this::chooseGradientEnd);
        gp.add(new JLabel("DNA Methyl Gradient End:"), btnGradientEnd);

        gradientPreview = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int w = getWidth(), h = getHeight();

                GradientPaint gpPaint = new GradientPaint(
                    0, 0, settings.getGradientStartColor(),
                    w, h, settings.getGradientEndColor()
                );

                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(gpPaint);
                g2.fillRect(10, 20, w - 20, h - 30);
            }
        };
        gradientPreview.setPreferredSize(new Dimension(200, 50));
        gradientPreview.setBorder(BorderFactory.createTitledBorder("Gradient Preview"));
        gp.add(gradientPreview, 2);

        JButton btnOk = new JButton("Save");
        btnOk.addActionListener(e -> close());
        gp.add(new JLabel(), btnOk);

        dialog.setContentPane(gp.getPanel());
        dialog.pack();
        dialog.setLocationRelativeTo(dialog.getOwner());
    }

    private void chooseEmptyColor(ActionEvent e) {
        Color chosen = JColorChooser.showDialog(dialog, "Choose Empty Cell Color", settings.getEmptyCellColor());
        if (chosen != null) {
            settings.setEmptyCellColor(chosen);
            btnEmptyColor.setBackground(chosen);
        }
    }

    private void chooseGradientStart(ActionEvent e) {
        Color chosen = JColorChooser.showDialog(dialog, "Choose Gradient Start Color", settings.getGradientStartColor());
        if (chosen != null) {
            settings.setGradientStartColor(chosen);
            btnGradientStart.setBackground(chosen);
            gradientPreview.repaint();
        }
    }

    private void chooseGradientEnd(ActionEvent e) {
        Color chosen = JColorChooser.showDialog(dialog, "Choose Gradient End Color", settings.getGradientEndColor());
        if (chosen != null) {
            settings.setGradientEndColor(chosen);
            btnGradientEnd.setBackground(chosen);
            gradientPreview.repaint();
        }
    }

    public void open() {
        dialog.setVisible(true);
    }

    public void close() {
        dialog.dispose();
        settings.getOnChange().run();

        try {
            settings.save();
        } catch (IOException e) {
            log.error("Failed to save settings: {}", e.getMessage());
            JOptionPane.showMessageDialog(dialog, "Failed to save settings: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
