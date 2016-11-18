package gmf.com.evan.controller.business;

import gmf.com.evan.MyApplication;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;

/**
 * Created by Evan on 16/7/11 下午4:06.
 */
public class NotificationCenter {

    public static PublishSubject<Void> loginSubject = PublishSubject.create();
    public static PublishSubject<Void> needLoginSubject = PublishSubject.create();
    public static PublishSubject<Void> cancelLoginSubject = PublishSubject.create();
    public static PublishSubject<Void> closeOpenSimulationPageSubject = PublishSubject.create();
    public static PublishSubject<Void> closeAuthenticPageSubject = PublishSubject.create();
    public static PublishSubject<Void> closeBindCNCardPageSubject = PublishSubject.create();

    public static PublishSubject<Void> closeEditShippingAddressPageSubject = PublishSubject.create();

    public static void init() {
        needLoginSubject.observeOn(AndroidSchedulers.mainThread())
                .subscribe(nil->{
                    if (!MyApplication.SHARE_INSTANCE.mLoginPageShowing){
                        MyApplication.SHARE_INSTANCE.mLoginPageShowing=true;
                    }
                });

    }
}
