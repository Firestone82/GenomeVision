package cz.mik0486.semestralprojekt;

import cz.mik0486.semestralproject.utils.StringUtils;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StringUtilsTest {

    @Test
    void calculateBytesHandlesEmptyString() {
        assertEquals(System.lineSeparator().getBytes(StandardCharsets.UTF_8).length, StringUtils.calculateBytes(""));
    }

    @Test
    void calculateBytesHandlesSingleCharacter() {
        assertEquals(1 + System.lineSeparator().getBytes(StandardCharsets.UTF_8).length, StringUtils.calculateBytes("a"));
    }

    @Test
    void calculateBytesHandlesMultilineString() {
        String line = "Hello\nWorld";
        int expectedBytes = line.getBytes(StandardCharsets.UTF_8).length + System.lineSeparator().getBytes(StandardCharsets.UTF_8).length;
        assertEquals(expectedBytes, StringUtils.calculateBytes(line));
    }

    @Test
    void calculateBytesHandlesUnicodeCharacters() {
        String line = "こんにちは";
        int expectedBytes = line.getBytes(StandardCharsets.UTF_8).length + System.lineSeparator().getBytes(StandardCharsets.UTF_8).length;
        assertEquals(expectedBytes, StringUtils.calculateBytes(line));
    }

    @Test
    void calculateBytesHandlesNullString() {
        assertThrows(NullPointerException.class, () -> StringUtils.calculateBytes(null));
    }
}
