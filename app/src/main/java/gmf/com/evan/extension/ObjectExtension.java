package gmf.com.evan.extension;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.lang.ref.WeakReference;

import gmf.com.evan.BuildConfig;
import gmf.com.evan.extension.common.function.SafeAction0;
import gmf.com.evan.extension.common.function.SafeFunc0;


/**
 * Created by Evan on 16/6/15 下午4:29.
 */
public class ObjectExtension {

    public static void safeCall(SafeAction0 operation) {
        try {
            if (operation != null) {
                operation.call();
            }
        } catch (Exception ignored) {
            if (BuildConfig.DEBUG)
                ignored.printStackTrace();
        } catch (Error e) {
            if (BuildConfig.DEBUG)
                e.printStackTrace();
        }
    }

    public static <T> T safeGet(SafeFunc0<T> getFunc, T defValue) {
        try {
            if (getFunc == null) {
                return defValue;
            }
            T ret = getFunc.call();
            if (ret instanceof CharSequence) {
                return TextUtils.isEmpty((CharSequence) ret) ? defValue : ret;
            } else {
                return ret == null ? defValue : ret;
            }
        } catch (NullPointerException ignored) {
            return defValue;
        } catch (ClassCastException ignored) {
            return defValue;
        } catch (NumberFormatException ignored) {
            return defValue;
        } catch (Error e) {
            if (BuildConfig.DEBUG)
                e.printStackTrace();
            return defValue;
        } catch (Exception e) {
            if (BuildConfig.DEBUG)
                e.printStackTrace();
            return defValue;
        }
    }
    public static <T> Optional<T> opt(@Nullable T value) {
        return Optional.of(value);
    }

    public static <T> Optional<T> opt(@Nullable WeakReference<T> value) {
        return Optional.of(value == null ? null : value.get());
    }

    public static <T> Optional<T> opt(@Nullable Optional<T> value) {
        return value;
    }


}
