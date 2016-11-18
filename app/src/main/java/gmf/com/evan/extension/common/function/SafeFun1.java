package gmf.com.evan.extension.common.function;

import rx.functions.Function;

/**
 * Created by Evan on 16/6/24 上午11:20.
 */
public interface SafeFun1<T, R> extends Function {
    R call(T arg) throws Exception;
}
