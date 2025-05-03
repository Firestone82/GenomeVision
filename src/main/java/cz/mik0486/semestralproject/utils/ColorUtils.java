package cz.mik0486.semestralproject.utils;

import java.awt.*;

public class ColorUtils {

    /**
     * Convert a Color to "#RRGGBB"
     */
    public static String toHex(Color c) {
        return String.format("#%02X%02X%02X", c.getRed(), c.getGreen(), c.getBlue());
    }

    /**
     * Parse "#RRGGBB" (or "RRGGBB") into a Color
     */
    public static Color parseHex(String hex) {
        String h = hex.startsWith("#") ? hex.substring(1) : hex;
        int rgb = Integer.parseInt(h, 16);
        return new Color((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF);
    }
}
