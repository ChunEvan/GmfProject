package gmf.com.evan.controller.activity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Window;

import java.util.LinkedList;
import java.util.List;

import gmf.com.evan.MyApplication;
import gmf.com.evan.R;
import gmf.com.evan.rx.RXActivity;
import gmf.com.evan.utils.GlobalVariableDic;
import rx.functions.Action0;

import static gmf.com.evan.extension.ViewExtension.findToolbar;
import static gmf.com.evan.extension.ViewExtension.v_setText;


/**
 * Created by Evan on 16/6/29 下午5:36.
 */
public class BaseActivity extends RXActivity {

    public static final String KEY_TRANSACTION_DIRECTION = "gmf_transaction_direction";
    private Handler mHandler;
    private boolean mOnForegound = false;
    private List<String> mRelativeObjectIDList = new LinkedList<>();

    public enum TRANSACTION_DIRECTION {
        DEFAULT, VERTICAL, NONE
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        }
        super.onCreate(savedInstanceState);
        MyApplication.setTopActivity(this);

        if (MyApplication.SHARE_INSTANCE != null && !MyApplication.SHARE_INSTANCE.mHasRequestFreshCommon) {
            MyApplication.SHARE_INSTANCE.mHasRequestFreshCommon = true;
//            CommonManager.getInstance().freshCommonInfo();
        }


        if (logLifeCycleEvent()) {

        }
        mHandler = new Handler();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mOnForegound = true;
        MyApplication.setTopActivity(this);
        if (isTracePageLifeRecycle()) {

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isTracePageLifeRecycle()) {


        }
    }

    @Override
    protected void onStop() {
        mOnForegound = false;
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (String objectID : mRelativeObjectIDList) {
            GlobalVariableDic.shareInstance().remove(objectID);
        }
    }

    @Override
    public void finish() {
        super.finish();
        TRANSACTION_DIRECTION direction = (TRANSACTION_DIRECTION) getIntent().getSerializableExtra(KEY_TRANSACTION_DIRECTION);
        if (direction != null) {
            if (direction == TRANSACTION_DIRECTION.DEFAULT) {
                overridePendingTransition(0, android.support.design.R.anim.abc_shrink_fade_out_from_bottom);
            } else if (direction == TRANSACTION_DIRECTION.VERTICAL) {
                overridePendingTransition(0, android.support.design.R.anim.abc_slide_out_bottom);
            } else if (direction == TRANSACTION_DIRECTION.NONE) {
                overridePendingTransition(0, 0);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            supportFinishAfterTransition();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void addRelativeObjectID(String objectID) {
        if (!TextUtils.isEmpty(objectID))
            mRelativeObjectIDList.add(objectID);
    }

    public boolean onForegound() {
        return mOnForegound;
    }

    protected boolean logLifeCycleEvent() {
        return true;
    }

    public boolean isTracePageLifeRecycle() {
        return true;
    }

    public void runOnUIThreadDelayed(Action0 task, long delayTimeMillis) {
        mHandler.postDelayed(task::call, delayTimeMillis);
    }

    public void updateTitle(CharSequence title) {
        v_setText(findToolbar(this), R.id.toolbarTitle, title);
    }

}
