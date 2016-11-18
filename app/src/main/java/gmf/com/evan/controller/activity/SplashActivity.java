package gmf.com.evan.controller.activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import gmf.com.evan.BuildConfig;
import gmf.com.evan.MyApplication;
import gmf.com.evan.R;
import gmf.com.evan.base.CommonPreProxy;
import gmf.com.evan.controller.business.CommonController;
import gmf.com.evan.controller.dialog.DownloadDialog;
import gmf.com.evan.controller.dialog.GMFDialog;
import gmf.com.evan.controller.dialog.RequestPermissionDialog;
import gmf.com.evan.controller.internal.ActivityNavigation;
import gmf.com.evan.extension.Optional;
import gmf.com.evan.extension.UIControllerExtension;
import gmf.com.evan.manager.common.CommonManager;
import gmf.com.evan.manager.common.UpdateInfo;
import gmf.com.evan.system.RunTimePermissionChecker;
import gmf.com.evan.utils.AppUtil;
import gmf.com.evan.utils.DownloadUtil;
import gmf.com.evan.utils.PersistentObjectUtil;
import gmf.com.evan.widget.SplashCalendarView;
import rx.android.schedulers.AndroidSchedulers;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static gmf.com.evan.controller.internal.ActivityNavigation.an_MainPage;
import static gmf.com.evan.controller.internal.ActivityNavigation.showActivity;
import static gmf.com.evan.extension.MResultExtension.isSuccess;
import static gmf.com.evan.extension.ObjectExtension.opt;
import static gmf.com.evan.extension.ObjectExtension.safeCall;
import static gmf.com.evan.extension.SignalColorHolder.WHITE_COLOR;
import static gmf.com.evan.extension.UIControllerExtension.setStatusBarBackgroundColor;
import static gmf.com.evan.extension.UIControllerExtension.showToast;
import static gmf.com.evan.extension.ViewExtension.v_findView;
import static gmf.com.evan.extension.ViewExtension.v_setText;

/**
 * Created by Evan on 16/7/1 下午4:42.
 */
public class SplashActivity extends BaseActivity {

    private static ArrayList<String> strArray = new ArrayList<>();

