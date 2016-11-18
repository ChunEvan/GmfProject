package gmf.com.evan.extension;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;

import static gmf.com.evan.extension.ObjectExtension.opt;


/**
 * Created by Evan on 16/7/19 上午11:40.
 */
public class SpannableStringExtension {

    private SpannableStringExtension() {
    }

    public static SpannableStringBuilder concat(CharSequence first, CharSequence... others) {
        first = opt(first).or("");
        if (first instanceof SpannableStringBuilder) {
            return concat(first, others);
        }
        return concat((SpannableStringBuilder) (first), others);
    }

    public static SpannableStringBuilder concat(SpannableStringBuilder first, CharSequence... others) {
        for (CharSequence other : others) {
            if (other != null)
                first.append("\n").append(other);
        }
        return first;
    }

    public static SpannableStringBuilder concatNoBreak(CharSequence first, CharSequence... others) {
        first = opt(first).or("");
        if (first instanceof SpannableStringBuilder) {
            return concatNoBreak(first, others);
        }
        return concatNoBreak((SpannableStringBuilder) (first), others);
    }

    public static SpannableStringBuilder concatNoBreak(SpannableStringBuilder first, CharSequence... others) {
        for (CharSequence other : others) {
            if (other != null) {
                first.append(other);
            }
        }
        return first;
    }

    public static SpannableStringBuilder setFontSize(CharSequence cs, int fontSizePx) {
        cs = opt(cs).or("");
        if (cs instanceof SpannableStringBuilder) {
            return setFontSize(cs, fontSizePx);
        }
        return setFontSize((SpannableStringBuilder) (cs), fontSizePx);
    }

    public static SpannableStringBuilder setFontSize(SpannableStringBuilder ssb, int fontSizePx) {
        ssb = opt(ssb).or(new SpannableStringBuilder());
        final int start = 0;
        final int end = ssb.length();
        if (end > start) {
            ssb.setSpan(new AbsoluteSizeSpan(fontSizePx), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return ssb;
    }

}
