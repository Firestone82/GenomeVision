package cz.mik0486.semestralproject.viewer.menu.file;

import cz.mik0486.semestralproject.viewer.Viewer;
import cz.mik0486.semestralproject.viewer.menu.file.item.*;
import lombok.Getter;

import javax.swing.*;

@Getter
public class FileMenu extends JMenu {
    private final Viewer viewer;

    public FileMenu(Viewer viewer) {
        super("File");
        this.viewer = viewer;

        add(new OpenFileMenuItem(viewer));
        add(new CloseFileMenuItem(this));
        addSeparator();
        add(new SettingsMenuItem(this));
        addSeparator();
        add(new ExportMenuItem(this));
        addSeparator();
        add(new ExitMenuItem(this));
    }
}
