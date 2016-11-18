package gmf.com.evan.manager.common;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gmf.com.evan.MyApplication;
import gmf.com.evan.base.MResults;
import gmf.com.evan.controller.business.NotificationCenter;
import gmf.com.evan.manager.chshier.BankCard;
import gmf.com.evan.protocol2.base.CHostName;
import gmf.com.evan.protocol2.base.CommonProtocol;
import gmf.com.evan.utils.AppUtil;
import gmf.com.evan.utils.GsonUtil;
import gmf.com.evan.utils.ModelSerialization;
import gmf.com.evan.utils.SecondUtil;
import gmf.com.evan.utils.VersionUtil;

/**
 * Created by Evan on 16/7/6 下午7:59.
 */
public class CommonManager {

    public final static String Config_Key_HK_Bank = "hk_bank";   //公司的hk银行信息，内容为BankCard
    private static String sCommonManagerCitysKey = "CommonManagerCitysKey";
    private static String sCommonManagerConfigsKey = "CommonManagerConfigsKey";
    private static String sCommonManagerQuestionsKey = "CommonManagerQuestionsKey";
    private static String sCommonManagerWhiteList = "CommonManagerWhiteList";
    private static String sCommonMessageManagerLoadingData = "CommonMessageManagerLoadingData";

    private String bountyCardFirst = "";
    private String currentVersion;
    private UpdateInfo mUpdateInfo;
    private JsonArray mQuestions;
    private ArrayList<String> whiteList = new ArrayList<>();
    private Map<String, Object> configs = new HashMap<>();

    private static CommonManager commonManager = new CommonManager();

    public static CommonManager getInstance() {
        return commonManager;
    }

    private CommonManager() {
        this.currentVersion = AppUtil.getVersionName(MyApplication.SHARE_INSTANCE);
        this.loadLocalData();
        NotificationCenter.loginSubject.subscribe(nil -> {
            CommonManager.this.freshRedPoint(null);
        });
    }

    private void loadLocalData() {
        {
            JsonArray array = ModelSerialization.loadByKey(sCommonManagerCitysKey, JsonArray.class);
            if (array != null) {
                CommonManager.this.loadCity(array);
            }
        }

        {
            JsonElement ret = ModelSerialization.loadByKey(sCommonManagerConfigsKey, JsonElement.class);
            if (ret != null)
                CommonManager.this.loadConfig(ret);
        }

        {
            this.mUpdateInfo = UpdateInfo.loadData();
            if (this.mUpdateInfo != null) {
                if (VersionUtil.isBigger(this.mUpdateInfo.updateVersion, this.currentVersion)) {

                } else {
                    this.mUpdateInfo.remove();
                    this.mUpdateInfo = null;
                }
            }
        }

        {
            this.mQuestions = ModelSerialization.loadByKey(sCommonManagerQuestionsKey, JsonArray.class);
        }

        {
            String[] dataSet = ModelSerialization.loadByKey(sCommonManagerWhiteList, String[].class);
            if (dataSet != null) {
                whiteList.addAll(Arrays.asList(dataSet));
            }
        }

        {
            JsonElement data = ModelSerialization.loadJsonByKey(sCommonMessageManagerLoadingData, false);
            if (data != null && data.isJsonObject()) {
                loadLoadingData();
            }
        }

    }

    private void loadCity(JsonArray array) {

    }

    private void freshRedPoint(final MResults<Void> results) {

    }

    private void loadLoadingData() {

    }

    public final void delayUpdateAlert() {
        if (mUpdateInfo != null) {
            this.mUpdateInfo.delayUpdateAlert();
            this.mUpdateInfo.save();
        }
    }

    public final List<String> getWhiteList() {
        return this.whiteList;
    }

    public void freshCommonInfo() {
        this.freshConfig();
        this.getNewVersion(null);

    }

    private void freshConfig() {
        new CommonProtocol.Builder()
                .url(CHostName.HOST1 + "init-config")
                .callback(new CommonProtocol.CommonCallback() {
                    @Override
                    public void onFailure(CommonProtocol protocol, int errCode, String errMsg) {

                    }

                    @Override
                    public void onSuccess(CommonProtocol protocol, JsonElement ret) {
                        CommonManager.this.loadConfig(ret);
                        long serverTime = GsonUtil.getAsLong(ret, "server_time");
                        if (serverTime != 0) {
                            SecondUtil.setServerTime(serverTime);
                        }

                        ModelSerialization<JsonElement> mSerialization = new ModelSerialization<>(ret);
                        mSerialization.saveByKey(sCommonManagerConfigsKey);
                    }
                })
                .build()
                .startWork();

    }

    private void loadConfig(JsonElement ret) {
        if (ret.isJsonObject()) {
            JsonObject hkconfig = GsonUtil.getAsJsonObject(ret, "foreign_bank", "cmb_hk");
            if (hkconfig != null) {
                BankCard card = BankCard.buildBankCard(hkconfig);
                CommonManager.this.configs.put(Config_Key_HK_Bank, card);

            }
            CommonManager.this.bountyCardFirst = GsonUtil.getAsString(ret, "bounty_card_first");
        }
    }

    public void getNewVersion(final MResults<UpdateInfo> results) {

        new CommonProtocol.Builder()
                .url(CHostName.HOST1 + "mobile-boot?cmd=update")
                .params(CommonProtocol.buildParams("os", "android", "version", currentVersion))
                .callback(new CommonProtocol.CommonCallback() {
                    @Override
                    public void onFailure(CommonProtocol protocol, int errCode, String errMsg) {
                        MResults.MResultsInfo.safeOnResult(results, protocol.buildRet());
                    }

                    @Override
                    public void onSuccess(CommonProtocol protocol, JsonElement ret) {

                        if (ret.isJsonObject()) {
                            UpdateInfo newUpdateInfo = UpdateInfo.translateFromJsonData(ret.getAsJsonObject());
                            String localVersion = (CommonManager.this.mUpdateInfo == null )? "" : mUpdateInfo.updateVersion;
                            if (newUpdateInfo != null && VersionUtil.isBigger(newUpdateInfo.updateVersion, CommonManager.this.currentVersion)) {
                                if (VersionUtil.isBigger(newUpdateInfo.updateVersion, localVersion)) {
                                    CommonManager.this.mUpdateInfo = newUpdateInfo;
                                } else {
                                    CommonManager.this.mUpdateInfo.needForceUpdate = newUpdateInfo.needForceUpdate;
                                }
                            }
                            if (CommonManager.this.mUpdateInfo != null) {
                                CommonManager.this.mUpdateInfo.save();
                            }
                        }

                        MResults.MResultsInfo<UpdateInfo> info = protocol.buildRet();
                        info.data = CommonManager.this.mUpdateInfo;
                        MResults.MResultsInfo.safeOnResult(results, info);
                    }
                })
                .build()
                .startWork();
    }
}
