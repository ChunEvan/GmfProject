package gmf.com.evan.extension.common.function;

import java.util.concurrent.Callable;

import rx.functions.Function;


/**
 * Created by Evan on 16/6/24 上午11:17.
 */
public interface SafeFunc0<R> extends Function, Callable<R> {
    @Override
    R call() throws Exception;

}
