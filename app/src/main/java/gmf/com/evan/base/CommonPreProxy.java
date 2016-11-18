package gmf.com.evan.base;

import android.content.Context;
import android.content.SharedPreferences;

import gmf.com.evan.MyApplication;
import gmf.com.evan.utils.AppUtil;


/**
 * Created by Evan on 16/7/6 下午7:33.
 */
public class CommonPreProxy {

    private static final String KEY_LAST_LAUNCH_VERSION_CODE_INT = "last_launch_version_code";

    private static SharedPreferences sPref;

    static {
        sPref = MyApplication.SHARE_INSTANCE.getSharedPreferences("common", Context.MODE_APPEND);
    }

    private CommonPreProxy() {
    }

    public static void updateLastLaunchVersionCode() {
        Context context = MyApplication.SHARE_INSTANCE;
        sPref.edit().putInt(KEY_LAST_LAUNCH_VERSION_CODE_INT, AppUtil.getVersionCode(context));
    }

    public static int getLastLaunchVersionCode() {
        return sPref.getInt(KEY_LAST_LAUNCH_VERSION_CODE_INT, -1);
    }


}
