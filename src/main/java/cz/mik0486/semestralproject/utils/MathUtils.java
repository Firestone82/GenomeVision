package cz.mik0486.semestralproject.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathUtils {

    @SuppressWarnings("unchecked")
    public static <T extends Number> T round(T value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException("places must be non-negative");
        }

        BigDecimal bd = new BigDecimal(value.toString());
        bd = bd.setScale(places, RoundingMode.HALF_UP);

        return switch (value) {
            case Integer i -> (T) Integer.valueOf(bd.intValue());
            case Long l -> (T) Long.valueOf(bd.longValue());
            case Float f -> (T) Float.valueOf(bd.floatValue());
            case Double d -> (T) Double.valueOf(bd.doubleValue());
            default -> throw new IllegalArgumentException("Type not supported: " + value.getClass());
        };
    }
}
