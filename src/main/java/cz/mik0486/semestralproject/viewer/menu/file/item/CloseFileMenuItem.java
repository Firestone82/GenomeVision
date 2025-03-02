package cz.mik0486.semestralproject.viewer.menu.file.item;

import cz.mik0486.semestralproject.viewer.Viewer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CloseFileMenuItem extends JMenuItem implements ActionListener {
    private final Viewer viewer;

    public CloseFileMenuItem(Viewer viewer) {
        super("Close file");
        this.viewer = viewer;

        addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        viewer.getAnalyzer().closeFile();
    }
}
