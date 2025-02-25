package cz.mik0486.semestralproject.gui;

import cz.mik0486.semestralproject.utils.log.Debouncer;
import lombok.NonNull;
import lombok.Setter;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class ChecklistSelector<T> {

    private final JPanel checklist = new JPanel();
    private final HashMap<JCheckBox, T> checkboxes = new HashMap<>();

    @Setter
    private Consumer<List<T>> onSelected;

    public ChecklistSelector() {
        checklist.setLayout(new BoxLayout(checklist, BoxLayout.Y_AXIS));
        checklist.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    public JScrollPane initUI(String title) {
        JScrollPane jScrollPane = new JScrollPane(checklist);
        jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jScrollPane.setBorder(BorderFactory.createTitledBorder(title));

        return jScrollPane;
    }

    public void setItems(@NonNull List<T> items) {
        clearItems();

        Debouncer<List<T>> debouncer = new Debouncer<>(200, val -> {
            if (onSelected == null) {
                return;
            }

            onSelected.accept(val);
        });

        for (T item : items) {
            JCheckBox checkbox = new JCheckBox(item.toString());
            checkboxes.put(checkbox, item);
            checklist.add(checkbox);

            checkbox.addActionListener(e -> debouncer.call(getSelected()));
        }
    }

    public void clearItems() {
        checkboxes.clear();
        checklist.removeAll();
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
}
