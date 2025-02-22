package cz.mik0486.semestralproject.gui;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;

@Getter
public abstract class ZoomableGrabbablePane {
    protected final JPanel panel;

    @Setter
    private boolean grabbable;

    @Setter
    private boolean zoomable;

    @Setter
    private boolean transparent;

    private double scale = 1.0;
    private double translateX = 0;
    private double translateY = 0;

    @Getter(AccessLevel.NONE)
    private Point lastMousePoint;

    public ZoomableGrabbablePane(boolean grabbable, boolean zoomable, boolean transparent) {
        this.grabbable = grabbable;
        this.zoomable = zoomable;
        this.transparent = transparent;

        this.panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g;
                AffineTransform originalTransform = g2.getTransform();

                if (!isTransparent()) {
                    g2.setColor(Color.WHITE);
                    g2.fillRect(0, 0, getPanel().getWidth(), getPanel().getHeight());
                }

                // Apply translation and scaling (zoom)
                g2.translate(translateX, translateY);
                g2.scale(scale, scale);

                // Paint the content
                ZoomableGrabbablePane.this.paint(g2);

                // Restore original transform
                g2.setTransform(originalTransform);
            }
        };

        this.panel.addMouseWheelListener(e -> {
            if (isZoomable()) {
                Point mousePoint = e.getPoint();
                double oldScale = scale;

                if (e.getWheelRotation() < 0) {
                    scale *= 1.1;
                } else {
                    scale /= 1.1;
                }

                double offsetX = mousePoint.getX() - translateX;
                double offsetY = mousePoint.getY() - translateY;
                translateX = mousePoint.getX() - offsetX * (scale / oldScale);
                translateY = mousePoint.getY() - offsetY * (scale / oldScale);

                this.panel.repaint();
            }
        });

        this.panel.addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {
                if (isGrabbable() && lastMousePoint != null) {
                    Point currentPoint = e.getPoint();

                    int dx = currentPoint.x - lastMousePoint.x;
                    int dy = currentPoint.y - lastMousePoint.y;
                    translateX += dx;
                    translateY += dy;

                    lastMousePoint = currentPoint;

                    panel.repaint();
                }
            }
        });

        this.panel.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                lastMousePoint = e.getPoint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                lastMousePoint = null;
            }
        });
    }

    public void resetZoom() {
        scale = 1.0;
        translateX = 0;
        translateY = 0;
        panel.repaint();
    }

    public boolean isZoomed() {
        return scale != 1.0 || translateX != 0 || translateY != 0;
    }

    public abstract void paint(Graphics2D g2);

}