    static {
        strArray.add("你今天聪明投资了吗");
        strArray.add("参加模拟炒股大赛\r\n立马大赚真钱");
        strArray.add("账户余额、已投资未运行资金每天收益\r\n不用等到最后一天再投资");
        strArray.add("操盘侠的账户资金安全\r\n由中国人保保驾护航");
        strArray.add("在这里遇到你\r\n真高兴");
        strArray.add("股票牛人圈\r\n跟对牛人，炒对股");
        strArray.add("真正高明的人\r\n就是能够借重别人的智慧\r\n来使自己不受蒙蔽的人\r\n--苏格拉底");
        strArray.add("买卖点比买卖什么股票更重要");
        strArray.add("风险来自于你不知道自己在干什么\r\n--沃沦・巴菲特");
        strArray.add("操盘乐\r\n雇个牛人，为你操盘");
        strArray.add("分红乐\r\n本金收益保障，更有浮动分红");
        strArray.add("盈多点，赢多点\r\n买涨买跌都能赚");
        strArray.add("公益乐\r\n赚钱的时候，不要忘了奉献爱心");
        strArray.add("一个好友多个帮\r\n邀请好友为你的分红乐加息");
        strArray.add("投资资金会进入证券会监管的托管帐户\r\n由券商进行独立的第三方监管");
        strArray.add("安全、便捷、透明\r\n满足不同需求和风险偏好的产品组合");
        strArray.add("每天签到赚积分\r\n商城好礼等你来兑");
        strArray.add("牛B的操盘手都在这里");
        strArray.add("只有穿越牛熊的操盘手才经得起考验");
        strArray.add("邀请好友投资，佣金马上到手");
        strArray.add("理解、尊重风险\r\n这就是顶尖操盘手标志\r\n--操盘侠明星操盘手・趋势信徒");
        strArray.add("独乐乐不如众乐乐\r\n邀请好友投资，佣金马上到手");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //        Fresco.getImagePipeline().clearMemoryCaches();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_splash);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        setStatusBarBackgroundColor(this, WHITE_COLOR);

        TextView versionLabel = v_findView(this, R.id.text_copyright);
        String versionText = versionLabel.getText().toString().replace("-version", AppUtil.getVersionName(this));
        v_setText(versionLabel, versionText);

        SplashCalendarView calendar = v_findView(this, R.id.splash_calendar);
        Random random = new Random();
        int index = random.nextInt(strArray.size());
        calendar.setInfos(strArray.get(index));

        calendar.setAlpha(0);
        ObjectAnimator.ofFloat(calendar, "alpha", 0, 1)
                .setDuration(1200)
                .start();


        if (needToCheckPermission()) {
            PersistentObjectUtil.writeHasRequestPermissionBefore(true);
            String[] deniedPermissions = RunTimePermissionChecker.getDeniedPermissionsImpl(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_PHONE_STATE, ACCESS_FINE_LOCATION});
            if (deniedPermissions.length > 0) {
                new RequestPermissionDialog(this, deniedPermissions) {

                    @Override
                    protected void onNextButtonClick(Dialog dialog, View button) {
                        dialog.dismiss();
                        performCheckPermission(deniedPermissions);
                    }
                }.show();
            } else {
                gotoNextAct();
            }
        } else {
            gotoNextAct();
        }
    }

    private boolean needToCheckPermission() {
        if (BuildConfig.DEBUG) {
            return Build.VERSION.SDK_INT > Build.VERSION_CODES.M;
        }
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.M && !PersistentObjectUtil.readHasRequestPermissionBefore();
    }

    private void performCheckPermission(String[] permissions) {
        RunTimePermissionChecker.requestPermissions(SplashActivity.this, permissions, (trigger, result) -> {
            if (result.isAllGranted || !result.isFistTimeRequest) {
                gotoNextAct();
            } else {
                String[] deniedPermissions = result.getDeniedPermissions(SplashActivity.this);
                createPermissionInfoDialog(deniedPermissions, trigger);
            }
        });
    }

    private Dialog createPermissionInfoDialog(String[] deniedPermissions, RunTimePermissionChecker.Trigger trigger) {
        List<String> desList = Stream.of(deniedPermissions)
                .map(it -> getPermissionDesc(it))
                .filter(it -> !TextUtils.isEmpty(it))
                .collect(Collectors.toList());

        StringBuilder builder = new StringBuilder();
        for (String desc : desList) {
            builder.append("\n").append(desc);
        }

        String content = builder.toString().replaceFirst("\n", "");
        return new GMFDialog.Builder(this)
                .setTitle("获取权限失败")
                .setMessage(content)
                .setPositiveButton("重试", (dialog, which) -> {
                    dialog.dismiss();
                    trigger.requestAgain();
                })
                .setNegativeButton("取消", (dialog1, which1) -> {
                    dialog1.dismiss();
                    gotoNextAct();
                })
                .create();
    }


    private boolean mGoingToNextAct = false;

    private void gotoNextAct() {
        if (mGoingToNextAct)
            return;
        mGoingToNextAct = true;

        runOnUIThreadDelayed(() -> {
            MyApplication.SHARE_INSTANCE.mHasLaunchSplash = true;
            int lastLaunchVersionCode = CommonPreProxy.getLastLaunchVersionCode();
            if (lastLaunchVersionCode < AppUtil.getVersionCode(this)) {
                showActivity(this, ActivityNavigation.an_FunctionIntroductionPage(true));
            } else {
                showActivity(this, an_MainPage(getIntent().getData()));
            }
            overridePendingTransition(0, 0);
            CommonPreProxy.updateLastLaunchVersionCode();
            performCheckUpdate();

            finish();
            mGoingToNextAct = false;
        }, 1500L);
    }

    private void performCheckUpdate() {
        CommonController.checkUpdate()
                .delay(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                    if (isSuccess(data) && data.data != null) {
                        UpdateInfo updateInfo = data.data;
                        if (updateInfo.showAlert || updateInfo.needForceUpdate) {
                            safeCall(() -> opt(createUpdateDialog(updateInfo)).consume(it -> it.show()));
                        }
                    }
                });
    }

    private static GMFDialog createUpdateDialog(UpdateInfo updateInfo) {
        Context context = MyApplication.SHARE_INSTANCE;
        File savePath = new File(context.getCacheDir().getAbsoluteFile() + File.separator + "update.apk");
        boolean isNeedToDownload = DownloadUtil.isNeedToDownload(savePath, updateInfo.md5);
        if (MyApplication.hasTopActivity()) {
            GMFDialog.Builder builder = new GMFDialog.Builder(MyApplication.getTopActivityOrNil().get());
            builder.setCancelable(false);
            builder.setTitle(updateInfo.updateTitle);
            builder.setMessage(updateInfo.updateMsg);
            builder.setPositiveButton(isNeedToDownload ? "立即更新" : "免流量更新", (dialog, which) -> {
                dialog.dismiss();
                if (BuildConfig.FLAVOR.equalsIgnoreCase("play")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.goldmf.GMFund"));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } else {
                    showDownloadDialog(updateInfo, savePath);
                }
            });
            if (!updateInfo.needForceUpdate) {
                builder.setNegativeButton("下次", (dialog, which) -> {
                    dialog.dismiss();
                    CommonManager.getInstance().delayUpdateAlert();
                    DownloadDialog.downloadOnBackground(updateInfo.url, savePath, updateInfo.md5, true);
                });
            }
            return builder.create();
        } else {
            return null;
        }
    }

    private static void showDownloadDialog(UpdateInfo info, File savePath) {
        if (MyApplication.hasTopActivity()) {

            DownloadDialog dialog = new DownloadDialog(MyApplication.SHARE_INSTANCE, info.url, savePath, Optional.of(info.md5));
            dialog.setCancelable(false);
            dialog.setFinishDownloadListener((d, isSuccess, file) -> {
                if (isSuccess) {
                    file.setReadable(true, false);
                    AppUtil.installApk(MyApplication.SHARE_INSTANCE, savePath);
                } else {
                    if (MyApplication.hasTopActivity()) {
                        if (info.needForceUpdate) {
                            Activity topActivity = MyApplication.getTopActivityOrNil().get();
                            if (topActivity instanceof BaseActivity) {
                                UIControllerExtension.createErrorDialog((BaseActivity) topActivity, "下载失败").show();
                            }
                        }
                    } else {
                        showToast(MyApplication.getTopActivityOrNil().get(), "下载失败");
                    }
                }
            });
            dialog.show();
            dialog.startDownload();
        }
    }

    private String getPermissionDesc(String permission) {
        switch (permission) {
            case WRITE_EXTERNAL_STORAGE:
                return "需要获取存储空间，以加快响应速度";
            case READ_PHONE_STATE:
                return "需要获取设备信息，以保障投资信息推送";
            case ACCESS_FINE_LOCATION:
                return "需要获取地理位置，以提升用户体验";
            default:
                return "";
        }
    }

}
