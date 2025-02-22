package cz.mik0486.semestralproject.viewer.menu;

import cz.mik0486.semestralproject.viewer.Viewer;
import cz.mik0486.semestralproject.viewer.menu.file.FileMenu;
import cz.mik0486.semestralproject.viewer.menu.help.HelpMenu;

import javax.swing.*;

public class Menu extends JMenuBar {

    public Menu(Viewer viewer) {
        add(new FileMenu(viewer));
        add(new HelpMenu(viewer));
    }
}
