package util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumbersUtil {
    public static BigDecimal toDp(BigDecimal value, int places) {
        if (value == null) {
            return null;
        }
        places = Math.max(places, 1);
        return value.setScale(places, RoundingMode.HALF_UP);
    }

    public static int compare(BigDecimal a, BigDecimal b) {
        if (a == null || b == null) {
            throw new IllegalArgumentException("Provide correct input");
        }
        return a.compareTo(b);
    }
}
