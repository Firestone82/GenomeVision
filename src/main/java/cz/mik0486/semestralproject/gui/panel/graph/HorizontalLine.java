package cz.mik0486.semestralproject.gui.panel.graph;

import lombok.Data;

@Data
public class HorizontalLine {
    private final String id;
    private double yValue;
    private HighlightRegion highlightRegion;

    public HorizontalLine(String id, double yValue, HighlightRegion highlightRegion) {
        this.id = id;
        this.yValue = yValue;
        this.highlightRegion = highlightRegion;
    }
}
