package gmf.com.evan.extension;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import gmf.com.evan.R;
import gmf.com.evan.MyApplication;
import gmf.com.evan.controller.activity.BaseActivity;
import gmf.com.evan.controller.dialog.GMFDialog;


/**
 * Created by Evan on 16/6/30 上午10:36.
 */
public class UIControllerExtension {

    private static Handler sHandler = new Handler(Looper.getMainLooper());

    public static void runOnMain(Runnable runnable) {
        sHandler.post(runnable);
    }

    public static int getStatusBarHeight(View view) {
        return getStatusBarHeight(view.getContext());
    }

    public static int getStatusBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        return ViewExtension.sp2px(25);
    }

    public static void setStatusBarBackgroundColor(Fragment fragment, int color) {
        setStatusBarBackgroundColor(fragment.getView(), color, false);
    }

    public static void setStatusBarBackgroundColor(Activity activity, int color) {
        setStatusBarBackgroundColor(ViewGroup.class.cast(activity.findViewById(android.R.id.content)), color, true);
    }

    private static void setStatusBarBackgroundColor(View root, int color, boolean fromActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (root != null && root instanceof LinearLayout) {
                Activity context = (Activity) root.getContext();
                Window window = context.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(color);
                    int systemUiVisibility = window.getDecorView().getSystemUiVisibility();
                    boolean isLightStatusBar = color == SignalColorHolder.YELLOW_COLOR;
                    if (isLightStatusBar) {
                        systemUiVisibility |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                    } else {
                        systemUiVisibility &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                    }
                    window.getDecorView().setSystemUiVisibility(systemUiVisibility);
                }

                if (!fromActivity || Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    FrameLayout container = null;
                    if (((LinearLayout) root).getChildCount() > 0 && ((LinearLayout) root).getChildAt(0).getId() == R.id.gmf_status_bar_layer) {
                        container = (FrameLayout) ((LinearLayout) root).getChildAt(0);
                    }
                    int statusBarHeight = getStatusBarHeight(root);
                    if (container != null) {
                        container.setBackgroundColor(color);
                    } else {
                        container = new FrameLayout(context);
                        container.setId(R.id.gmf_status_bar_layer);
                        container.setBackgroundColor(color);
                        {
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, statusBarHeight);
                            ((LinearLayout) root).addView(container, params);
                        }

                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                            FrameLayout darkLayerView = new FrameLayout(context);
                            darkLayerView.setBackgroundResource(R.color.gmf_dark_layer);
                            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(-1, statusBarHeight);
                            container.addView(darkLayerView, params);
                        }
                    }
                }
            }
        } else {


        }

    }

    public static void hideKeyboardFromWindow(View view) {
        hideKeyboardFromWindow(view.getContext());
    }

    public static void hideKeyboardFromWindow(Context context) {
        if (context != null && context instanceof Activity) {
            hideKeyboardFromWindow((Activity) context);
        }
    }

    public static void hideKeyboardFromWindow(Fragment fragment) {
        hideKeyboardFromWindow(fragment.getActivity());
    }

    public static void hideKeyboardFromWindow(Activity activity) {
        if (activity != null && activity.getCurrentFocus() != null && activity.getCurrentFocus().getWindowToken() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public static void showKeyboardFromWindow(View view) {
        showKeyboardFromWindow(view.getContext());
    }

    public static void showKeyboardFromWindow(Context context) {
        if (context != null && context instanceof Activity) {
            showKeyboardFromWindow((Activity) context);
        }
    }

    public static void showKeyboardFromWindow(Fragment fragment) {
        showKeyboardFromWindow(fragment.getActivity());
    }

    public static void showKeyboardFromWindow(Activity activity) {
        if (activity != null && activity.getCurrentFocus() != null) {
            InputMethodManager manager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.showSoftInput(activity.getCurrentFocus(), 0);
        }
    }

    public static GMFDialog createErrorDialog(BaseActivity activity, CharSequence message) {
        return createErrorDialog(activity, null, message, null);
    }

    public static GMFDialog createErrorDialog(BaseActivity activity, CharSequence titleOrNil, CharSequence message, CharSequence buttonTextOrNil) {

        return new GMFDialog.Builder(activity)
                .setCancelable(false)
                .setTitle(titleOrNil)
                .setMessage(message)
                .setPositiveButton(TextUtils.isEmpty(buttonTextOrNil) ? "知道了" : buttonTextOrNil, (dialog, which) -> {
                    dialog.dismiss();
                })
                .setOnDismissListener(dialog -> {
                    dialog.dismiss();
                    activity.finish();
                })
                .create();
    }

    public static void showToast(Fragment fragment, CharSequence msg) {
        showToast(fragment.getActivity(), msg);
    }

    public static void showToast(Context context, CharSequence msg) {
        if (context instanceof MyApplication) {
            MyApplication.SHARE_INSTANCE.mHandler.post(() -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }

    }


}
