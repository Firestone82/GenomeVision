package cz.mik0486.semestralproject.viewer.menu.file.item;

import cz.mik0486.semestralproject.viewer.Viewer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
        // TODO: Open export dialog
    }
}
