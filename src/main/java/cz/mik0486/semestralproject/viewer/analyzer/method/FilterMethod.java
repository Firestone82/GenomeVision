package cz.mik0486.semestralproject.viewer.analyzer.method;

import cz.mik0486.semestralproject.data.holder.Matrix;
import cz.mik0486.semestralproject.data.holder.Sample;
import lombok.Data;

import java.util.List;

@Data
public abstract class FilterMethod {

    public abstract Matrix process(List<Sample> samples);

    public Matrix calculate(List<Sample> originSamples, List<Sample> targetSamples, float epsilon) {
        Matrix originMatrix = process(originSamples);
        Matrix targetMatrix = process(targetSamples);

        int rows = originMatrix.getRows();
        int columns = originMatrix.getColumns();

        return new Matrix(rows, columns, 0.0f, (x, y, currentValue) -> {
            float originValue = originMatrix.getValue(x, y);
            float targetValue = targetMatrix.getValue(x, y);
            float diff = Math.abs(originValue - targetValue);

            // Guard against division by zero by using the maximum of the absolute values.
            float maxValue = Math.max(Math.abs(originValue), Math.abs(targetValue));

            // If both values are 0, they are the same; we return 0.
            if (maxValue == 0.0f) {
                return 0.0f;
            }

            // Calculate the relative difference in percentage.
            float percentDifference = (diff / maxValue) * 100.0f;

            // When the values are within the tolerance margin, ignore them by setting the result to 0.
            if (percentDifference <= 100 - epsilon) {
                return 0.0f;
            } else {
                // Otherwise, the values are different enough; we interpolate (here using average).
                return (originValue + targetValue) / 2.0f;
            }
        });
    }

    public abstract String getName();

    @Override
    public String toString() {
        return getName();
    }
}
