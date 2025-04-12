package cz.mik0486.semestralproject.gui.panel.graph;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class GraphPanel {
    protected final JPanel panel;

    private static final int PADDING = 50;
    private static final int POINT_RADIUS = 2;

    private final List<Point2D> dataPoints = new ArrayList<>();
    private Point hoveredPoint = null;

    private final Map<String, HorizontalLine> horizontalLines = new HashMap<>();
    private final Map<String, VerticalLine> verticalLines = new HashMap<>();

    public GraphPanel() {
        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGraph((Graphics2D) g);
            }
        };

        panel.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                hoveredPoint = null;
                for (Point2D p : dataPoints) {
                    Point screenPoint = dataToScreen(p);
                    if (screenPoint.distance(e.getPoint()) <= POINT_RADIUS * 2) {
                        hoveredPoint = screenPoint;
                        panel.setToolTipText(String.format("(%.2f, %.2f)", p.getX(), p.getY()));
                        break;
                    }
                }
                if (hoveredPoint == null) {
                    panel.setToolTipText(null);
                }
                panel.repaint();
            }
        });
    }

    public void setPoints(List<Point2D> points) {
        dataPoints.clear();
        dataPoints.addAll(points);
        panel.repaint();
    }

    public void setHorizontalLine(String id, double yValue, HighlightRegion highlightRegion) {
        HorizontalLine line = horizontalLines.get(id);

        if (line != null) {
            line.setYValue(yValue);
            line.setHighlightRegion(highlightRegion);
        } else {
            line = new HorizontalLine(id, yValue, highlightRegion);
            horizontalLines.put(id, line);
        }

        panel.repaint();
    }

    public void removeHorizontalLine(String id) {
        if (horizontalLines.containsKey(id)) {
            horizontalLines.remove(id);
            panel.repaint();
        }
    }

    public void setVerticalLine(String id, double xValue, HighlightRegion highlightRegion) {
        VerticalLine line = verticalLines.get(id);

        if (line != null) {
            line.setXValue(xValue);
            line.setHighlightRegion(highlightRegion);
        } else {
            line = new VerticalLine(id, xValue, highlightRegion);
            verticalLines.put(id, line);
        }

        panel.repaint();
    }

    public void removeVerticalLine(String id) {
        if (verticalLines.containsKey(id)) {
            verticalLines.remove(id);
            panel.repaint();
        }
    }

    // --------------- Helper Methods ----------------

    // Transforms a data point to a screen coordinate.
    private Point dataToScreen(Point2D point) {
        double minX = dataPoints.stream().mapToDouble(Point2D::getX).min().orElse(0);
        double maxX = dataPoints.stream().mapToDouble(Point2D::getX).max().orElse(10);
        double minY = dataPoints.stream().mapToDouble(Point2D::getY).min().orElse(0);
        double maxY = dataPoints.stream().mapToDouble(Point2D::getY).max().orElse(10);

        double xScale = (panel.getWidth() - 2.0 * PADDING) / (maxX - minX);
        double yScale = (panel.getHeight() - 2.0 * PADDING) / (maxY - minY);
        int x = (int) (PADDING + (point.getX() - minX) * xScale);
        int y = (int) (panel.getHeight() - PADDING - (point.getY() - minY) * yScale);
        return new Point(x, y);
    }

    // --------------- Drawing the Graph ----------------

    private void drawGraph(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int width = panel.getWidth();
        int height = panel.getHeight();

        double minX = dataPoints.stream().mapToDouble(Point2D::getX).min().orElse(0);
        double maxX = dataPoints.stream().mapToDouble(Point2D::getX).max().orElse(10);
        double minY = dataPoints.stream().mapToDouble(Point2D::getY).min().orElse(0);
        double maxY = dataPoints.stream().mapToDouble(Point2D::getY).max().orElse(10);

        int numXTicks = 10;
        int numYTicks = 10;

        double xScale = (width - 2.0 * PADDING) / (maxX - minX);
        double yScale = (height - 2.0 * PADDING) / (maxY - minY);

        // ------------------ Draw Grid & Ticks ------------------

        // Draw horizontal grid lines and Y axis ticks.
        g2.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i <= numYTicks; i++) {
            int y = (int) (height - PADDING - i * (height - 2 * PADDING) / numYTicks);
            g2.drawLine(PADDING, y, width - PADDING, y);
            double yValue = minY + i * (maxY - minY) / numYTicks;
            String label = String.format("%.1f", yValue);
            FontMetrics fm = g2.getFontMetrics();
            int labelWidth = fm.stringWidth(label);
            g2.setColor(Color.BLACK);
            g2.drawString(label, PADDING - labelWidth - 5, y + fm.getAscent() / 2);
            g2.setColor(Color.LIGHT_GRAY);
        }

        // Draw vertical grid lines and X axis ticks.
        for (int i = 0; i <= numXTicks; i++) {
            int x = (int) (PADDING + i * (width - 2 * PADDING) / numXTicks);
            g2.drawLine(x, PADDING, x, height - PADDING);
            double xValue = minX + i * (maxX - minX) / numXTicks;
            String label = String.format("%.1f", xValue);
            FontMetrics fm = g2.getFontMetrics();
            int labelWidth = fm.stringWidth(label);
            g2.setColor(Color.BLACK);
            g2.drawString(label, x - labelWidth / 2, height - PADDING + fm.getAscent() + 3);
            g2.setColor(Color.LIGHT_GRAY);
        }

        // Draw axes.
        g2.setColor(Color.BLACK);
        g2.drawLine(PADDING, height - PADDING, width - PADDING, height - PADDING);
        g2.drawLine(PADDING, PADDING, PADDING, height - PADDING);

        // ------------------ Draw Line Highlights & Markers ------------------

        for (HorizontalLine hLine : horizontalLines.values()) {
            int lineY = (int) (height - PADDING - (hLine.getYValue() - minY) * yScale);
            Color highlightColor = new Color(255, 0, 0, 20);

            if (hLine.getHighlightRegion() == HighlightRegion.AFTER) {
                g2.setColor(highlightColor);
                g2.fillRect(PADDING, PADDING, width - 2 * PADDING, lineY - PADDING);
            } else if (hLine.getHighlightRegion() == HighlightRegion.BEFORE) {
                g2.setColor(highlightColor);
                g2.fillRect(PADDING, lineY, width - 2 * PADDING, height - PADDING - lineY);
            }

            g2.setColor(Color.RED);
            g2.setStroke(new BasicStroke(2f));
            g2.drawLine(PADDING, lineY, width - PADDING, lineY);
        }

        for (VerticalLine vLine : verticalLines.values()) {
            int lineX = (int) (PADDING + (vLine.getXValue() - minX) * xScale);
            Color highlightColor = new Color(255, 0, 0, 20);

            if (vLine.getHighlightRegion() == HighlightRegion.BEFORE) {
                g2.setColor(highlightColor);
                g2.fillRect(PADDING, PADDING, lineX - PADDING, height - 2 * PADDING);
            } else if (vLine.getHighlightRegion() == HighlightRegion.AFTER) {
                g2.setColor(highlightColor);
                g2.fillRect(lineX, PADDING, (width - PADDING) - lineX, height - 2 * PADDING);
            }

            g2.setColor(Color.RED);
            g2.setStroke(new BasicStroke(2f));
            g2.drawLine(lineX, PADDING, lineX, height - PADDING);
        }

        // ------------------ Draw Graph Lines & Data Points ------------------

        // Draw lines connecting the data points.
        g2.setColor(Color.BLUE);
        g2.setStroke(new BasicStroke(2f));
        for (int i = 0; i < dataPoints.size() - 1; i++) {
            Point p1 = dataToScreen(dataPoints.get(i));
            Point p2 = dataToScreen(dataPoints.get(i + 1));
            g2.drawLine(p1.x, p1.y, p2.x, p2.y);
        }

        // Draw the data points.
        g2.setColor(Color.BLACK);
        for (Point2D dp : dataPoints) {
            Point screenPoint = dataToScreen(dp);
            g2.fill(new Ellipse2D.Double(screenPoint.x - POINT_RADIUS, screenPoint.y - POINT_RADIUS,
                POINT_RADIUS * 2, POINT_RADIUS * 2));
        }

        // Draw hover effect (if any).
        if (hoveredPoint != null) {
            g2.setColor(Color.ORANGE);
            g2.draw(new Ellipse2D.Double(hoveredPoint.x - POINT_RADIUS * 2, hoveredPoint.y - POINT_RADIUS * 2,
                POINT_RADIUS * 4, POINT_RADIUS * 4));
        }
    }
}
