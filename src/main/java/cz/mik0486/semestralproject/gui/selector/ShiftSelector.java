package cz.mik0486.semestralproject.gui.selector;

import cz.mik0486.semestralproject.utils.Debouncer;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseWheelListener;
import java.util.function.Consumer;

@Setter
public class ShiftSelector {
    private final JSlider slider;
    private final JTextField valueField;
    private Consumer<Integer> onShifted;

    public ShiftSelector(int min, int max, int value) {
        this.slider = new JSlider(min, max, value);
        this.slider.setMajorTickSpacing(10);
        this.slider.setPaintTicks(true);
        this.slider.setPaintLabels(true);

        this.valueField = new JTextField(String.valueOf(value), 2);

        Debouncer<Integer> debouncer = new Debouncer<>(250, val -> {
            if (onShifted == null) {
                return;
            }

            onShifted.accept(val);
        });

        slider.addChangeListener(e -> {
            int val = slider.getValue();
            valueField.setText(String.valueOf(val));

            if (!slider.getValueIsAdjusting()) {
                debouncer.call(val);
            }
        });

        valueField.addActionListener(e -> {
            try {
                int input = Integer.parseInt(valueField.getText());
                int val = Math.max(slider.getMinimum(), Math.min(slider.getMaximum(), input));

                slider.setValue(val);
                debouncer.call(val);
            } catch (NumberFormatException ex) {
                valueField.setText(String.valueOf(slider.getValue()));
            }
        });

        MouseWheelListener listener = e -> {
            int val = slider.getValue() + e.getWheelRotation();
            val = Math.max(slider.getMinimum(), Math.min(slider.getMaximum(), val));

            slider.setValue(val);
            debouncer.call(val);
        };

        slider.addMouseWheelListener(listener);
        valueField.addMouseWheelListener(listener);
    }

    public JPanel getComponent() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        mainPanel.add(slider);
        mainPanel.add(valueField);

        return mainPanel;
    }

    public int getValue() {
        return slider.getValue();
    }
}
