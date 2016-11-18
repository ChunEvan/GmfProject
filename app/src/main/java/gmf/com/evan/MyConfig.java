package gmf.com.evan;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Pair;

/**
 * Created by Evan on 16/9/23 上午10:57.
 */

public class MyConfig {


    private MyConfig() {
    }

    public static final boolean IS_DEBUG_MODE = BuildConfig.DEBUG;

    public static final Pair<String, String> DEBUG1_HOST_NAME = Pair.create("http://192.168.0.21:32080", "http://192.168.0.21:34080");
    public static final Pair<String, String> DEBUG2_HOST_NAME = Pair.create("http://192.168.0.21:42081", "http://192.168.0.21:34080");
    public static final Pair<String, String> DEBUG3_HOST_NAME = Pair.create("http://192.168.0.21:29080", "http://192.168.0.21:34080");
    public static final Pair<String, String> PRE_PUBLIC_HOST_NAME = Pair.create("http://pub.goldmf.com", "http://sns-pub.goldmf.com:81");
    public static final Pair<String, String> PUBLIC_HOST_NAME = Pair.create("https://www.caopanman.com", "http://sns.caopanman.com");
    public static final Pair<String, String> RANGO_HOST_NAME = Pair.create("http://192.168.0.21:26080", "http://192.168.0.21:26081");
    public static final Pair<String, String> CONSIS_HOST_NAME = Pair.create("http://192.168.0.21:25080", "http://192.168.0.21:25080");
    public static final Pair<String, String> AUSTIN_HOST_NAME = Pair.create("http://192.168.0.21:24080", "http://192.168.0.21:24081");
    public static final Pair<String, String> LAIR_HOST_NAME = Pair.create("http://192.168.0.21:29080", "http://192.168.0.21:29081");
    public static final Pair<String, String> BUTY_HOST_NAME = Pair.create("http://192.168.0.21:22080", "http://192.168.0.21:22082");
    public static final Pair<String, String> BUTY_REAL_NAME = Pair.create("http://192.168.0.20:32080", "http://192.168.0.21:34080");


    public static final int DEFAULT_HOST_INDEX = IS_DEBUG_MODE ? 10 : 3;
    public static Pair<String, String> CURRENT_HOST_NAME = getCurrentHostName();
    public static int CURRENT_HOST_NAME_IDX = getCurrentHostNameIdx();

    public static final String channel_id = getChannelId();

    private static String getChannelId() {
        Context ctx = MyApplication.SHARE_INSTANCE;
        try {
            PackageInfo info = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
            if (info.applicationInfo.metaData != null) {
                return info.applicationInfo.metaData.getString("GMF_CHANNEL_ID");
            }
        } catch (PackageManager.NameNotFoundException e) {

        }
        return "unknown";
    }

    public static void setCurrentHostName(int hostIndex) {
        if (hostIndex >= 1 && hostIndex <= 10) {
            getDevConfig().edit().putInt("current_host_index", hostIndex);
            CURRENT_HOST_NAME_IDX = hostIndex;
            CURRENT_HOST_NAME = getCurrentHostName();
        }
    }

    private static int getCurrentHostNameIdx() {
        return getDevConfig().getInt("current_host_index", DEFAULT_HOST_INDEX);
    }

    public static Pair<String, String> getCurrentHostName() {
        int index = getCurrentHostNameIdx();
        switch (index) {
            case 1:
                return DEBUG1_HOST_NAME;
            case 2:
                return PRE_PUBLIC_HOST_NAME;
            case 3:
                return PUBLIC_HOST_NAME;
            case 4:
                return RANGO_HOST_NAME;
            case 5:
                return CONSIS_HOST_NAME;
            case 6:
                return AUSTIN_HOST_NAME;
            case 7:
                return LAIR_HOST_NAME;
            case 8:
                return BUTY_HOST_NAME;
            case 9:
                return DEBUG2_HOST_NAME;
            case 10:
                return DEBUG3_HOST_NAME;
        }
        return PUBLIC_HOST_NAME;
    }

    private static SharedPreferences getDevConfig() {
        return MyApplication.SHARE_INSTANCE.getSharedPreferences("dev.config", Context.MODE_PRIVATE);
    }

    public static void setDevModeEnable(boolean isEnable) {
        getDevConfig().edit().putBoolean("is_dev_mode_enable", isEnable).commit();
    }

    public static boolean isDevModeEnable() {
        return getDevConfig().getBoolean("is_dev_mode_enable", MyConfig.IS_DEBUG_MODE);
    }


}
