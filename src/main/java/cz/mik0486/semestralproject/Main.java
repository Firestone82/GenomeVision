package cz.mik0486.semestralproject;

import cz.mik0486.semestralproject.viewer.Viewer;
import lombok.extern.log4j.Log4j2;

import javax.swing.*;

@Log4j2
public class Main {

    public static void main(String[] args) {
        log.info("Starting the application.");

        SwingUtilities.invokeLater(() -> {
            Viewer viewer = new Viewer();
            viewer.setVisible(true);
        });
    }
}
