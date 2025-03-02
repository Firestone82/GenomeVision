package cz.mik0486.semestralproject.viewer.menu.help;

import cz.mik0486.semestralproject.viewer.Viewer;
import cz.mik0486.semestralproject.viewer.menu.help.item.AboutMenuItem;
import lombok.Getter;

import javax.swing.*;

@Getter
public class HelpMenu extends JMenu {
    private final Viewer viewer;

    public HelpMenu(Viewer viewer) {
        super("Help");
        this.viewer = viewer;

        add(new AboutMenuItem(viewer));
    }
}
