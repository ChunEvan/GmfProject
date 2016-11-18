package gmf.com.evan.extension;

/**
 * Created by Evan on 16/6/24 上午11:52.
 */
public class PreCondition {

    private PreCondition() {

    }

    public static void checkNotNull(Object... args) {
        for (Object arg : args) {
            if (arg == null)
                throw new AssertionError("argument must not be null");
        }
    }
}
