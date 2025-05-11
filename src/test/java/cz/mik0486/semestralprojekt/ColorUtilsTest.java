package cz.mik0486.semestralprojekt;

import cz.mik0486.semestralproject.utils.ColorUtils;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ColorUtilsTest {

    @Test
    void toHexConvertsBlackColor() {
        assertEquals("#000000", ColorUtils.toHex(new Color(0, 0, 0)));
    }

    @Test
    void toHexConvertsWhiteColor() {
        assertEquals("#FFFFFF", ColorUtils.toHex(new Color(255, 255, 255)));
    }

    @Test
    void toHexConvertsRandomColor() {
        assertEquals("#1A2B3C", ColorUtils.toHex(new Color(26, 43, 60)));
    }

    @Test
    void parseHexParsesBlackColor() {
        assertEquals(new Color(0, 0, 0), ColorUtils.parseHex("#000000"));
    }

    @Test
    void parseHexParsesWhiteColor() {
        assertEquals(new Color(255, 255, 255), ColorUtils.parseHex("#FFFFFF"));
    }

    @Test
    void parseHexParsesRandomColor() {
        assertEquals(new Color(26, 43, 60), ColorUtils.parseHex("#1A2B3C"));
    }

    @Test
    void parseHexHandlesHexWithoutHash() {
        assertEquals(new Color(26, 43, 60), ColorUtils.parseHex("1A2B3C"));
    }

    @Test
    void parseHexThrowsExceptionForInvalidHex() {
        assertThrows(IllegalArgumentException.class, () -> ColorUtils.parseHex("ZZZZZZ"));
    }

    @Test
    void parseHexThrowsExceptionForShortHex() {
        assertThrows(IllegalArgumentException.class, () -> ColorUtils.parseHex("#1O3"));
    }
}
