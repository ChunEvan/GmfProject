package gmf.com.evan.rx;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Evan on 16/7/19 下午2:57.
 */
public class ProxyCompositionSubscription {

    private CompositeSubscription mSubscription;

    public ProxyCompositionSubscription() {
        mSubscription = new CompositeSubscription();
    }

    public static ProxyCompositionSubscription create() {
        return new ProxyCompositionSubscription();
    }

    public void add(Subscription subscription) {
        if (mSubscription != null) {
            mSubscription.add(subscription);
        }
    }

    public void reset() {
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
        mSubscription = new CompositeSubscription();
    }

    public void close() {
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
        mSubscription = null;
    }

}
