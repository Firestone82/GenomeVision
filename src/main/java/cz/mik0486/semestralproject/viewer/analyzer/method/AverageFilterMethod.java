package cz.mik0486.semestralproject.viewer.analyzer.method;

import cz.mik0486.semestralproject.data.holder.Matrix;
import cz.mik0486.semestralproject.data.holder.Sample;
import cz.mik0486.semestralproject.viewer.analyzer.gui.ScanViewer;

public class AverageFilterMethod extends FilterMethod {

    public AverageFilterMethod() {
        super("Average");
    }

    public Matrix process(ScanViewer viewer) {
        Matrix originAverageMatrix = Matrix.average(
            viewer.getOriginSamples().stream().map(Sample::getMatrix).toList()
        );

        Matrix targetAverageMatrix = Matrix.average(
            viewer.getTargetSamples().stream().map(Sample::getMatrix).toList()
        );

        float minEps = viewer.getEpsilon().lower() / 100.f;
        float maxEps = viewer.getEpsilon().upper() / 100.f;

        int rows = originAverageMatrix.getRows();
        int columns = originAverageMatrix.getColumns();

        return new Matrix(rows, columns, 0.0f, (x, y, currentValue) -> {
            float originValue = originAverageMatrix.getValue(x, y);
            float targetValue = targetAverageMatrix.getValue(x, y);
            float diff = Math.abs(originValue - targetValue);

            if (diff < minEps) {
                // When difference is below lower epsilon, set output to 0.
                return 0.0f;
            } else if (diff >= minEps && diff < maxEps) {
                // When the difference is within the epsilon range, return the average value
                return (originValue + targetValue) / 2.0f;
            } else {
                // When the difference is above the upper epsilon, set output to 1.
                return 1.0f;
            }
        });
    }
}
