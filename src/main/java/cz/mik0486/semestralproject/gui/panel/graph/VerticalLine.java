package cz.mik0486.semestralproject.gui.panel.graph;

import lombok.Data;

@Data
public class VerticalLine {
    private final String id;
    private double xValue;
    private HighlightRegion highlightRegion;

    public VerticalLine(String id, double xValue, HighlightRegion highlightRegion) {
        this.id = id;
        this.xValue = xValue;
        this.highlightRegion = highlightRegion;
    }
}
