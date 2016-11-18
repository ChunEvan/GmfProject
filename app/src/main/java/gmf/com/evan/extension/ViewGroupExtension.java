package gmf.com.evan.extension;

import android.view.View;
import android.view.ViewGroup;

import rx.functions.Action2;

/**
 * Created by Evan on 16/7/14 下午8:44.
 */
public class ViewGroupExtension {


    public static void v_forEach(ViewGroup viewGroup, Action2<Integer, View> operation) {
        int count = viewGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            operation.call(i, viewGroup.getChildAt(i));
        }
    }
}
