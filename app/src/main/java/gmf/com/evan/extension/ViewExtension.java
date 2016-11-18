package gmf.com.evan.extension;

import android.app.Activity;
import android.app.Dialog;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toolbar;

import gmf.com.evan.R;
import gmf.com.evan.MyApplication;
import gmf.com.evan.utils.DimensionConverter;
import rx.functions.Action1;

import static gmf.com.evan.extension.ObjectExtension.safeCall;

/**
 * Created by Evan on 16/6/29 下午7:22.
 */
public class ViewExtension {

    private ViewExtension() {
    }


    public static Toolbar findToolbar(Activity activity) {
        return findToolbar(activity.getWindow().getDecorView());
    }

    public static Toolbar findToolbar(Fragment fragment) {
        return findToolbar(fragment.getView());
    }

    public static Toolbar findToolbar(View view) {
        return (Toolbar) view.findViewById(R.id.toolbar);
    }


    public static int dp2px(View view, float dp) {
        return (int) DimensionConverter.dp2px(view.getContext(), dp);
    }

    public static int dp2px(float dp) {
        return (int) DimensionConverter.dp2px(MyApplication.SHARE_INSTANCE, dp);
    }

    public static int px2dp(View view, float px) {
        return (int) DimensionConverter.px2dp(view.getContext(), px);
    }

    public static int px2dp(float px) {
        return (int) DimensionConverter.px2dp(MyApplication.SHARE_INSTANCE, px);
    }

    public static int sp2px(View view, float sp) {
        return (int) DimensionConverter.sp2px(view.getContext(), sp);
    }

    public static int sp2px(float sp) {
        return (int) DimensionConverter.sp2px(MyApplication.SHARE_INSTANCE, sp);
    }


    public static <T extends View> T v_findView(Fragment fragment, @IdRes int viewId) {
        return (T) fragment.getView().findViewById(viewId);
    }

    public static <T extends View> T v_findView(Dialog dialog, @IdRes int viewId) {
        return (T) dialog.getWindow().getDecorView().findViewById(viewId);
    }

    public static <T extends View> T v_findView(Activity activity, @IdRes int viewId) {
        return (T) activity.getWindow().getDecorView().findViewById(viewId);
    }

    public static <T extends View> T v_findView(View parent, @IdRes int viewId) {
        return (T) parent.findViewById(viewId);
    }

    public static void v_setClick(View view, View.OnClickListener listener) {
        view.setOnClickListener(v -> safeCall(() -> {
            listener.onClick(v);
        }));
    }

    public static void v_setText(Activity activity, @IdRes int textViewId, CharSequence text) {
        v_setText(activity.getWindow().getDecorView(), textViewId, text);
    }

    public static void v_setText(TextView textView, CharSequence text) {
        textView.setText(text);
    }

    public static void v_setText(View parent, @IdRes int textViewId, CharSequence text) {
        TextView textView = (TextView) parent.findViewById(textViewId);
        textView.setText(text);
    }

    public static void v_preDraw(final View view, final boolean autoRelease, final Action1<View> callback) {
        view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (autoRelease)
                    view.getViewTreeObserver().removeOnPreDrawListener(this);
                callback.call(view);
                return true;
            }
        });
    }

    public static void v_globalLayout(final View view, final boolean autoRelease, final Action1<View> callback) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (autoRelease)
                    view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                callback.call(view);
            }
        });
    }

    public static void v_setOnFreshListener(SwipeRefreshLayout refreshLayout, SwipeRefreshLayout.OnRefreshListener listener) {
        refreshLayout.setOnRefreshListener(() -> {
            listener.onRefresh();
        });
    }

    public static void v_setOnFreshListener(SwipeRefreshLayout refreshLayout, Action1<SwipeRefreshLayout> callback) {
        refreshLayout.setOnRefreshListener(() -> {
            callback.call(refreshLayout);
        });
    }

    public static void v_setVisibility(View finder, int viewId, int visibility) {
        finder.findViewById(viewId).setVisibility(visibility);
    }

    public static void v_setVisibility(Dialog finder, int viewId, int visibility) {
        finder.findViewById(viewId).setVisibility(visibility);
    }

    public static void v_setVisibility(Activity finder, int viewId, int visibility) {
        finder.findViewById(viewId).setVisibility(visibility);
    }

    public static void v_setVisibility(Fragment fragment, int viewId, int visibility) {
        fragment.getView().findViewById(viewId).setVisibility(visibility);
    }

    public static void v_setVisibility(View view, int visibility) {
        view.setVisibility(visibility);
    }

    public static boolean v_isVisible(View parent, @IdRes int viewId) {
        return parent.findViewById(viewId).getVisibility() == View.VISIBLE;
    }

    public static boolean v_isVisible(View view) {
        return view.getVisibility() == View.VISIBLE;
    }

    public static void v_setVisible(View finder, int viewId) {
        finder.findViewById(viewId).setVisibility(View.VISIBLE);
    }

    public static void v_setVisible(Activity finder, int viewId) {
        finder.findViewById(viewId).setVisibility(View.VISIBLE);
    }

    public static void v_setVisible(Fragment fragment, int viewId) {
        fragment.getView().findViewById(viewId).setVisibility(View.VISIBLE);
    }

    public static void v_setVisible(View view) {
        view.setVisibility(View.VISIBLE);
    }

    public static void v_setInvisible(View finder, int viewId) {
        finder.findViewById(viewId).setVisibility(View.INVISIBLE);
    }

    public static void v_setInvisible(Activity finder, int viewId) {
        finder.findViewById(viewId).setVisibility(View.INVISIBLE);
    }

    public static void v_setInvisible(Fragment fragment, int viewId) {
        fragment.getView().findViewById(viewId).setVisibility(View.INVISIBLE);
    }

    public static void v_setInvisible(View view) {
        view.setVisibility(View.INVISIBLE);
    }

    public static void v_setGone(View parent, int viewId) {
        parent.findViewById(viewId).setVisibility(View.GONE);
    }

    public static void v_setGone(Activity finder, int viewId) {
        finder.findViewById(viewId).setVisibility(View.GONE);
    }

    public static void v_setGone(Fragment fragment, int viewId) {
        fragment.getView().findViewById(viewId).setVisibility(View.GONE);
    }

    public static void v_setGone(View view) {
        view.setVisibility(View.GONE);
    }
}
