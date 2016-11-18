package gmf.com.evan.extension;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.Serializable;

import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Evan on 16/6/24 上午11:42.
 */
public class Optional<T> implements Serializable {


    private T mArg;

    public Optional(T arg) {
        this.mArg = arg;
    }

    public static <T> Optional<T> of(T arg) {
        return new Optional<>(arg);
    }

    public static <T> Optional<T> empty() {
        return new Optional<>(null);
    }

    public boolean isPresent() {
        return mArg != null;
    }

    public boolean isAbsent() {
        return mArg == null;
    }

    public Optional<T> apply(Action1<T> func) {
        if (isPresent()) {
            func.call(mArg);
        }
        return this;
    }

    public void consume(Action1<T> func) {
        if (isPresent()) {
            func.call(mArg);
        }
    }

    public <R> Optional<R> let(Func1<T, R> func) {
        if (this.isAbsent()) {
            return (Optional<R>) this;
        } else {
            return Optional.of(func.call(this.get()));
        }
    }

    public void set(T arg) {
        mArg = arg;
    }

    public T get() {
        return mArg;
    }

    public T or(T optional) {
        if (mArg instanceof CharSequence) {
            return TextUtils.isEmpty((CharSequence) mArg) ? optional : mArg;
        }
        return mArg == null ? optional : mArg;
    }

    @Nullable
    public T orNull() {
        return mArg;
    }

    public Optional<T> reserveIf(Func1<T, Boolean> booleanExp) {
        if (mArg != null && !booleanExp.call(mArg)) {
            mArg = null;
        }
        return this;
    }

    public <R> Optional<R> cast(Class<R> clazz) {
        if (mArg != null && clazz.isInstance(mArg)) {
            return Optional.of((R) mArg);
        } else {
            mArg = null;
            return (Optional<R>) this;
        }
    }
}
