package gmf.com.evan.rx;

import android.support.v4.app.Fragment;
import android.util.Log;

import java.util.List;

import gmf.com.evan.base.MResults;
import gmf.com.evan.controller.manager.SubscriptionManager;
import gmf.com.evan.controller.protocol.RXViewControllerProtocol;
import rx.Observable;
import rx.Subscription;

/**
 * Created by Evan on 16/6/29 下午7:45.
 */
public class RxFragment extends Fragment implements RXViewControllerProtocol {
    private SubscriptionManager mSubManager = new SubscriptionManager();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSubManager.unsubscribe();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubManager.unsubscribe();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        mSubManager.unsubscribe(SubscriptionManager.LIFE_PERIOD.START_STOP);
    }

    @Override
    public void addSubscriptionToMain(String tag, Subscription subscription) {
        if (getActivity() != null && getView() != null) {
            int key = tag == null ? subscription.hashCode() : tag.hashCode();
            mSubManager.subscribe(SubscriptionManager.LIFE_PERIOD.CREATE_DESTROY, key, subscription);
        } else {
            mSubManager.unsubscribe();
        }
    }

    @Override
    public void addSubscriptionToVisible(String tag, Subscription subscription) {
        if (getActivity() != null && getView() != null) {
            int key = tag == null ? subscription.hashCode() : tag.hashCode();
            mSubManager.subscribe(SubscriptionManager.LIFE_PERIOD.START_STOP, key, subscription);
        } else {
            mSubManager.unsubscribe();
        }
    }

    @Override
    public void unSubscribeFromMain(String tag) {
        if (tag == null)
            return;
        mSubManager.unsubscribe(SubscriptionManager.LIFE_PERIOD.CREATE_DESTROY, tag.hashCode());
    }

    @Override
    public void unSubscribeFromVisible(String tag) {
        if (tag == null)
            return;
        mSubManager.unsubscribe(SubscriptionManager.LIFE_PERIOD.START_STOP, tag.hashCode());
    }

    @Override
    public boolean containSubscriptionOfMain(String tag) {
        return tag != null && mSubManager.contain(SubscriptionManager.LIFE_PERIOD.CREATE_DESTROY, tag.hashCode());
    }

    @Override
    public boolean containSubscriptionOfVisible(String tag) {
        return tag != null && mSubManager.contain(SubscriptionManager.LIFE_PERIOD.START_STOP, tag.hashCode());
    }

    public void log(String format, Object... args) {
        Log.e(getClass().getSimpleName(), String.format(format, args));
    }

    public final <T> ConsumeEventChain<T> consumeEvent(Observable<T> observable) {
        return new ConsumeEventChain<T>(this).setObservable(observable);
    }

    public final <T extends MResults.MResultsInfo> ConsumeEventChainMR<T> consumeEventMR(Observable<T> observable) {
        return new ConsumeEventChainMR<T>(this).setObservable(observable);
    }

    public final <T extends List<MResults.MResultsInfo>> ConsumeEventChainMRList<T> consumeEventMRList(Observable<T> observable) {
        return new ConsumeEventChainMRList<T>(this).setObservable(observable);
    }
}
