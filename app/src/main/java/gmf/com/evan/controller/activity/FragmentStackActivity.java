package gmf.com.evan.controller.activity;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.Stack;
import java.util.concurrent.TimeUnit;

import gmf.com.evan.R;
import gmf.com.evan.controller.fragment.BaseFragment;
import gmf.com.evan.extension.UIControllerExtension;
import rx.Observable;

import static gmf.com.evan.extension.UIControllerExtension.hideKeyboardFromWindow;

/**
 * Created by Evan on 16/6/30 上午10:17.
 */
public class FragmentStackActivity extends BaseActivity {

    public Stack<BaseFragment> mFragmentStack = new Stack<>();

    @Override
    protected boolean logLifeCycleEvent() {
        return false;
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        addExtraWindowOffsetIfNeed();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        addExtraWindowOffsetIfNeed();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        addExtraWindowOffsetIfNeed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mFragmentStack != null) {
            if (mFragmentStack.peek().onInterceptKeyDown(keyCode, event)) {
                return true;
            }
        }

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (!mFragmentStack.isEmpty()) {
            mFragmentStack.peek().onInterceptActivityResult(requestCode, resultCode, data);
        }
    }

    protected void addExtraWindowOffsetIfNeed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View rootView = ((ViewGroup) getWindow().getDecorView()).getChildAt(0);
            if (rootView != null) {
                ViewGroup.LayoutParams params = rootView.getLayoutParams();
                if (params instanceof ViewGroup.MarginLayoutParams) {
                    int statusBarHeight = UIControllerExtension.getStatusBarHeight(this);
                    ((ViewGroup.MarginLayoutParams) params).topMargin = -statusBarHeight;
                    rootView.setLayoutParams(params);
                }
            }
        }
    }


    public BaseFragment peekTopFragmentOrNil() {
        return mFragmentStack.isEmpty() ? null : mFragmentStack.peek();
    }

    protected int fragmentCount() {
        return mFragmentStack.size();
    }

    public static void goBack(BaseFragment currentFragment) {
        if (currentFragment.getActivity() instanceof FragmentStackActivity) {
            FragmentStackActivity activity = (FragmentStackActivity) currentFragment.getActivity();
            activity.goBack();
        } else {
            if (!currentFragment.onInterceptGoBack()) {
                currentFragment.getActivity().finish();
            }
        }
    }

    public void goBack() {
        if (!mFragmentStack.isEmpty() && mFragmentStack.peek().onInterceptGoBack()) {
            return;
        }

        if (fragmentCount() > 1 && mFragmentStack.peek().mForceFinishOnGoBack) {
            popFragmentOrNil();
        } else {
            if (!mFragmentStack.isEmpty()) {
                mFragmentStack.peek().setUserVisibleHint(false);
            }
            mFragmentStack.clear();
            finish();
        }
    }

    public static void pushFragment(BaseFragment currentFragment, BaseFragment newFragment) {
        FragmentStackActivity activity = (FragmentStackActivity) currentFragment.getActivity();
        activity.pushFragment(newFragment, TRANSACTION_DIRECTION.DEFAULT);
    }

    public static void pushFragment(BaseFragment currentFragment, BaseFragment newFragment, TRANSACTION_DIRECTION direction) {
        FragmentStackActivity activity = (FragmentStackActivity) currentFragment.getActivity();
        activity.pushFragment(newFragment, direction);
    }

    public static void replaceTopFragment(BaseFragment currentFragment, BaseFragment newFragment) {
        FragmentStackActivity activity = (FragmentStackActivity) currentFragment.getActivity();
        activity.replaceTopFragment(newFragment, TRANSACTION_DIRECTION.DEFAULT);
    }

    public static void replaceTopFragment(BaseFragment currentFragment, BaseFragment newFragment, TRANSACTION_DIRECTION direction) {
        FragmentStackActivity activity = (FragmentStackActivity) currentFragment.getActivity();
        activity.replaceTopFragment(newFragment, direction);
    }

    public static void resetFragment(BaseFragment currentFragment, BaseFragment newFragment) {
        FragmentStackActivity activity = (FragmentStackActivity) currentFragment.getActivity();
        activity.resetFragment(currentFragment, TRANSACTION_DIRECTION.DEFAULT);
    }

    public static void resetFragment(BaseFragment currentFragment, BaseFragment newFragment, TRANSACTION_DIRECTION direction) {
        FragmentStackActivity activity = (FragmentStackActivity) currentFragment.getActivity();
        activity.resetFragment(currentFragment, direction);
    }

    public void pushFragment(BaseFragment fragment) {
        TRANSACTION_DIRECTION direction = (TRANSACTION_DIRECTION) getIntent().getSerializableExtra(BaseActivity.KEY_TRANSACTION_DIRECTION);
        pushFragment(fragment, direction);
    }

    public void pushFragment(BaseFragment fragment, @Nullable TRANSACTION_DIRECTION direction) {

        hideKeyboardFromWindow(this);
        fragment.setExtraData(direction);

        BaseFragment preFragmentOrNil = mFragmentStack.isEmpty() ? null : mFragmentStack.peek();
        if (preFragmentOrNil != null) {
            preFragmentOrNil.setUserVisibleHint(false);
        }
        mFragmentStack.push(fragment);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (fragmentCount() > 1) {
            if (direction == TRANSACTION_DIRECTION.DEFAULT) {
                transaction.setCustomAnimations(android.support.design.R.anim.abc_grow_fade_in_from_bottom, R.anim.stay);
            } else if (direction == TRANSACTION_DIRECTION.VERTICAL) {
                transaction.setCustomAnimations(android.support.design.R.anim.abc_slide_in_bottom, R.anim.stay);
            } else if (direction == TRANSACTION_DIRECTION.NONE) {
                transaction.setCustomAnimations(0, 0);
            }
        } else {
            transaction.setCustomAnimations(0, 0, 0, 0);
        }

        transaction.add(getFragmentContainerId(), fragment);
        if (preFragmentOrNil != null) {
            transaction.hide(preFragmentOrNil);
        }
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void replaceTopFragment(BaseFragment fragment, @Nullable TRANSACTION_DIRECTION direction) {
        hideKeyboardFromWindow(this);
        fragment.setExtraData(direction);
        int count = fragmentCount();
        if (count == 0) {
            pushFragment(fragment, direction);
        } else {
            BaseFragment preFragment = mFragmentStack.pop();
            preFragment.setUserVisibleHint(false);
            mFragmentStack.push(fragment);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (direction == TRANSACTION_DIRECTION.DEFAULT) {
                transaction.setCustomAnimations(android.support.design.R.anim.abc_grow_fade_in_from_bottom, R.anim.stay);
            } else if (direction == TRANSACTION_DIRECTION.VERTICAL) {
                transaction.setCustomAnimations(android.support.design.R.anim.abc_slide_in_bottom, R.anim.stay);
            } else if (direction == TRANSACTION_DIRECTION.NONE) {
                transaction.setCustomAnimations(0, 0);
            }

            transaction.add(getFragmentContainerId(), fragment);
            transaction.commit();
            transaction.hide(preFragment);

            consumeEvent(Observable.empty().delaySubscription(300, TimeUnit.MILLISECONDS))
                    .onComplete(() -> getSupportFragmentManager().beginTransaction().remove(preFragment).commit())
                    .done();
        }
    }

    public void resetFragment(BaseFragment fragment, TRANSACTION_DIRECTION direction) {
        hideKeyboardFromWindow(this);
        fragment.setExtraData(direction);
        int count = fragmentCount();
        if (count == 0) {
            pushFragment(fragment, direction);
        } else {
            mFragmentStack.peek().setUserVisibleHint(false);
            mFragmentStack.push(fragment);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (direction == TRANSACTION_DIRECTION.DEFAULT) {
                transaction.setCustomAnimations(android.support.design.R.anim.abc_grow_fade_in_from_bottom, R.anim.stay);
            } else if (direction == TRANSACTION_DIRECTION.VERTICAL) {
                transaction.setCustomAnimations(android.support.design.R.anim.abc_slide_in_bottom, R.anim.stay);
            } else if (direction == TRANSACTION_DIRECTION.NONE) {
                transaction.setCustomAnimations(0, 0);
            }

            transaction.add(getFragmentContainerId(), fragment);
            transaction.commit();
            consumeEvent(Observable.empty().delaySubscription(300, TimeUnit.MILLISECONDS))
                    .onComplete(() -> {
                        if (mFragmentStack.size() > 1) {
                            BaseFragment topFragment = mFragmentStack.pop();
                            FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
                            while (!mFragmentStack.isEmpty()) {
                                transaction1.remove(mFragmentStack.pop());
                            }
                            transaction1.commit();
                            mFragmentStack.push(topFragment);
                        }
                    })
                    .done();
        }
    }

    public Fragment popFragmentOrNil() {
        if (fragmentCount() == 0)
            return null;

        BaseFragment removeFragment = mFragmentStack.pop();
        removeFragment.setUserVisibleHint(false);
        BaseFragment topFragmentOrNil = mFragmentStack.isEmpty() ? null : mFragmentStack.peek();
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            TRANSACTION_DIRECTION direction = (TRANSACTION_DIRECTION) removeFragment.getExtraData();
            if (direction == TRANSACTION_DIRECTION.DEFAULT) {
                transaction.setCustomAnimations(android.support.design.R.anim.abc_grow_fade_in_from_bottom, R.anim.stay);
            } else if (direction == TRANSACTION_DIRECTION.VERTICAL) {
                transaction.setCustomAnimations(android.support.design.R.anim.abc_slide_in_bottom, R.anim.stay);
            } else if (direction == TRANSACTION_DIRECTION.NONE) {
                transaction.setCustomAnimations(0, 0);
            }

            transaction.remove(removeFragment);
            if (topFragmentOrNil != null) {
                transaction.show(topFragmentOrNil);
            }
            transaction.commit();
        }

        if (topFragmentOrNil != null) {
            topFragmentOrNil.setUserVisibleHint(true);
        }
        return removeFragment;
    }

    private int getFragmentContainerId() {
                return R.id.container_fragment;
    }
}
