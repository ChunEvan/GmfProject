package gmf.com.evan.extension;

import android.content.Context;
import android.content.res.Resources;

import gmf.com.evan.R;


/**
 * Created by Evan on 16/7/4 下午7:40.
 */
public class SignalColorHolder {

    public static int YELLOW_COLOR;
    public static int WHITE_COLOR;
    public static int STATUS_BAR_BLACK = 0;

    public SignalColorHolder() {
    }

    public static void init(Context context) {
        Resources res = context.getResources();
        YELLOW_COLOR = context.getResources().getColor(R.color.gmf_yellow);
        WHITE_COLOR = context.getResources().getColor(R.color.gmf_white);
        STATUS_BAR_BLACK = res.getColor(R.color.gmf_status_bar_black);
    }

}
