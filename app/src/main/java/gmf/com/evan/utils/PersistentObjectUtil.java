package gmf.com.evan.utils;


import io.paperdb.Paper;

/**
 * Created by Evan on 16/7/5 下午4:18.
 */
public class PersistentObjectUtil {


    private static final String KEY_HAS_REQUEST_PERMISSION_BEFORE = "has_request_permission_before";

    private PersistentObjectUtil() {
    }

    public static void writeHasRequestPermissionBefore(boolean value) {
        Paper.book().write(KEY_HAS_REQUEST_PERMISSION_BEFORE, value);
    }

    public static boolean readHasRequestPermissionBefore() {
        return Paper.book().read(KEY_HAS_REQUEST_PERMISSION_BEFORE, false);
    }


}
