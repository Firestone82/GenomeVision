package cz.mik0486.semestralproject.gui.selector.range;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;
import java.awt.event.MouseEvent;

public class RangeSliderUI extends BasicSliderUI {
    private static final int THUMB_SIZE = 12;

    private final Rectangle lowerThumbRect;
    private final Rectangle upperThumbRect;
    private Thumb selectedThumb = Thumb.NONE;

    private enum Thumb {LOWER, UPPER, NONE}

    public RangeSliderUI(RangeSlider slider) {
        super(slider);
        lowerThumbRect = new Rectangle(0, 0, THUMB_SIZE, THUMB_SIZE);
        upperThumbRect = new Rectangle(0, 0, THUMB_SIZE, THUMB_SIZE);
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);

        slider.removeMouseListener(trackListener);
        slider.removeMouseMotionListener(trackListener);

        trackListener = new RangeTrackListener();

        slider.addMouseListener(trackListener);
        slider.addMouseMotionListener(trackListener);
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        super.paint(g, c);

        int lowerPos = xPositionForValue(((RangeSlider) slider).getLowerValue());
        int upperPos = xPositionForValue(((RangeSlider) slider).getUpperValue());

        g.setColor(new Color(255, 0, 0, 200));
        g.fillRect(lowerPos, trackRect.y + trackRect.height / 2 - 2, upperPos - lowerPos, 2);

        paintThumb(g, lowerThumbRect, lowerPos);
        paintThumb(g, upperThumbRect, upperPos);
    }

    private void paintThumb(Graphics g, Rectangle rect, int pos) {
        int yPos = trackRect.y + (trackRect.height - THUMB_SIZE) / 2;
        rect.setLocation(pos - THUMB_SIZE / 2, yPos);

        Rectangle oldThumbRect = thumbRect;
        thumbRect = rect;
        super.paintThumb(g);
        thumbRect = oldThumbRect;
    }

    @Override
    protected TrackListener createTrackListener(JSlider slider) {
        return new RangeTrackListener();
    }

    private class RangeTrackListener extends TrackListener {
        @Override
        public void mousePressed(MouseEvent e) {
            if (!slider.isEnabled()) {
                return;
            }

            Point p = e.getPoint();
            if (lowerThumbRect.contains(p)) {
                selectedThumb = Thumb.LOWER;
            } else if (upperThumbRect.contains(p)) {
                selectedThumb = Thumb.UPPER;
            } else {
                selectedThumb = Thumb.NONE;
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (!slider.isEnabled() || selectedThumb == Thumb.NONE) {
                return;
            }

            int x = e.getX();
            int value = valueForXPosition(x);
            RangeSlider rangeSlider = (RangeSlider) slider;

            if (selectedThumb == Thumb.LOWER) {
                rangeSlider.setLowerValue(value);
            } else if (selectedThumb == Thumb.UPPER) {
                rangeSlider.setUpperValue(value);
            }

            slider.setValue(value);
            slider.repaint();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            selectedThumb = Thumb.NONE;

            if (slider instanceof RangeSlider rangeSlider) {
                for (ChangeListener listener : rangeSlider.getChangeListeners()) {
                    listener.stateChanged(null);
                }
            }
        }
    }

    @Override
    public void paintThumb(Graphics g) {
        // Do nothing
    }

    @Override
    protected boolean isDragging() {
        return selectedThumb != Thumb.NONE;
    }
}

