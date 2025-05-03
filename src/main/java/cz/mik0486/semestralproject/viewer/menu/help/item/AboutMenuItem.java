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

        ImageIcon icon = new ImageIcon(Objects.requireNonNull(
            getClass().getResource("/icons/icons8-about-16.png")));
        setIcon(icon);

        addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String html = """
            <html>
            <h2 style="margin-top: 0;">GenomeVision</h2>
            <p>Desktop application written in <b>Java</b> using the <b>Swing</b> library.<br/>
            Allows you to load and analyze large DNA data files in CSV format and visualize the resulting graphical matrix.</p>
            <h3 style="margin-bottom: 0;">Feature Overview:</h3>
            <ul style="margin: 0; padding-left:10px;">
              <li><b>CSV Loading:</b> fast initialization by loading only the record names.</li>
              <li><b>Progress Bar:</b> shows loading progress asynchronously to keep the UI responsive.</li>
              <li><b>Sample Selection & Filtering:</b> browse and filter the list of available samples.</li>
              <li><b>Epsilon Parameter:</b> set the epsilon value that influences the sample similarity algorithm.</li>
              <li><b>Matrix Generation:</b> create a graphical representation of relationships/metrics between samples.</li>
              <li><b>Results Export:</b> save or export the graphical matrix for further use.</li>
            </ul>
            <br>
            <p><i>Version 1.0</i></p>
            </html>
            """;

        // PLAIN_MESSAGE to remove the default icon
        JOptionPane.showMessageDialog(
            viewer,
            html,
            "About",
            JOptionPane.PLAIN_MESSAGE
        );
    }
}
