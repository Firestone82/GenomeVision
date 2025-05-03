package cz.mik0486.semestralproject.gui.panel;

import lombok.Getter;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

@Getter
public class GridPanel {
    private final JPanel panel;
    private final int gapX;
    private final int gapY;

    // Tracks the current row number.
    private int currentRow = 0;

    /**
     * Constructs a new GridPanel.
     *
     * @param gapX   The horizontal gap to insert only between columns.
     * @param gapY   The vertical gap to insert only between rows.
     * @param border The border to set on the panel.
     */
    public GridPanel(int gapX, int gapY, Border border) {
        this.gapX = gapX;
        this.gapY = gapY;
        panel = new JPanel(new GridBagLayout());
        panel.setBorder(border);
    }

    /**
     * Adds a new row to the grid with the provided components.
     * Each row can have a variable number of columns.
     * If a row is added with fewer components than desired, no extra fillers are added.
     * <p>
     * Gaps are applied only between cells:
     * <ul>
     *   <li>No vertical gap above the first row.</li>
     *   <li>No horizontal gap to the left of the first column.</li>
     *   <li>The other cells have a gap of gapY (top) or gapX (left).</li>
     * </ul>
     *
     * @param components The components to be added as a row.
     */
    public void add(Component... components) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = currentRow;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; // Allow components to expand horizontally.

        for (int i = 0; i < components.length; i++) {
            gbc.gridx = i;
            int topInset = (currentRow == 0) ? 0 : gapY;
            int leftInset = (i == 0) ? 0 : gapX;
            gbc.insets = new Insets(topInset, leftInset, 0, 0);
            panel.add(components[i], gbc);
        }

        currentRow++;
    }

    /**
     * Adds a single component that spans multiple columns in the current row.
     * Useful for a full-width component when you only have one item on the row.
     *
     * @param component The component to add.
     * @param colSpan   The number of columns to span.
     */
    public void add(Component component, int colSpan) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = currentRow;
        gbc.gridx = 0;
        gbc.gridwidth = colSpan;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        int topInset = (currentRow == 0) ? 0 : gapY;
        // no left gap when spanning from first column
        gbc.insets = new Insets(topInset, 0, 0, 0);
        panel.add(component, gbc);

        currentRow++;
    }
}
