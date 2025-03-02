package cz.mik0486.semestralprojekt.gen;

import cz.mik0486.semestralproject.data.holder.Matrix;
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
        Pair<Integer, Integer> dimensions = Matrix.calculateDimensions(size);

        assertEquals(rows, dimensions.first());
        assertEquals(cols, dimensions.second());
    }

    @Test
    void create() {
        Matrix matrix = new Matrix(2, 3, 0.0f);
        matrix.setData(new Vector<>(List.of(
            1.0f, 2.0f, 3.0f,
            4.0f, 5.0f
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
