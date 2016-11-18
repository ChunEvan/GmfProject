package gmf.com.evan.rx;

import gmf.com.evan.base.MResults;
import gmf.com.evan.controller.protocol.RXViewControllerProtocol;
import gmf.com.evan.extension.common.function.SafeAction0;
import gmf.com.evan.extension.common.function.SafeAction1;
import rx.Observable;

import static gmf.com.evan.base.MResults.MResultsInfo.isSuccess;
import static gmf.com.evan.extension.ObjectExtension.safeCall;

/**
 * Created by Evan on 16/6/29 下午3:00.
 */
public class ConsumeEventChainMR<T extends MResults.MResultsInfo> extends ConsumeEventChain<T> {


    private SafeAction1<T> mOnSuccess;
    private SafeAction1<T> mOnFail;

    public ConsumeEventChainMR(RXViewControllerProtocol vc) {
        super(vc);
    }

    @Override
    public ConsumeEventChainMR<T> setTag(String tag) {
        super.setTag(tag);
        return this;
    }

    @Override
    public ConsumeEventChain<T> setPolicy(POLICY policy) {
        super.setPolicy(policy);
        return this;
    }

    @Override
    public ConsumeEventChainMR<T> addToMain() {
        super.addToMain();
        return this;
    }

    @Override
    public ConsumeEventChainMR<T> addToVisible() {
        super.addToVisible();
        return this;
    }

    @Override
    public ConsumeEventChainMR<T> setObservable(Observable<T> observable) {
        super.setObservable(observable);
        return this;
    }

    @Override
    public ConsumeEventChainMR<T> onConsumed(SafeAction0 operation) {
        mOnConsumed = operation;
        return this;
    }

    @Override
    public ConsumeEventChainMR<T> onNextStart(SafeAction1 operation) {
        super.onNextStart(operation);
        return this;
    }

    @Override
    public ConsumeEventChainMR<T> onNextFinish(SafeAction1 operation) {
        super.onNextFinish(operation);
        return this;
    }

    public ConsumeEventChainMR<T> onNextSuccess(SafeAction1<T> operation) {
        mOnSuccess = operation;
        return this;
    }

    public ConsumeEventChainMR<T> onNextFail(SafeAction1<T> operation) {
        mOnFail = operation;
        return this;
    }

    @Override
    public SafeAction0 getOnConsumed() {
        return mOnConsumed;
    }

    @Override
    public SafeAction1<T> getOnNextStart() {
        return response -> {
            safeCall(() -> {
                SafeAction1<T> action = this.mOnStart;
                if (action != null) {
                    action.call(response);
                }
            });

            if (isSuccess(response)) {
                safeCall(() -> {
                    SafeAction1<T> action = getOnNextSuccess();
                    if (action != null) {
                        action.call(response);
                    }
                });
            } else {
                safeCall(() -> {
                    SafeAction1<T> action = getOnNextFail();
                    if (action != null) {
                        action.call(response);
                    }
                });
            }
        };
    }

    public SafeAction1<T> getOnNextSuccess() {
        return mOnSuccess;
    }

    public SafeAction1<T> getOnNextFail() {
        return mOnFail;
    }
}
