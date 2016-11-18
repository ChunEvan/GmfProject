package gmf.com.evan.manager.common;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import gmf.com.evan.utils.GsonUtil;
import gmf.com.evan.utils.ModelSerialization;
import gmf.com.evan.utils.SecondUtil;


/**
 * Created by Evan on 16/7/6 下午8:07.
 */
public class UpdateInfo {

    public static final String sUpdateInfoKey = "updateInfoKey";

    @SerializedName("url")
    public String url;
    @SerializedName("ver")
    public String updateVersion;
    @SerializedName("title")
    public String updateTitle;
    @SerializedName("msg")
    public String updateMsg;
    @SerializedName("md5")
    public String md5;

    public boolean showAlert;
    public boolean needForceUpdate;
    private long minNextRemind;

    public static UpdateInfo translateFromJsonData(JsonObject dic) {
        Gson gson = new Gson();

        try {
            UpdateInfo info = gson.fromJson(dic, UpdateInfo.class);
            info.needForceUpdate = (GsonUtil.getAsInt(dic, "new_type") == 2);
            info.showAlert = true;
            info.minNextRemind = 0;
            return info;
        } catch (Exception ignored) {
            return null;
        }
    }

    public void delayUpdateAlert() {
        this.minNextRemind = SecondUtil.currentSecond() + 24 * 3600;
        this.save();
    }

    transient ModelSerialization<UpdateInfo> mSerialization = new ModelSerialization<>(this);

    public static UpdateInfo loadData() {
        UpdateInfo info = ModelSerialization.loadByKey(sUpdateInfoKey, UpdateInfo.class);
        if (info != null) {
            info.showAlert = SecondUtil.currentSecond() > info.minNextRemind;
        }
        return info;
    }

    public void save() {
        mSerialization.saveByKey(sUpdateInfoKey);
    }

    public void remove() {
        mSerialization.removeByKey(sUpdateInfoKey);
    }

}
