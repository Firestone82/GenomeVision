package cz.mik0486.semestralproject.viewer.analyzer.method;

import cz.mik0486.semestralproject.data.holder.Matrix;
import cz.mik0486.semestralproject.data.holder.Sample;
import cz.mik0486.semestralproject.gui.selector.range.Range;
import lombok.Data;

import java.util.List;

@Data
public abstract class FilterMethod {

    public abstract Matrix process(List<Sample> samples);

    public Matrix calculate(List<Sample> originSamples, List<Sample> targetSamples, Range originBoundary, Range targetBoundary, float epsilon) {
        Matrix originMatrix = process(originSamples);
        originMatrix.applyBoundary(originBoundary);

        Matrix targetMatrix = process(targetSamples);
        targetMatrix.applyBoundary(targetBoundary);

        int rows = originMatrix.getRows();
        int columns = originMatrix.getColumns();
        float defaultValue = originMatrix.getDefaultValue();

        return new Matrix(rows, columns, defaultValue, (x, y, currentValue) -> {
            float originValue = originMatrix.getValue(y, x);
            float targetValue = targetMatrix.getValue(y, x);
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
            if (percentDifference <= epsilon) {
                return 0.0f;
            } else {
                // Otherwise, the values are different enough; we interpolate (here using average).
//                return (originValue + targetValue) / 2.0f;
                return originValue;
            }
        });
    }

    public abstract String getName();

    @Override
    public String toString() {
        return getName();
    }
}
