package cz.mik0486.semestralproject.viewer.analyzer.method;

import cz.mik0486.semestralproject.data.holder.Matrix;
import cz.mik0486.semestralproject.viewer.analyzer.gui.ScanViewer;
import lombok.Data;

@Data
public abstract class FilterMethod {
    protected final String name;

    public abstract Matrix process(ScanViewer viewer);

    @Override
    public String toString() {
        return name;
    }
}
