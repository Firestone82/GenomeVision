package cz.mik0486.semestralproject.gui;

import lombok.Getter;
import lombok.NonNull;

import javax.swing.*;
import java.awt.*;
import java.util.List;

@Getter
public abstract class DropdownSelector<T> {
    private final JPanel panel = new JPanel();

    private final JLabel label;
    private final JComboBox<T> comboBox;
    private final String defaultText = "Click to select";

    public DropdownSelector(String labelText) {
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));

        label = new JLabel(labelText);
        panel.add(label);

        comboBox = new JComboBox<>();
        comboBox.addItem(null);
        panel.add(comboBox);

        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value == null) {
                    setText(defaultText);
                    setForeground(Color.GRAY);
                } else {
                    setText(value.toString());
                    setForeground(Color.BLACK);
                }

                return this;
            }
        });

        comboBox.addActionListener(e -> {
            @SuppressWarnings("unchecked")
            T selected = (T) comboBox.getSelectedItem();

            if (selected != null) {
                if (comboBox.getItemCount() > 0 && comboBox.getItemAt(0) == null) {
                    comboBox.removeItemAt(0);
                }

                onSelected(selected);
            }
        });
    }

    public void setItems(@NonNull List<T> items) {
        comboBox.removeAllItems();

        comboBox.addItem(null);
        for (T item : items) {
            comboBox.addItem(item);
        }

        comboBox.setSelectedItem(null);
        comboBox.repaint();
    }

    public void clearItems() {
        comboBox.removeAllItems();
        comboBox.addItem(null);
        comboBox.repaint();
    }

    public abstract void onSelected(@NonNull T selected);
}
