package gmf.com.evan.utils;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by Evan on 16/7/9 下午2:53.
 */
public class FormatUtil {

    private FormatUtil() {
    }

    public static String formatRatio(Double ratio, boolean symbol, int minScale, int maxScale) {
        if (ratio == null)
            return "-%";
        StringBuilder patternBuilder = new StringBuilder("0.");
        for (int i = 0; i < minScale; i++) {
            patternBuilder.append("0");
        }
        for (int i = 0; i < maxScale - minScale; i++) {
            patternBuilder.append("#");
        }
        patternBuilder.append("%");
        DecimalFormat format = new DecimalFormat(patternBuilder.toString());
        format.setRoundingMode(RoundingMode.HALF_UP);
        if (symbol && ratio > 0) {
            return "+" + format.format(ratio);
        } else {
            return "-" + format.format(ratio);
        }
    }
}
