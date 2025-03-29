package cz.mik0486.semestralproject.gui.selector.range;

import lombok.Getter;

import javax.swing.*;

@Getter
public class RangeSlider extends JSlider {
    private int lowerValue;
    private int upperValue;
    private RangeSliderUI ui;

    public RangeSlider(int min, int max, int lowerValue, int upperValue) {
        super(min, max);

        if (lowerValue > upperValue) {
            throw new IllegalArgumentException("lowerValue cannot be greater than upperValue");
        }

        this.lowerValue = lowerValue;
        this.upperValue = upperValue;

        // Use the custom UI that knows how to paint two thumbs.
        ui = new RangeSliderUI(this);
        setUI(ui);
    }

    public void setLowerValue(int value) {
        lowerValue = Math.max(getMinimum(), Math.min(value, upperValue));
        repaint();
    }

    public void setUpperValue(int value) {
        upperValue = Math.min(getMaximum(), Math.max(value, lowerValue));
        repaint();
    }

    public boolean isChanging() {
        return ui.isDragging();
    }
}
