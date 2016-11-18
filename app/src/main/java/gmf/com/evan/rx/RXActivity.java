package gmf.com.evan.rx;

import android.support.v7.app.AppCompatActivity;

import java.util.List;

import gmf.com.evan.base.MResults;
import gmf.com.evan.controller.manager.SubscriptionManager;
import gmf.com.evan.controller.manager.SubscriptionManager.LIFE_PERIOD;
import gmf.com.evan.controller.protocol.RXViewControllerProtocol;
import rx.Observable;
import rx.Subscription;

/**
 * Created by Evan on 16/6/23 下午6:01.
 */
public class RXActivity extends AppCompatActivity implements RXViewControllerProtocol {

    private SubscriptionManager mSubManager = new SubscriptionManager();

    @Override
    protected void onStart() {
        super.onStart();
        mSubManager.unsubscribe(LIFE_PERIOD.START_STOP);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSubManager.unsubscribe(LIFE_PERIOD.START_STOP);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubManager.unsubscribe();
    }

    @Override
    public void addSubscriptionToMain(String tag, Subscription subscription) {
        int key = tag == null ? subscription.hashCode() : tag.hashCode();
        mSubManager.subscribe(LIFE_PERIOD.CREATE_DESTROY, key, subscription);
    }

    @Override
    public void addSubscriptionToVisible(String tag, Subscription subscription) {
        int key = tag == null ? subscription.hashCode() : tag.hashCode();
        mSubManager.subscribe(LIFE_PERIOD.START_STOP, key, subscription);
    }

    @Override
    public void unSubscribeFromMain(String tag) {
        if (tag == null)
            return;
        int key = tag.hashCode();
        mSubManager.unsubscribe(LIFE_PERIOD.CREATE_DESTROY, key);
    }

    @Override
    public void unSubscribeFromVisible(String tag) {
        if (tag == null)
            return;
        int key = tag.hashCode();
        mSubManager.unsubscribe(LIFE_PERIOD.START_STOP, key);
    }

    @Override
    public boolean containSubscriptionOfMain(String tag) {
        return tag != null && mSubManager.contain(LIFE_PERIOD.CREATE_DESTROY, tag.hashCode());
    }

    @Override
    public boolean containSubscriptionOfVisible(String tag) {
        return tag != null && mSubManager.contain(LIFE_PERIOD.START_STOP, tag.hashCode());
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
