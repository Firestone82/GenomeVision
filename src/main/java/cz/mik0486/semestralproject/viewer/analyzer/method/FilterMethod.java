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

            if (inTolerance(originValue, targetValue, epsilon)) {
                return 0.f;
            }

            return originValue;
        });
    }

    private boolean inTolerance(float originValue, float targetValue, float epsilon) {
        if (originValue == targetValue) {
            return true;
        }

        float diff = Math.abs(targetValue - originValue);

        // Use the larger absolute value of the two as the reference
        float reference = Math.max(Math.abs(originValue), Math.abs(targetValue));
        float tolerance = reference * (epsilon / 100.f);

        return diff <= tolerance;
    }

    public abstract String getName();

    @Override
    public String toString() {
        return getName();
    }
}
