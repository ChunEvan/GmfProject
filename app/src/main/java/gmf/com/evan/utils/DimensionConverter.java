package gmf.com.evan.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

/**
 * Created by Evan on 16/6/30 上午10:47.
 */
public class DimensionConverter {

    public DimensionConverter() {
    }

    public static float dp2px(Context context, float dp) {
        Resources resources = context.getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
    }

    public static float sp2px(Context context, float sp) {
        Resources resources = context.getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, resources.getDisplayMetrics());
    }

    public static float px2dp(Context context, float px) {
        Resources resources = context.getResources();
        return px / resources.getDisplayMetrics().density;
    }


}
