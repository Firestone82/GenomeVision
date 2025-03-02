package cz.mik0486.semestralproject.viewer.menu.file.item;

import cz.mik0486.semestralproject.viewer.Viewer;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class OpenFileMenuItem extends JMenuItem implements ActionListener {
    private final Viewer viewer;

    public OpenFileMenuItem(Viewer viewer) {
        super("Open file");
        this.viewer = viewer;
        addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();

        // Set the default directory to the resources/data directory.
        File currentDir = new File(System.getProperty("user.dir"));
        File resourceDir = new File(currentDir, "data");

        if (resourceDir.exists()) {
            fileChooser.setCurrentDirectory(resourceDir);
        } else {
            fileChooser.setCurrentDirectory(currentDir);
        }

        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV File", "csv", "txt");
        fileChooser.setFileFilter(filter);

        switch (fileChooser.showOpenDialog(viewer)) {
            case JFileChooser.APPROVE_OPTION -> viewer.getAnalyzer().loadFile(fileChooser.getSelectedFile());
            case JFileChooser.CANCEL_OPTION -> System.out.println("Open command cancelled by user.");
            case JFileChooser.ERROR_OPTION -> System.err.println("Error occurred while opening file.");
        }
    }


}
