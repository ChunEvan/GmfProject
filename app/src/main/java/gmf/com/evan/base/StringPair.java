package gmf.com.evan.base;

import android.util.Pair;

import static gmf.com.evan.extension.ObjectExtension.opt;

/**
 * Created by Evan on 16/7/19 下午9:09.
 */
public class StringPair extends Pair<String, String> {
    /**
     * Constructor for a Pair.
     *
     * @param first  the first object in the Pair
     * @param second the second object in the pair
     */
    public StringPair(String first, String second) {
        super(first, second);
    }

    public static StringPair create(String first, String second) {
        return new StringPair(first, second);
    }

    @Override
    public String toString() {
        return opt(first).or("") + opt(second).or("");
    }
}
