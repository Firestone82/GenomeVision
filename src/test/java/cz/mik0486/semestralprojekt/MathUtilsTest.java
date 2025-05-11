package cz.mik0486.semestralprojekt;

import cz.mik0486.semestralproject.utils.MathUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MathUtilsTest {

    @Test
    void roundRoundsIntegerCorrectly() {
        assertEquals(123, MathUtils.round(123, 2));
    }

    @Test
    void roundRoundsLongCorrectly() {
        assertEquals(1234567890123456789L, MathUtils.round(1234567890123456789L, 0));
    }

    @Test
    void roundRoundsFloatCorrectly() {
        assertEquals(123.46f, MathUtils.round(123.456f, 2));
    }

    @Test
    void roundRoundsDoubleCorrectly() {
        assertEquals(123.46, MathUtils.round(123.456, 2));
    }

    @Test
    void roundThrowsExceptionForNegativePlaces() {
        assertThrows(IllegalArgumentException.class, () -> MathUtils.round(123.456, -1));
    }

    @Test
    void roundThrowsExceptionForUnsupportedType() {
        assertThrows(IllegalArgumentException.class, () -> MathUtils.round((short) 123, 2));
    }

    @Test
    void roundHandlesZeroPlacesCorrectly() {
        assertEquals(123, MathUtils.round(123.456, 0));
    }

    @Test
    void roundHandlesZeroValueCorrectly() {
        assertEquals(0.0, MathUtils.round(0.0, 2));
    }
}
