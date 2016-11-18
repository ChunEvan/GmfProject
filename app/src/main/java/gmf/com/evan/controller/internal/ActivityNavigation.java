package gmf.com.evan.controller.internal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;

import java.lang.ref.WeakReference;

import gmf.com.evan.R;
import gmf.com.evan.controller.activity.BaseActivity;
import gmf.com.evan.controller.activity.BaseActivity.TRANSACTION_DIRECTION;
import gmf.com.evan.controller.activity.CommonProxyActivity;
import gmf.com.evan.controller.activity.MainActivity;
import gmf.com.evan.controller.fragment.IntroductionFragments;
import rx.functions.Func1;

import static gmf.com.evan.extension.ObjectExtension.opt;
import static gmf.com.evan.extension.ObjectExtension.safeCall;

/**
 * Created by Evan on 16/7/9 下午5:28.
 */
public class ActivityNavigation {
    private static final String KEY_FRAGMENT_CLASS_NAME = "gmf_fragment_class_name";
    private static final String KEY_IS_REQUEST_LOGIN_BOOLEAN = "an_is_required_login";
    private static final String KEY_IS_REQUEST_OPEN_SIMULATION_ACCOUNT_BOOLEAN = "an_is_required_open_simulation_account";
    private static final String KEY_IS_REQUEST_AUTHENTIC_BOOLEAN = "an_is_required_authentic";
    private static final String KEY_IS_REQUEST_BIND_CN_CARD_BOOLEAN = "an_is_bind_cn_card";

    public static final String KEY_LAUNCH_MAIN_ACTIVITY_BOOLEAN = "gmf_launch_main_activity";

    private ActivityNavigation() {
    }

    public static void showActivity(Fragment fragment, Func1<Context, Intent> pageIntentBuilder) {
        showActivity(fragment, pageIntentBuilder);
    }

    public static void showActivity(Fragment fragment, Func1<Context, Intent> pageIntentBuilder, TRANSACTION_DIRECTION direction) {
        showActivity(fragment, pageIntentBuilder, direction);
    }

    public static void showActivity(Context context, Func1<Context, Intent> pageIntentBuilder) {

        showActivity(context, pageIntentBuilder, TRANSACTION_DIRECTION.DEFAULT);
    }

    public static void showActivity(Context context, Func1<Context, Intent> pageIntentBuilder, TRANSACTION_DIRECTION direction) {
        showActivity(new WeakReference<Context>(context), pageIntentBuilder.call(context), direction);
    }

    private static void showActivity(WeakReference<Context> contextRef, Intent intent, TRANSACTION_DIRECTION direction) {

        safeCall(() -> {
            boolean isRequestLogin = intent.getBooleanExtra(KEY_IS_REQUEST_LOGIN_BOOLEAN, false);
            boolean isRequestOpenSimulationAccount = intent.getBooleanExtra(KEY_IS_REQUEST_OPEN_SIMULATION_ACCOUNT_BOOLEAN, false);
            boolean isRequestAuthentic = intent.getBooleanExtra(KEY_IS_REQUEST_AUTHENTIC_BOOLEAN, false);
            boolean isRequestBindCNCard = intent.getBooleanExtra(KEY_IS_REQUEST_BIND_CN_CARD_BOOLEAN, false);

            isRequestLogin = (isRequestOpenSimulationAccount || isRequestAuthentic || isRequestBindCNCard) ? true : isRequestLogin;
            isRequestAuthentic = isRequestBindCNCard ? true : isRequestAuthentic;


            performToNextPage(contextRef, intent, direction);
        });

    }

    private static void performToNextPage(WeakReference<Context> contextRef, Intent intent, TRANSACTION_DIRECTION direction) {
        if (!isActivity(contextRef)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        if (direction != null) {
            intent.putExtra(BaseActivity.KEY_TRANSACTION_DIRECTION, direction);
        }
        if (isContextAlive(contextRef)) {
            getContext(contextRef).startActivity(intent);
        }

        if (direction != null && isBaseActivity(contextRef)) {
            if (direction == TRANSACTION_DIRECTION.DEFAULT) {
                getBaseActivity(contextRef).overridePendingTransition(android.support.design.R.anim.abc_grow_fade_in_from_bottom, R.anim.stay);
            } else if (direction == TRANSACTION_DIRECTION.VERTICAL) {
                getBaseActivity(contextRef).overridePendingTransition(android.support.design.R.anim.abc_slide_in_bottom, R.anim.stay);
            } else if (direction == TRANSACTION_DIRECTION.NONE) {
                getBaseActivity(contextRef).overridePendingTransition(0, 0);
            }
        }
    }

    public static boolean isContextAlive(WeakReference<Context> contextRef) {
        return opt(contextRef).isPresent();
    }

    public static Context getContext(WeakReference<Context> contextRef) {
        return opt(contextRef).orNull();
    }

    private static boolean isActivity(WeakReference<Context> contextRef) {
        return opt(contextRef).cast(Activity.class).isPresent();
    }

    private static boolean isBaseActivity(WeakReference<Context> contextRef) {
        return opt(contextRef).cast(BaseActivity.class).isPresent();
    }

    public static BaseActivity getBaseActivity(WeakReference<Context> contextRef) {
        return opt(contextRef).cast(BaseActivity.class).orNull();
    }

    public static Func1<Context, Intent> an_FunctionIntroductionPage(boolean launchMainActivity) {
        return ctx -> {
            Intent intent = new Intent(ctx, CommonProxyActivity.class);
            intent.putExtra(KEY_FRAGMENT_CLASS_NAME, IntroductionFragments.PPTHostFragment.class.getName());
            intent.putExtra(KEY_LAUNCH_MAIN_ACTIVITY_BOOLEAN, launchMainActivity);
            return intent;
        };
    }

    public static Func1<Context, Intent> an_MainPage(Uri uri) {
        return ctx -> {
            Intent intent = new Intent(ctx, MainActivity.class);
            intent.setData(uri);
            return intent;
        };
    }

}
