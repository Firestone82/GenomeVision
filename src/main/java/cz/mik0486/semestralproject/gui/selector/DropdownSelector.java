package cz.mik0486.semestralproject.gui.selector;

import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
@Setter
public class DropdownSelector<T> {
    private final JComboBox<T> comboBox = new JComboBox<>();
    private Consumer<T> onSelected;

    public DropdownSelector() {
        comboBox.addItem(null);
        comboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, comboBox.getPreferredSize().height));

        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value == null) {
                    setText("Click to select");
                    setForeground(Color.GRAY);
                } else {
                    setText(value.toString());
                    setForeground(Color.BLACK);
                }

                return this;
            }
        });

        comboBox.addActionListener(e -> {
            T selected = getSelected();

            if (selected != null) {
                if (comboBox.getItemCount() > 0 && comboBox.getItemAt(0) == null) {
                    comboBox.removeItemAt(0);
                }

                if (onSelected != null) {
                    onSelected.accept(selected);
                }
            }
        });
    }

    public Component getComponent() {
        return comboBox;
    }

    public void add(T item) {
        comboBox.addItem(item);
    }

    public void setItems(@NonNull List<T> items) {
        clearItems();

        for (T item : items) {
            comboBox.addItem(item);
        }
    }

    public void clearItems() {
        comboBox.removeAllItems();
        comboBox.addItem(null);
        comboBox.setSelectedItem(null);
    }

    @SuppressWarnings("unchecked")
    public T getSelected() {
        if (comboBox.getItemCount() == 0) {
            return null;
        }

        return (T) comboBox.getSelectedItem();
    }

    public boolean hasSelected() {
        return getSelected() != null;
    }
}
