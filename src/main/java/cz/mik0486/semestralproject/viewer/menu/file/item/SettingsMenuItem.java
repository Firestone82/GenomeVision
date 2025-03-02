package cz.mik0486.semestralproject.viewer.menu.file.item;

import cz.mik0486.semestralproject.viewer.menu.file.FileMenu;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

public class SettingsMenuItem extends JMenuItem implements ActionListener {
    private final FileMenu fileMenu;

    public SettingsMenuItem(FileMenu fileMenu) {
        super("Settings");
        this.fileMenu = fileMenu;

        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/icons8-settings-16.png")));
        setIcon(icon);

        addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO: Open settings dialog
    }
}
