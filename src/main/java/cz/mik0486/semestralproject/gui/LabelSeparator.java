package cz.mik0486.semestralproject.gui;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;

@Getter
public class LabelSeparator {

    private final JPanel component = new JPanel();

    public LabelSeparator(String label) {
        component.setLayout(new BoxLayout(component, BoxLayout.X_AXIS));
        component.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        component.add(new JLabel(label, SwingConstants.LEFT), BorderLayout.NORTH);

        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        component.add(Box.createHorizontalStrut(5));
        component.add(separator);

        component.setMaximumSize(new Dimension(Integer.MAX_VALUE, component.getPreferredSize().height));
        component.setPreferredSize(new Dimension(0, component.getPreferredSize().height));
        component.setAlignmentX(Component.LEFT_ALIGNMENT);
        component.setAlignmentY(Component.TOP_ALIGNMENT);
    }

    public void addButton(String label, Runnable action) {
        JButton button = new JButton(label);
        button.addActionListener(e -> action.run());

        component.add(Box.createHorizontalStrut(5));
        component.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        component.setMaximumSize(new Dimension(Integer.MAX_VALUE, component.getPreferredSize().height + 5));
        component.add(button, BorderLayout.SOUTH);
    }
}
