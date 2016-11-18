package gmf.com.evan.extension.common.function;

import rx.functions.Action;

/**
 * Created by Evan on 16/6/24 上午11:16.
 */
public interface SafeAction1<T> extends Action {
    void call(T arg) throws Exception;
}
