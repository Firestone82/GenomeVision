package cz.mik0486.semestralproject.data.holder;

import lombok.Data;

import java.util.Collections;
import java.util.Vector;

@Data
public class Matrix2D<T> {

    private final int rows;
    private final int columns;
    private final Vector<T> data;
    private final T defaultValue;

    public Matrix2D(int rows, int columns, T defaultValue) {
        this.rows = rows;
        this.columns = columns;
        this.defaultValue = defaultValue;

        this.data = new Vector<>(rows * columns);
        this.data.addAll(Collections.nCopies(rows * columns, defaultValue));
    }

    public void setData(Vector<T> data) {
        this.data.clear();
        this.data.addAll(data);
    }

    public void setValue(int row, int column, T value) {
        data.set(row * columns + column, value);
    }

    public T getValue(int row, int column) {
        int index = row * columns + column;

        if (index >= data.size()) {
            return defaultValue;
        }

        return data.get(index);
    }

    public long size() {
        return data.size();
    }

    public static Pair<Integer, Integer> calculateDimensions(int size) {
        int rows = (int) Math.floor(Math.sqrt(size));
        int cols = (int) Math.ceil(Math.sqrt(size));

        if (rows * cols < size) {
            rows = cols;
        }

        return new Pair<>(rows, cols);
    }

    public static <T> Matrix2D<T> createBySize(int size, T defaultValue) {
        Pair<Integer, Integer> dimensions = calculateDimensions(size);
        return new Matrix2D<>(dimensions.first(), dimensions.second(), defaultValue);
    }
}
