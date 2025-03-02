package cz.mik0486.semestralproject;

import cz.mik0486.semestralproject.viewer.Viewer;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;

@Slf4j
public class Main {

    public static void main(String[] args) {
        log.info("Starting the application.");

        SwingUtilities.invokeLater(() -> {
            Viewer viewer = new Viewer();
            viewer.setVisible(true);
        });
    }
}
