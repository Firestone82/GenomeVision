package cz.mik0486.semestralproject.data.holder;

import cz.mik0486.semestralproject.gui.selector.range.Range;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.function.Function;

@Data
public class Matrix {
    private final int rows;
    private final int columns;
    private final float defaultValue;
    private final List<Float> data;

    public Matrix(int rows, int columns, float defaultValue) {
        this.rows = rows;
        this.columns = columns;
        this.defaultValue = defaultValue;

        this.data = new ArrayList<>(rows * columns);
        this.data.addAll(Collections.nCopies(rows * columns, defaultValue));
    }

    public Matrix(int rows, int columns, float defaultValue, MatrixElementFunction func) {
        this.rows = rows;
        this.columns = columns;
        this.defaultValue = defaultValue;

        this.data = new ArrayList<>(rows * columns);
        this.data.addAll(Collections.nCopies(rows * columns, defaultValue));

        compute(func);
    }

    public void setData(Vector<Float> data) {
        this.data.clear();
        this.data.addAll(data);
    }

    public void setValue(int row, int column, float value) {
        int index = row * columns + column;

        if (index >= data.size()) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }

        data.set(index, value);
    }

    public float getValue(int row, int column) {
        int index = row * columns + column;

        if (index >= data.size()) {
            return defaultValue;
        }

        return data.get(index);
    }

    private boolean indexOutOfBounds(int row, int column) {
        int index = row * columns + column;
        return index >= data.size();
    }

    public long size() {
        return data.size();
    }

    public void compute(Function<Float, Float> func) {
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.columns; j++) {
                if (indexOutOfBounds(i, j)) {
                    continue;
                }

                float value = func.apply(getValue(i, j));
                setValue(i, j, value);
            }
        }
    }

    public void compute(MatrixElementFunction func) {
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.columns; j++) {
                if (indexOutOfBounds(i, j)) {
                    continue;
                }

                float value = func.apply(i, j, getValue(i, j));
                setValue(i, j, value);
            }
        }
    }

    public void applyBoundary(Range boundary) {
        this.compute((x, y, value) -> {
            if (value < (boundary.lower() / 100.f)) {
                return 0.0f;
            } else if (value > (boundary.upper() / 100.f)) {
                return 1.0f;
            }

            return value;
        });
    }

    public static Pair<Integer, Integer> calculateDimensions(int size) {
        int rows = (int) Math.floor(Math.sqrt(size));
        int cols = (int) Math.ceil(Math.sqrt(size));

        if (rows * cols < size) {
            rows = cols;
        }

        return new Pair<>(rows, cols);
    }

    public static Matrix createWith(Vector<Float> data, float defaultValue) {
        Pair<Integer, Integer> dimensions = calculateDimensions(data.size());

        Matrix matrix = new Matrix(dimensions.first(), dimensions.second(), defaultValue);
        matrix.setData(data);

        return matrix;
    }

    @FunctionalInterface
    public interface MatrixElementFunction {
        float apply(int row, int col, float value);
    }
}


