package gmf.com.evan.base;

import com.google.gson.JsonElement;

/**
 * Created by Evan on 16/6/14 上午10:03.
 */
public interface MResults<T> {

    void onResult(MResultsInfo<T> result);


    class MResultsInfo<T> {

        public boolean isSuccess;
        public JsonElement ret;
        public T data;
        public int errCode;
        public String msg;
        public boolean isFromCache;
        public boolean hasNext;

        public MResultsInfo() {
            isSuccess = true;
            data = null;
            errCode = 0;
            msg = null;
            isFromCache = false;
            hasNext=false;
        }

        public MResultsInfo<T> setData(T data) {
            this.data = data;
            return this;
        }

        public MResultsInfo<T> setIsSuccess(boolean isSuccess) {
            this.isSuccess = isSuccess;
            return this;
        }

        public MResultsInfo<T> setHasSuccess(boolean hasNext){
            this.hasNext=hasNext;
            return this;
        }

        public static <T> void safeOnResult(MResults<T> results, MResultsInfo<T> result) {
            if (results != null) {
                results.onResult(result);
            }
        }

        public static void COPY_ALL(MResultsInfo to, MResultsInfo from) {
            to.ret = from.ret;
            to.isSuccess = from.isSuccess;
            to.errCode = from.errCode;
            to.msg = from.msg;
            to.isFromCache = from.isFromCache;
        }

        public static <T> MResultsInfo<T> SuccessComRet() {
            MResultsInfo<T> info = new MResultsInfo<>();
            return info;
        }

        public static <T> MResultsInfo<T> FailureComRet() {
            MResultsInfo<T> info = new MResultsInfo<>();
            info.isSuccess = false;
            return info;
        }

        public static boolean isSuccess(MResultsInfo ret) {
            return ret != null && ret.isSuccess;
        }
    }
}
