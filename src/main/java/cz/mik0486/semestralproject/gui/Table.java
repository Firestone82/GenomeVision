package cz.mik0486.semestralproject.gui;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class Table {

    private final JPanel labelColumn = new JPanel();
    private final JPanel valueColumn = new JPanel();

    private final HashMap<String, JLabel> rows = new HashMap<>();

    public JPanel initUI(String title) {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
        mainPanel.setBorder(BorderFactory.createTitledBorder(" " + title.trim() + " "));

        labelColumn.setLayout(new BoxLayout(labelColumn, BoxLayout.Y_AXIS));
        labelColumn.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        mainPanel.add(labelColumn);
        mainPanel.add(Box.createHorizontalStrut(5));  // spacing between columns

        valueColumn.setLayout(new BoxLayout(valueColumn, BoxLayout.Y_AXIS));
        valueColumn.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        valueColumn.setPreferredSize(new Dimension(0, valueColumn.getPreferredSize().height));
        mainPanel.add(valueColumn);

        Dimension pref = mainPanel.getPreferredSize();
        mainPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, pref.height));

        return mainPanel;
    }

    public void clear(boolean clearLabels) {
        if (clearLabels) {
            labelColumn.removeAll();
            valueColumn.removeAll();
            rows.clear();
        } else {
            for (JLabel label : rows.values()) {
                label.setText("N/A");
            }
        }

        labelColumn.revalidate();
        labelColumn.repaint();
        valueColumn.revalidate();
        valueColumn.repaint();
    }

    public void setValue(String name, Object value) {
        JLabel valueLabel = rows.get(name);

        if (valueLabel == null) {
            JLabel keyLabel = new JLabel(name);
            labelColumn.add(keyLabel);

            valueLabel = new JLabel(value.toString());
            valueColumn.add(valueLabel);

            rows.put(name, valueLabel);
        } else {
            valueLabel.setText(value.toString());
        }

        int maxWidth = 0;
        for (Component comp : valueColumn.getComponents()) {
            if (comp instanceof JLabel) {
                Dimension d = comp.getPreferredSize();
                maxWidth = Math.max(maxWidth, d.width);
            }
        }

        Dimension currentPref = valueColumn.getPreferredSize();
        valueColumn.setPreferredSize(new Dimension(maxWidth, currentPref.height));
        valueColumn.revalidate();
        valueColumn.repaint();
    }
}
