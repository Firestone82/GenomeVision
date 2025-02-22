package cz.mik0486.semestralproject.viewer.menu.file.item;

import cz.mik0486.semestralproject.viewer.menu.file.FileMenu;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ExportMenuItem extends JMenuItem implements ActionListener {
    private final FileMenu fileMenu;

    public ExportMenuItem(FileMenu fileMenu) {
        super("Export");
        this.fileMenu = fileMenu;
        addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO: Open export dialog
    }
}
