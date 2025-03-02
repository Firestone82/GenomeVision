package cz.mik0486.semestralproject.gui.selector;

import cz.mik0486.semestralproject.utils.Debouncer;
import lombok.NonNull;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

@Setter
public class ChecklistSelector<T> {

    private final JPanel mainPanel = new JPanel(new GridLayout(1, 2, 5, 5));
    private final JPanel column1 = new JPanel();
    private final JPanel column2 = new JPanel();

    private final HashMap<JCheckBox, T> checkboxes = new HashMap<>();
    private Consumer<List<T>> onSelected;

    public JScrollPane initUI(String title) {
        column1.setLayout(new BoxLayout(column1, BoxLayout.Y_AXIS));
        column1.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        mainPanel.add(column1);

        column2.setLayout(new BoxLayout(column2, BoxLayout.Y_AXIS));
        column2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        mainPanel.add(column2);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createTitledBorder(" " + title.trim() + " "));

        return scrollPane;
    }

    public void hideItem(T item) {
        for (JCheckBox checkbox : checkboxes.keySet()) {
            if (checkbox.getText().equals(item.toString())) {
                checkbox.setVisible(false);
                checkbox.setSelected(false);
            } else {
                checkbox.setVisible(true);
            }
        }
    }

    public void setItems(@NonNull List<T> items) {
        clearItems();

        Debouncer<List<T>> debouncer = new Debouncer<>(250, val -> {
            if (onSelected != null) {
                onSelected.accept(val);
            }
        });

        int total = items.size();
        int firstColumnCount = (total + 1) / 2;

        for (int i = 0; i < total; i++) {
            T item = items.get(i);

            JCheckBox checkbox = new JCheckBox(item.toString());
            checkboxes.put(checkbox, item);

            if (i < firstColumnCount) {
                column1.add(checkbox);
            } else {
                column2.add(checkbox);
            }

            checkbox.addActionListener(e -> debouncer.call(getSelected()));
        }

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    public void clearItems() {
        checkboxes.clear();
        column1.removeAll();
        column2.removeAll();

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    public List<T> getSelected() {
        List<T> selected = new ArrayList<>();

        for (JCheckBox checkbox : checkboxes.keySet()) {
            if (checkbox.isSelected()) {
                selected.add(checkboxes.get(checkbox));
            }
        }

        return selected;
    }

    public boolean hasSelected() {
        return !getSelected().isEmpty();
    }
}
