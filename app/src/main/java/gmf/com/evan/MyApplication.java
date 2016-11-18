package gmf.com.evan;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Process;
import android.support.multidex.MultiDexApplication;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;

import java.lang.ref.WeakReference;

import gmf.com.evan.controller.activity.BaseActivity;
import gmf.com.evan.controller.business.NotificationCenter;
import gmf.com.evan.extension.ObjectExtension;
import gmf.com.evan.utils.FileUtil;

/**
 * Created by Evan on 16/6/16 下午2:44.
 */
public class MyApplication extends MultiDexApplication {

    public static MyApplication SHARE_INSTANCE = null;

    public WeakReference<Activity> mTopActivityOrNil = null;
    public WeakReference<Dialog> mTopDialogOrNil = null;
    public boolean mHasRequestFreshCommon = false;
    public boolean mLoginPageShowing = false;
    public boolean mHasLaunchSplash = false;

    public Handler mHandler = new Handler();

    public static void setTopActivity(Activity activity) {
        MyApplication.SHARE_INSTANCE.mTopActivityOrNil = activity == null ? null : new WeakReference<>(activity);
    }

    public static WeakReference<Activity> getTopActivityOrNil() {
        return ObjectExtension.opt(SHARE_INSTANCE).let(it -> it.mTopActivityOrNil).or(null);
    }

    public static boolean hasTopActivity() {
        return MyApplication.SHARE_INSTANCE.mTopActivityOrNil != null && MyApplication.SHARE_INSTANCE.mTopActivityOrNil.get() != null;
    }

    public static void setTopDialog(Dialog dialog) {
        MyApplication.SHARE_INSTANCE.mTopDialogOrNil = dialog == null ? null : new WeakReference<>(dialog);
    }

    public static WeakReference<Dialog> getTopDialogOrNil() {
        return ObjectExtension.opt(SHARE_INSTANCE).let(it -> it.mTopDialogOrNil).or(null);
    }

    public static boolean hasTopDialog() {
        return MyApplication.SHARE_INSTANCE.mTopDialogOrNil != null && MyApplication.SHARE_INSTANCE.mTopDialogOrNil.get() != null;
    }

    public static void post(Runnable runnable) {
        if (SHARE_INSTANCE != null && runnable != null) {
            SHARE_INSTANCE.mHandler.post(runnable);
        }
    }

    public static void postDelayed(Runnable runnable, long delayInTimeMills) {
        if (SHARE_INSTANCE != null && runnable != null) {
            SHARE_INSTANCE.mHandler.postDelayed(runnable, delayInTimeMills);
        }
    }

    public static Resources getResource() {
        return SHARE_INSTANCE.getResources();
    }

    @Override
    public void onCreate() {
        SHARE_INSTANCE = this;
        super.onCreate();

        runInMainProcess(() -> {
            FileUtil.mContext = MyApplication.this;
            NotificationCenter.init();
            initFresco();
        });
    }

    private void runInMainProcess(Runnable task) {
        int pid = Process.myPid();
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo info : am.getRunningAppProcesses()) {
            if (info.pid == pid) {
                if (info.processName.equalsIgnoreCase(getPackageName())) {
                    task.run();
                }
                break;
            }
        }
    }

    public boolean onForeground() {
        if (mTopActivityOrNil != null && mTopActivityOrNil.get() != null) {
            Activity activity = mTopActivityOrNil.get();
            if (activity instanceof BaseActivity) {
                BaseActivity cast = (BaseActivity) activity;
                return cast.onForegound();
            }
        }
        return false;
    }

    private void initFresco() {
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setBitmapsConfig(Bitmap.Config.RGB_565)
                .setResizeAndRotateEnabledForNetwork(true)
                .build();
        Fresco.initialize(this, config);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        SHARE_INSTANCE = null;
    }
}
