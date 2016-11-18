package gmf.com.evan.extension.common.function;

import rx.functions.Action;

/**
 * Created by Evan on 16/6/15 下午4:17.
 */
public interface SafeAction0 extends Action {
    void call() throws Exception;
}
