package gmf.com.evan.controller.protocol;

import rx.Subscription;

/**
 * Created by Evan on 16/6/23 下午6:04.
 */
public interface RXViewControllerProtocol {

    void addSubscriptionToMain(String tag, Subscription subscription);

    void addSubscriptionToVisible(String tag, Subscription subscription);

    void unSubscribeFromMain(String tag);

    void unSubscribeFromVisible(String tag);

    boolean containSubscriptionOfMain(String tag);

    boolean containSubscriptionOfVisible(String tag);
}
