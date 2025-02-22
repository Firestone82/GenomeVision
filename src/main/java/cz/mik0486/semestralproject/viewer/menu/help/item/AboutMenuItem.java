package cz.mik0486.semestralproject.viewer.menu.help.item;

import cz.mik0486.semestralproject.viewer.menu.help.HelpMenu;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AboutMenuItem extends JMenuItem implements ActionListener {
    private final HelpMenu helpMenu;

    public AboutMenuItem(HelpMenu helpMenu) {
        super("About");
        this.helpMenu = helpMenu;
        addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JOptionPane.showMessageDialog(helpMenu.getViewer(),
            "DNA Application TODO: rest n\nVersion 1.0",
            "About", JOptionPane.INFORMATION_MESSAGE);
    }
}
