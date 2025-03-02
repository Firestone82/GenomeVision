package cz.mik0486.semestralproject.utils;

import java.nio.charset.StandardCharsets;

public class StringUtils {

    public static int calculateBytes(String line) {
        return line.getBytes(StandardCharsets.UTF_8).length + System.lineSeparator().getBytes(StandardCharsets.UTF_8).length;
    }
}
