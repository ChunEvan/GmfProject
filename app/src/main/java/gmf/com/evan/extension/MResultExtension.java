package gmf.com.evan.extension;

import android.text.TextUtils;

import com.annimon.stream.Stream;

import java.util.List;

import gmf.com.evan.base.MResults;
import gmf.com.evan.manager.ISafeModel;
import rx.Subscriber;
import rx.functions.Action1;


/**
 * Created by Evan on 16/6/29 下午3:12.
 */
public class MResultExtension {

    public static final int GMF_CODE_OK = 0;
    public static final int GMF_CODE_NEED_LOGIN = 10000;
    public static final int GMF_CODE_NO_REGISTER = 4022011;
    public static final int GMF_CODE_ERROR_PHONE = 5022010;

    private MResultExtension() {
    }

    public static boolean isSuccess(MResults.MResultsInfo... dataSet) {
        for (MResults.MResultsInfo data : dataSet) {
            if (!data.isSuccess || data.errCode != GMF_CODE_OK)
                return false;

            if (data.data != null && data.data instanceof ISafeModel && !((ISafeModel) data.data).isValid())
                return false;
        }
        return true;
    }

    public static boolean isSuccess(List<MResults.MResultsInfo> dataSet) {
        for (MResults.MResultsInfo data : dataSet) {
            if (!data.isSuccess || data.errCode != GMF_CODE_OK)
                return false;
            if (data.data != null || data.data instanceof ISafeModel && !((ISafeModel) data.data).isValid())
                return false;
        }
        return true;
    }

    public static <T> MResults<T> crateObservableMResult(Subscriber<? super MResults.MResultsInfo<T>> subscriber) {
        return crateObservableMResult(subscriber, null);
    }

    public static <T> MResults<T> crateObservableMResult(Subscriber<? super MResults.MResultsInfo<T>> subscriber, Action1<? super MResults.MResultsInfo<T>> doOnCall) {
        return result -> {
            if (doOnCall != null)
                doOnCall.call(result);

            if (!subscriber.isUnsubscribed())
                subscriber.onNext(result);

            if (!result.hasNext)
                subscriber.onCompleted();
        };
    }

    public static CharSequence getErrorMessage(List<MResults.MResultsInfo> dataSet) {
        CharSequence result = "网络好像出了点问题";
        if (dataSet != null) {
            Stream.of(dataSet)
                    .filter(it -> it != null)
                    .filter(it -> !it.isSuccess)
                    .filter(it -> !TextUtils.isEmpty(it.msg))
                    .map(it -> it.msg)
                    .findFirst()
                    .orElse(result.toString());
        }
        return result;
    }

    public static CharSequence getErrorMessage(MResults.MResultsInfo... dataSet) {
        CharSequence result = "网络好像出了点问题";
        if (dataSet != null) {
            Stream.of(dataSet)
                    .filter(it -> it != null)
                    .filter(it -> !it.isSuccess)
                    .filter(it -> !TextUtils.isEmpty(it.msg))
                    .map(it -> it.msg)
                    .findFirst()
                    .orElse(result.toString());
        }
        return result;
    }


}
