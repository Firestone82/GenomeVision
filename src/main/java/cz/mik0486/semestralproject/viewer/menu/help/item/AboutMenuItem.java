package cz.mik0486.semestralproject.viewer.menu.help.item;

import cz.mik0486.semestralproject.viewer.Viewer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

public class AboutMenuItem extends JMenuItem implements ActionListener {
    private final Viewer viewer;

    public AboutMenuItem(Viewer viewer) {
        super("About");
        this.viewer = viewer;

        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/icons8-about-16.png")));
        setIcon(icon);

        addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JOptionPane.showMessageDialog(viewer,
            "DNA Application TODO: rest n\nVersion 1.0",
            "About", JOptionPane.INFORMATION_MESSAGE
        );
    }
}
