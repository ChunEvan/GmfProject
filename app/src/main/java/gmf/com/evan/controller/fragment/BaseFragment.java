package gmf.com.evan.controller.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;

import java.io.Serializable;
import java.lang.ref.WeakReference;

import gmf.com.evan.R;
import gmf.com.evan.controller.activity.BaseActivity;
import gmf.com.evan.controller.activity.FragmentStackActivity;
import gmf.com.evan.rx.RxFragment;

import static gmf.com.evan.extension.ViewExtension.v_setText;

/**
 * Created by Evan on 16/6/30 上午9:53.
 */
public class BaseFragment extends RxFragment {

    private Serializable mExtraData;
    private Thread mUIThread;
    private Handler mHandler;

    public BaseFragment init(Bundle bundle) {
        if (bundle != null) {
            Bundle arguments = new Bundle(bundle);
            setArguments(arguments);
        } else {
            setArguments(new Bundle());
        }
        return this;
    }

    public void setExtraData(Serializable data) {
        mExtraData = data;
    }

    public Serializable getExtraData() {
        return mExtraData;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mUIThread = Thread.currentThread();
        mHandler = new Handler();
        if (savedInstanceState != null) {
            mExtraData = savedInstanceState.getSerializable("gmf_extra_data");
        }
        view.setClickable(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("gmf_extra_data", mExtraData);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    private boolean needToSetUserVisibleHint() {
        Activity activity = getActivity();
        if (activity instanceof FragmentStackActivity) {
            FragmentStackActivity cast = (FragmentStackActivity) activity;
            return !cast.mFragmentStack.isEmpty() && cast.mFragmentStack.peek() == this;
        } else {
            return true;
        }
    }


    private static final int PAGE_STATE_VISIBLE = 1;
    private static final int PAGE_STATE_INVISIBLE = 2;
    private int mPageState = 1;


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isTraceLifeCycle()) {
            if (isTraceLifeCycle()) {
                if (isVisibleToUser && mPageState == PAGE_STATE_VISIBLE) {
                    mPageState = PAGE_STATE_VISIBLE;

                } else {
                    if (mPageState == PAGE_STATE_VISIBLE) {
                        mPageState = PAGE_STATE_INVISIBLE;
                    }
                }
            }
        }
    }

    public Window getWindow() {
        return getActivity() == null ? null : getActivity().getWindow();
    }

    public boolean onInterceptKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    public boolean onInterceptActivityResult(int requestCode, int resultCode, Intent data) {
        return false;
    }

    protected boolean logLifeCycleEvent() {
        return true;
    }

    protected boolean isDelegateLifeCycleEventToSetUserVisible() {
        return true;
    }

    public boolean onInterceptGoBack() {
        if (mIsOperation) {
            showDialog(new WeakReference<>(this));
            return true;
        }
        return false;
    }

    private void showDialog(WeakReference<BaseFragment> baseFragmentWeakReference) {

    }

    protected boolean mIsOperation = false;

    public boolean mForceFinishOnGoBack = false;


    protected void updateTitle(CharSequence text) {
        v_setText(getView(), R.id.toolbarTitle, text);
    }

    public int mIsHostActivityTraceLifeRecycle = -1;

    protected boolean isTraceLifeCycle() {
        return !isHostActivityTraceLifeRecycle();
    }

    protected final boolean isHostActivityTraceLifeRecycle() {
        if (mIsHostActivityTraceLifeRecycle == -1) {
            FragmentActivity host = getActivity();
            boolean ret = host != null && host instanceof BaseActivity && ((BaseActivity) host).isTracePageLifeRecycle();
            mIsHostActivityTraceLifeRecycle = (ret == true) ? 1 : 0;
            return ret;
        }
        return mIsHostActivityTraceLifeRecycle == 1;
    }


}
