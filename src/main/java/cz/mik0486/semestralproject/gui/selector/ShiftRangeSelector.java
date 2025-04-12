package cz.mik0486.semestralproject.gui.selector;

import cz.mik0486.semestralproject.gui.panel.GridPanel;
import cz.mik0486.semestralproject.gui.selector.range.Range;
import cz.mik0486.semestralproject.gui.selector.range.RangeSlider;
import cz.mik0486.semestralproject.utils.Debouncer;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Setter
public class ShiftRangeSelector {
    private final RangeSlider slider;
    private final JTextField lowerValueField;
    private final JTextField upperValueField;
    private Consumer<Range> onShifted;

    public ShiftRangeSelector(int min, int max, int lowerValue, int upperValue) {
        slider = new RangeSlider(min, max, lowerValue, upperValue);
        slider.setMajorTickSpacing(10);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);

        lowerValueField = new JTextField(String.valueOf(lowerValue), 2);
        upperValueField = new JTextField(String.valueOf(upperValue), 2);

        Debouncer<Range> debouncer = new Debouncer<>(300, range -> {
            if (slider.isChanging()) {
                return;
            }

            if (onShifted != null) {
                onShifted.accept(range);
            }
        });

        BiConsumer<Integer, Integer> updateRange = (newLower, newUpper) -> {
            if (newLower < slider.getMinimum() || newUpper > slider.getMaximum() || newLower > newUpper) {
                return;
            }

            slider.setLowerValue(newLower);
            lowerValueField.setText(String.valueOf(newLower));

            slider.setUpperValue(newUpper);
            upperValueField.setText(String.valueOf(newUpper));

            debouncer.call(new Range(newLower, newUpper));
        };

        slider.addChangeListener(e -> {
            lowerValueField.setText(String.valueOf(slider.getLowerValue()));
            upperValueField.setText(String.valueOf(slider.getUpperValue()));

            debouncer.call(new Range(slider.getLowerValue(), slider.getUpperValue()));
        });

        slider.addMouseWheelListener(e -> updateRange.accept(
            slider.getLowerValue() - e.getWheelRotation(),
            slider.getUpperValue() - e.getWheelRotation()
        ));

        lowerValueField.addMouseWheelListener(e -> updateRange.accept(
            Math.max(Math.min(slider.getLowerValue() - e.getWheelRotation(), slider.getUpperValue()), slider.getMinimum()),
            slider.getUpperValue()
        ));

        lowerValueField.addActionListener(e -> updateRange.accept(
            Math.max(Math.min(Integer.parseInt(lowerValueField.getText()), slider.getUpperValue()), slider.getMinimum()),
            slider.getUpperValue()
        ));

        upperValueField.addMouseWheelListener(e -> updateRange.accept(
            slider.getLowerValue(),
            Math.min(Math.max(slider.getUpperValue() - e.getWheelRotation(), slider.getLowerValue()), slider.getMaximum())
        ));

        upperValueField.addActionListener(e -> updateRange.accept(
            slider.getLowerValue(),
            Math.min(Math.max(Integer.parseInt(upperValueField.getText()), slider.getLowerValue()), slider.getMaximum())
        ));
    }

    public JPanel getComponent(boolean horizontal) {
        JPanel component = new JPanel();
        component.setLayout(new BoxLayout(component, horizontal ? BoxLayout.X_AXIS : BoxLayout.Y_AXIS));

        // Compoenent should be only as tall as it needs to be, do not scale
        component.setMaximumSize(new Dimension(Integer.MAX_VALUE, component.getPreferredSize().height));

        GridPanel gridPanel = new GridPanel(2, 5, BorderFactory.createEmptyBorder());

        if (horizontal) {
            gridPanel.add(new JLabel("Min: "), lowerValueField);
            gridPanel.add(new JLabel("Max: "), upperValueField);
        } else {
            JPanel minPanel = new JPanel();
            minPanel.setLayout(new BoxLayout(minPanel, BoxLayout.X_AXIS));
            minPanel.add(new JLabel("Min: "));
            minPanel.add(lowerValueField);

            JPanel maxPanel = new JPanel();
            maxPanel.setLayout(new BoxLayout(maxPanel, BoxLayout.X_AXIS));
            maxPanel.add(new JLabel("Max: "));
            maxPanel.add(upperValueField);

            gridPanel.add(minPanel, new JLabel("-", JLabel.CENTER), maxPanel);
        }

        component.add(slider);

        if (horizontal) {
            component.add(Box.createHorizontalStrut(10));
        } else {
            component.add(Box.createVerticalStrut(10));
        }

        component.add(gridPanel.getPanel());

        return component;
    }

    public Range getValue() {
        return new Range(slider.getLowerValue(), slider.getUpperValue());
    }
}
