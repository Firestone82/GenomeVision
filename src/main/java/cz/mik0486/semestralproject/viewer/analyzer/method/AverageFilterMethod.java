package cz.mik0486.semestralproject.viewer.analyzer.method;

import cz.mik0486.semestralproject.data.holder.Matrix;
import cz.mik0486.semestralproject.data.holder.Sample;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
public class AverageFilterMethod extends FilterMethod {

    public Matrix process(List<Sample> samples) {
        if (samples.isEmpty()) {
            throw new IllegalArgumentException("Samples list cannot be empty");
        }

        List<Matrix> matrices = samples.stream()
            .map(Sample::getMatrix)
            .toList();

        int rows = matrices.getFirst().getRows();
        int columns = matrices.getFirst().getColumns();
        float defaultValue = matrices.getFirst().getDefaultValue();

        Matrix result = new Matrix(rows, columns, defaultValue);

        for (int i = 0; i < result.getRows(); i++) {
            for (int j = 0; j < result.getColumns(); j++) {
                float sum = 0;

                for (Matrix matrix : matrices) {
                    sum += matrix.getValue(i, j);
                }

                result.setValue(i, j, sum / matrices.size());
            }
        }

        return result;
    }

    @Override
    public String getName() {
        return "Average Filter";
    }
}
