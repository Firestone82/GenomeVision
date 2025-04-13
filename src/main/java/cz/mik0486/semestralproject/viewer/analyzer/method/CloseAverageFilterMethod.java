package cz.mik0486.semestralproject.viewer.analyzer.method;

import cz.mik0486.semestralproject.data.holder.Matrix;
import cz.mik0486.semestralproject.data.holder.Sample;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
public class CloseAverageFilterMethod extends AverageFilterMethod {

    @Override
    public Matrix process(List<Sample> samples) {
        Matrix averageMatrix = super.process(samples);

        List<Matrix> matrices = samples.stream()
            .map(Sample::getMatrix)
            .toList();

        Matrix bestMatrix = null;
        double bestDiff = Double.MAX_VALUE;

        for (Matrix matrix : matrices) {
            double diff = matrixDifference(matrix, averageMatrix);

            if (diff < bestDiff) {
                bestDiff = diff;
                bestMatrix = matrix;
            }
        }

        return bestMatrix;
    }

    private double matrixDifference(Matrix m1, Matrix m2) {
        double sum = 0.0;
        int rows = m1.getRows();
        int cols = m1.getColumns();

        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                sum += Math.abs(m1.getValue(x, y) - m2.getValue(x, y));
            }
        }

        return sum;
    }

    @Override
    public String getName() {
        return "Close Average Filter";
    }
}
