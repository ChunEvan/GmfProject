package gmf.com.evan.rx;

import java.util.List;

import gmf.com.evan.base.MResults;
import gmf.com.evan.controller.protocol.RXViewControllerProtocol;
import gmf.com.evan.extension.common.function.SafeAction0;
import gmf.com.evan.extension.common.function.SafeAction1;
import rx.Observable;

import static gmf.com.evan.extension.MResultExtension.isSuccess;
import static gmf.com.evan.extension.ObjectExtension.safeCall;


/**
 * Created by Evan on 16/6/29 下午3:34.
 */
public class ConsumeEventChainMRList<T extends List<MResults.MResultsInfo>> extends ConsumeEventChain<T> {
    protected SafeAction1<T> mOnSuccess;
    protected SafeAction1<T> mOnFail;

    public ConsumeEventChainMRList(RXViewControllerProtocol vc) {
        super(vc);
    }

    @Override
    public ConsumeEventChainMRList<T> setTag(String tag) {
        super.setTag(tag);
        return this;
    }

    @Override
    public ConsumeEventChain<T> setPolicy(POLICY policy) {
        super.setPolicy(policy);
        return this;
    }

    @Override
    public ConsumeEventChainMRList<T> addToMain() {
        super.addToMain();
        return this;
    }

    @Override
    public ConsumeEventChainMRList<T> addToVisible() {
        super.addToVisible();
        return this;
    }

    @Override
    public ConsumeEventChainMRList<T> setObservable(Observable<T> observable) {
        super.setObservable(observable);
        return this;
    }

    public ConsumeEventChainMRList<T> onConsumed(SafeAction0 operation) {
        mOnConsumed = operation;
        return this;
    }

    @Override
    public ConsumeEventChainMRList<T> onNextStart(SafeAction1<T> operation) {
        super.onNextStart(operation);
        return this;
    }

    public ConsumeEventChainMRList<T> onNextSuccess(SafeAction1<T> operation) {
        mOnSuccess = operation;
        return this;
    }

    public ConsumeEventChainMRList<T> onNextFail(SafeAction1<T> operation) {
        mOnFail = operation;
        return this;
    }

    @Override
    public ConsumeEventChainMRList<T> onNextFinish(SafeAction1<T> operation) {
        super.onNextFinish(operation);
        return this;
    }

    public SafeAction0 getOnConsumed() {
        return mOnConsumed;
    }

    public SafeAction1<T> getOnNextStart() {
        return response -> {
            safeCall(() -> {
                if (mOnStart != null) {
                    mOnStart.call(response);
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
