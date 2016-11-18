package gmf.com.evan.controller.manager;

import android.util.SparseArray;

import rx.Subscription;

/**
 * Created by Evan on 16/6/23 下午6:12.
 */
public class SubscriptionManager {

    public enum LIFE_PERIOD {
        CREATE_DESTROY,
        START_STOP,
    }

    public SparseArray<SparseArray<Subscription>> mSubCompositionArray = new SparseArray<>();

    public SubscriptionManager() {

    }

    public boolean contain(LIFE_PERIOD period, int key) {
        SparseArray<Subscription> array = mSubCompositionArray.get(period.ordinal());
        if (array != null) {
            Subscription sub = array.get(key);
            if (sub != null) {
                return !sub.isUnsubscribed();
            }
        }
        return false;
    }

    public void subscribe(LIFE_PERIOD period, int key, Subscription subscription) {
        SparseArray<Subscription> array = mSubCompositionArray.get(period.ordinal());
        if (array == null) {
            array = new SparseArray<>();
            mSubCompositionArray.append(period.ordinal(), array);
        }

        Subscription cache = array.get(key);
        if (cache != null) {
            cache.unsubscribe();
        }
        array.append(key, subscription);
    }

    public void unsubscribe(LIFE_PERIOD period, int key) {
        SparseArray<Subscription> array = mSubCompositionArray.get(period.ordinal());
        if (array != null) {
            Subscription sub = array.get(key);
            if (sub != null) {
                sub.unsubscribe();
            }
            array.delete(period.ordinal());
        }
    }

    public void unsubscribe(LIFE_PERIOD period) {
        SparseArray<Subscription> array = mSubCompositionArray.get(period.ordinal());
        if (array != null) {
            int size = array.size();
            for (int i = 0; i < size; i++) {
                Subscription sub = array.valueAt(i);
                sub.unsubscribe();
            }
            array.clear();
        }
    }

    public void unsubscribe(){
        int size = mSubCompositionArray.size();
        for (int i=0;i<size;i++){
            SparseArray<Subscription> array = mSubCompositionArray.get(i);
            for (int j=0;j<array.size();j++){
                Subscription sub = array.valueAt(j);
                sub.unsubscribe();
            }
        }
    }
}
