package cz.mik0486.semestralprojekt.gen;

import cz.mik0486.semestralproject.data.holder.Matrix2D;
import cz.mik0486.semestralproject.data.holder.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MatrixTest {

    @ParameterizedTest
    @CsvSource({"16, 4, 4", "20, 4, 5", "1, 1, 1", "0, 0, 0", "1000000, 1000, 1000"})
    void calculateDimensions(int size, int rows, int cols) {
        Pair<Integer, Integer> dimensions = Matrix2D.calculateDimensions(size);

        assertEquals(rows, dimensions.first());
        assertEquals(cols, dimensions.second());
    }

    @Test
    void create() {
        Matrix2D<Double> matrix = new Matrix2D<>(2, 3, 0.0);
        matrix.setData(new Vector<>(List.of(
            1.0, 2.0, 3.0,
            4.0, 5.0
        )));

        assertEquals(2, matrix.getRows());
        assertEquals(3, matrix.getColumns());

        // Check the values
        assertEquals(1.0, matrix.getValue(0, 0));
        assertEquals(2.0, matrix.getValue(0, 1));
        assertEquals(3.0, matrix.getValue(0, 2));
        assertEquals(4.0, matrix.getValue(1, 0));
        assertEquals(5.0, matrix.getValue(1, 1));
        assertEquals(0.0, matrix.getValue(1, 2));
    }
}
