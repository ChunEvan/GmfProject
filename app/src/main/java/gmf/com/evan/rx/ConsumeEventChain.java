package gmf.com.evan.rx;

import java.lang.ref.WeakReference;

import gmf.com.evan.controller.protocol.RXViewControllerProtocol;
import gmf.com.evan.extension.common.function.SafeAction0;
import gmf.com.evan.extension.common.function.SafeAction1;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

import static gmf.com.evan.extension.ObjectExtension.opt;
import static gmf.com.evan.extension.ObjectExtension.safeCall;


/**
 * Created by Evan on 16/6/23 下午8:22.
 */
public class ConsumeEventChain<T> {

    protected Observable<T> mObservable;
    protected String mTag;
    protected POLICY mPolicy;
    protected boolean mIsAddToMain;
    protected boolean mIsAddToVisible;
    protected SafeAction1 mOnStart;
    protected SafeAction1 mOnFinish;
    protected SafeAction0 mOnComplete;
    protected SafeAction0 mOnConsumed;
    protected WeakReference<RXViewControllerProtocol> mVCRef = new WeakReference<>(null);

    public enum POLICY {
        REPLACE,
        IGNORED,
    }

    public ConsumeEventChain(RXViewControllerProtocol vc) {
        mVCRef = new WeakReference<>(vc);
    }

    public ConsumeEventChain<T> setObservable(Observable<T> observable) {
        mObservable = observable;
        return this;
    }

    public ConsumeEventChain<T> setTag(String tag) {
        mTag = tag;
        return this;
    }

    public ConsumeEventChain<T> setPolicy(POLICY policy) {
        mPolicy = policy == null ? POLICY.REPLACE : policy;
        return this;
    }

    public ConsumeEventChain<T> addToMain() {
        mIsAddToMain = true;
        mIsAddToVisible = false;
        return this;
    }

    public ConsumeEventChain<T> addToVisible() {
        mIsAddToMain = false;
        mIsAddToVisible = true;
        return this;
    }

    public ConsumeEventChain<T> onConsumed(SafeAction0 operation) {
        mOnConsumed = operation;
        return this;
    }

    public ConsumeEventChain<T> onNextStart(SafeAction1<T> operation) {
        mOnStart = operation;
        return this;
    }

    public ConsumeEventChain<T> onNextFinish(SafeAction1<T> operation) {
        mOnFinish = operation;
        return this;
    }

    public ConsumeEventChain<T> onComplete(SafeAction0 mOnComplete) {
        this.mOnComplete = mOnComplete;
        return this;
    }

    public SafeAction0 getOnConsumed() {
        return mOnConsumed;
    }

    public SafeAction0 getOnComplete() {
        return mOnComplete;
    }

    public SafeAction1<T> getOnNextStart() {
        return mOnStart;
    }

    public SafeAction1<T> getOnNextFinish() {
        return mOnFinish;
    }

    public void done() {
        if (mObservable == null)
            return;
        opt(getOnConsumed()).consume(it -> safeCall(() -> it.call()));
        if (mVCRef.get() != null) {
            if (mPolicy == POLICY.IGNORED) {
                RXViewControllerProtocol vc = mVCRef.get();
                if (mIsAddToMain && vc.containSubscriptionOfMain(mTag))
                    return;
                if (mIsAddToVisible && vc.containSubscriptionOfMain(mTag))
                    return;
            }
        }
        Subscription sub = mObservable
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(response -> {
                    opt(getOnNextStart()).consume(it -> safeCall(() -> it.call(response)));
                    opt(getOnNextFinish()).consume(it -> safeCall(() -> it.call(response)));
                })
                .doOnCompleted(() -> {
                    opt(getOnComplete()).consume(it -> safeCall(() -> it.call()));
                })
                .subscribe();

        if (mIsAddToMain && mVCRef.get() != null) {
            mVCRef.get().addSubscriptionToMain(mTag, sub);
        }

        if (mIsAddToVisible && mVCRef.get() != null) {
            mVCRef.get().addSubscriptionToVisible(mTag, sub);
        }
    }
}
