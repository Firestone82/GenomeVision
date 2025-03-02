package cz.mik0486.semestralproject.gui.selector;

import cz.mik0486.semestralproject.utils.Debouncer;
import lombok.NonNull;
import lombok.Setter;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

@Setter
public class ChecklistSelector<T> {

    private final JPanel column1 = new JPanel();
    private final JPanel column2 = new JPanel();

    private final JTextField filter = new JTextField(20);
    private final HashMap<JCheckBox, T> checkboxes = new HashMap<>();
    private Consumer<List<T>> onSelected;

    public ChecklistSelector() {
        filter.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                applyFilter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                applyFilter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                applyFilter();
            }
        });

        applyFilter();
    }

    public JPanel initUI(String title) {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createTitledBorder(" " + title.trim() + " "));

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        filterPanel.add(new JLabel("Filter:"));
        filterPanel.add(filter);

        // Top panel
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        mainPanel.add(topPanel, BorderLayout.NORTH);

        topPanel.add(filterPanel);
        topPanel.add(Box.createVerticalStrut(4));
        topPanel.add(new JSeparator(SwingConstants.HORIZONTAL));

        // List panel
        JPanel listPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        column1.setLayout(new BoxLayout(column1, BoxLayout.Y_AXIS));
        column1.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        listPanel.add(column1);

        column2.setLayout(new BoxLayout(column2, BoxLayout.Y_AXIS));
        column2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        listPanel.add(column2);

        return mainPanel;
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
        applyFilter();
    }

    public void setItems(@NonNull List<T> items) {
        clearItems();

        Debouncer<List<T>> debouncer = new Debouncer<>(250, val -> {
            if (onSelected != null) {
                onSelected.accept(val);
            }
        });

        for (T item : items) {
            JCheckBox checkbox = new JCheckBox(item.toString());
            checkboxes.put(checkbox, item);
            checkbox.addActionListener(e -> debouncer.call(getSelected()));
        }

        applyFilter();
    }

    public void clearItems() {
        checkboxes.clear();
        column1.removeAll();
        column2.removeAll();
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

    /**
     * Applies the current filter text to determine which checkboxes should be visible,
     * sorts them alphabetically, and then reorders the visible checkboxes evenly into two columns.
     * When no items are available (either nothing loaded or no matching items), a placeholder is shown at the top.
     */
    private void applyFilter() {
        String filterText = filter.getText().trim().toLowerCase();
        List<JCheckBox> filteredCheckboxes = new ArrayList<>();

        for (JCheckBox checkbox : checkboxes.keySet()) {
            String text = checkbox.getText().toLowerCase();
            if (text.contains(filterText) || checkbox.isSelected()) {
                checkbox.setVisible(true);
                filteredCheckboxes.add(checkbox);
            } else {
                checkbox.setVisible(false);
            }
        }

        filteredCheckboxes.sort((a, b) -> {
            if (a.isSelected() && !b.isSelected()) {
                return -1;
            } else if (!a.isSelected() && b.isSelected()) {
                return 1;
            } else {
                return a.getText().compareTo(b.getText());
            }
        });

        column1.removeAll();
        column2.removeAll();

        int total = filteredCheckboxes.size();
        if (total == 0) {
            String placeholderText = filterText.isEmpty() ? "No items loaded" : "No matching items";
            JLabel placeholderLabel = new JLabel(placeholderText);

            placeholderLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            column1.setLayout(new BoxLayout(column1, BoxLayout.Y_AXIS));
            column1.add(placeholderLabel);
        } else {
            column1.setLayout(new BoxLayout(column1, BoxLayout.Y_AXIS));
            column2.setLayout(new BoxLayout(column2, BoxLayout.Y_AXIS));

            int firstColumnCount = (total + 1) / 2;
            for (int i = 0; i < total; i++) {
                JCheckBox checkbox = filteredCheckboxes.get(i);

                if (i < firstColumnCount) {
                    column1.add(checkbox);
                } else {
                    column2.add(checkbox);
                }
            }
        }

        column1.revalidate();
        column1.repaint();

        column2.revalidate();
        column2.repaint();
    }
}
