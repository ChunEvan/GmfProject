package gmf.com.evan.protocol2.base;

import com.google.gson.JsonElement;

import java.util.Map;

import gmf.com.evan.base.MResults;
import gmf.com.evan.utils.GsonUtil;
import gmf.com.evan.utils.Hourglass;
import okhttp3.Call;


/**
 * Created by Evan on 16/6/13 下午2:36.
 */
public abstract class ProtocolBase {

    public interface ErrCode {
        int SUCCESS = 0;

        int ERR_HTTP = -1;
        int ERR_JSON = -2;
        int ERR_UNKNOWN = -3;

    }

    public ProtocolCallback callback = null;
    public Hourglass mHourglass = new Hourglass();
    boolean reStart = false;

    public int returnCode = 0;
    public String returnMsg = "";
    public JsonElement results = null;

    public ProtocolBase() {
    }

    public ProtocolBase(ProtocolCallback callback) {
        this.callback = callback;
    }

    public Call startWork() {
        return ProtocolManager.geInstance().enqueue(this);
    }

    public String url() {
        String subUrl = this.getUrl();
        return CHostName.formatUrl(subUrl);
    }

    public boolean parseJson(JsonElement data) {
        if (data != null && data.isJsonObject()) {

            returnCode = GsonUtil.getAsInt(data, "code");
            returnMsg = GsonUtil.getAsString(data, "msg");

            if (returnCode == 0) {
                this.results = data.getAsJsonObject().get("data");
                if (this.results != null) {
                    return this.parseData(this.results);
                }
            }
            return (returnCode == 0);

        } else if (data != null && data.isJsonArray()) {
            this.returnCode = 0;
            this.returnMsg = "";
            this.results = GsonUtil.getAsJsonArray(data);
            return true;
        } else {
            return this.parseData(data);
        }
    }

    public static <T> MResults.MResultsInfo<T> builder(int errCode, String errMsg) {
        MResults.MResultsInfo<T> info = new MResults.MResultsInfo<>();
        info.isSuccess = false;
        info.errCode = errCode;
        info.msg = errMsg;
        info.data = null;
        return info;
    }

    public <T> MResults.MResultsInfo<T> buildRet() {
        MResults.MResultsInfo<T> info = new MResults.MResultsInfo<>();
        info.ret = this.results;
        info.isSuccess = (this.returnCode == 0);
        info.errCode = returnCode;
        info.msg = returnMsg;
        info.data = null;
        return info;
    }

    protected abstract boolean parseData(JsonElement data);

    protected abstract String getUrl();

    protected abstract Map<String, Object> getPostData();

    protected abstract Map<String, String> getParam();

}
