package cz.mik0486.semestralproject.viewer.menu.file.item;

import cz.mik0486.semestralproject.viewer.Viewer;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Objects;

public class ExportMenuItem extends JMenuItem implements ActionListener {
    private final Viewer viewer;

    public ExportMenuItem(Viewer viewer) {
        super("Export");
        this.viewer = viewer;

        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/icons8-export-16.png")));
        setIcon(icon);

        addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        BufferedImage image = viewer.getAnalyzer().getScanViewer().getCachedImage();

        if (image == null) {
            JOptionPane.showMessageDialog(viewer, "No image to export!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Image");
        fileChooser.setFileFilter(new FileNameExtensionFilter("PNG Images", "png"));
        int userSelection = fileChooser.showSaveDialog(viewer);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();

            if (!filePath.endsWith(".png")) {
                filePath += ".png";
            }

            try {
                ImageIO.write(image, "png", new File(filePath));
                JOptionPane.showMessageDialog(viewer, "Image exported successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (java.io.IOException ex) {
                JOptionPane.showMessageDialog(viewer, "Error exporting image: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
