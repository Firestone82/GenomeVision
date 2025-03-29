package cz.mik0486.semestralproject.viewer.analyzer.method;

import cz.mik0486.semestralproject.data.holder.Matrix;
import cz.mik0486.semestralproject.data.holder.Sample;
import cz.mik0486.semestralproject.viewer.analyzer.gui.ScanViewer;

public class CloseAverageFilterMethod extends FilterMethod {

    public CloseAverageFilterMethod() {
        super("CloseAverage");
    }

    public Matrix process(ScanViewer viewer) {
        Matrix originAverageClosestMatrix = Matrix.closestToAverage(
            viewer.getOriginSamples().stream().map(Sample::getMatrix).toList()
        );

        Matrix targetAverageClosestMatrix = Matrix.closestToAverage(
            viewer.getTargetSamples().stream().map(Sample::getMatrix).toList()
        );

        float minEps = viewer.getEpsilon().lower() / 100.f;
        float maxEps = viewer.getEpsilon().upper() / 100.f;

        int rows = originAverageClosestMatrix.getRows();
        int columns = originAverageClosestMatrix.getColumns();

        return new Matrix(rows, columns, 0.0f, (x, y, currentValue) -> {
            float originValue = originAverageClosestMatrix.getValue(x, y);
            float targetValue = targetAverageClosestMatrix.getValue(x, y);
            float diff = Math.abs(originValue - targetValue);

            if (diff < minEps) {
                // When difference is below lower epsilon, set output to 0.
                return 0.0f;
            } else if (diff >= minEps && diff < maxEps) {
                // When in the middle range, choose the origin sample value.
                return originValue;
            } else {
                // When above the upper epsilon, choose the target sample value.
                return targetValue;
            }
        });
    }
}
