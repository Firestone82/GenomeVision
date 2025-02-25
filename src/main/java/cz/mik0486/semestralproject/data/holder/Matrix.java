package cz.mik0486.semestralproject.data.holder;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

@Data
public class Matrix {

    private final int rows;
    private final int columns;
    private final List<Float> data;
    private final float defaultValue;

    public Matrix(int rows, int columns, float defaultValue) {
        this.rows = rows;
        this.columns = columns;
        this.defaultValue = defaultValue;

        this.data = new ArrayList<>(rows * columns);
        this.data.addAll(Collections.nCopies(rows * columns, defaultValue));
    }

    public void setData(Vector<Float> data) {
        this.data.clear();
        this.data.addAll(data);
    }

    public void setValue(int row, int column, float value) {
        data.set(row * columns + column, value);
    }

    public float getValue(int row, int column) {
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

    public static Matrix createBySize(int size, float defaultValue) {
        Pair<Integer, Integer> dimensions = calculateDimensions(size);
        return new Matrix(dimensions.first(), dimensions.second(), defaultValue);
    }
}
