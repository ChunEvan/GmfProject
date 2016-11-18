package gmf.com.evan.controller.dialog;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.view.View;

import gmf.com.evan.MyApplication;
import gmf.com.evan.controller.activity.BaseActivity;
import rx.functions.Action0;

import static gmf.com.evan.extension.ObjectExtension.opt;
import static gmf.com.evan.extension.ObjectExtension.safeCall;
import static gmf.com.evan.extension.UIControllerExtension.hideKeyboardFromWindow;
import static gmf.com.evan.extension.ViewExtension.v_preDraw;


/**
 * Created by Evan on 16/7/5 下午5:19.
 */
public class BasicDialog extends Dialog {
    public BasicDialog(Context context) {
        super(context);
    }

    protected BasicDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    public void show() {
        opt(MyApplication.getTopActivityOrNil())
                .cast(BaseActivity.class)
                .consume(it -> hideKeyboardFromWindow(it));
        //        opt(MyApplication.getTopDialogOrNil())
        //                .consume(it -> it.dismiss());
        super.show();
        MyApplication.setTopDialog(this);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        MyApplication.setTopDialog(null);
    }

    protected void animateToShow(View rootView, Action0 onFinish) {
        if (rootView == null) {
            return;
        }

        v_preDraw(rootView, true, v -> {
            v.setTranslationY(v.getMeasuredHeight());
            ObjectAnimator animator = ObjectAnimator.ofFloat(rootView, "translationY", 0);
            animator.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationCancel(Animator animation) {
                    super.onAnimationCancel(animation);
                    safeCall(() -> onFinish.call());
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    safeCall(() -> onFinish.call());
                }
            });
            animator.start();
        });
    }

    protected void animateToHide(View rootView, Action0 onFinish) {
        if (rootView == null) return;

        ObjectAnimator animator = ObjectAnimator.ofFloat(rootView, "translationY", rootView.getMeasuredHeight());
        animator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                if (onFinish != null)
                    onFinish.call();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (onFinish != null)
                    onFinish.call();
            }
        });
        animator.start();

    }
}
