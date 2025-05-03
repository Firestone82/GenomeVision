package cz.mik0486.semestralproject.viewer.menu.file.item;

import cz.mik0486.semestralproject.viewer.Viewer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

public class SettingsMenuItem extends JMenuItem implements ActionListener {
    private final Viewer viewer;

    public SettingsMenuItem(Viewer viewer) {
        super("Settings");
        this.viewer = viewer;

        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/icons8-settings-16.png")));
        setIcon(icon);

        addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        viewer.getAnalyzer().getSettings().getSettingsDialog().open();
    }
}
