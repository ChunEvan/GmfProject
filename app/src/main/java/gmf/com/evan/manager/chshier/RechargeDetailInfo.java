package gmf.com.evan.manager.chshier;

import com.google.gson.JsonObject;

import java.io.Serializable;

import gmf.com.evan.utils.GsonUtil;


/**
 * Created by Evan on 16/6/16 下午7:23.
 */
public class RechargeDetailInfo {

    public String orderID;
    public double totalAmount;
    public double finishAmount;
    public boolean multiple;
    public int totalCount;
    public int currentCount;
    public double currentRechargeAmount;
    public double cashBalance;
    public String depositTips;
    public PayAction payAction;

    private void readFromJsonData(JsonObject dic) {

        orderID = GsonUtil.getAsString(dic, "order_id");
        totalAmount = GsonUtil.getAsDouble(dic, "total_amount");
        finishAmount = GsonUtil.getAsDouble(dic, "finish_amount");
        multiple = GsonUtil.getAsBoolean(dic, "multiple_type");
        totalCount = GsonUtil.getAsInt(dic, "total_count");
        JsonObject current = GsonUtil.getAsJsonObject(dic, "current");
        currentCount = GsonUtil.getAsInt(dic, "current");
        currentRechargeAmount = GsonUtil.getAsDouble(current, "deposit_amount");
        cashBalance = GsonUtil.getAsDouble(current, "cash_balance");
        payAction = PayAction.translateFromJsonData(current);
        depositTips = GsonUtil.getAsString(current, "deposit_tips");
    }

    public final boolean isFinish() {
        return currentCount >= this.totalCount;
    }

    public static RechargeDetailInfo translateFromJsonData(JsonObject dic) {

        try {
            RechargeDetailInfo info = new RechargeDetailInfo();
            info.readFromJsonData(dic);
            return info;
        } catch (Exception ignored) {
            return null;
        }
    }

    public static class PayAction implements Serializable {
        public String payChannel;
        public String fuyouUrl;

        public static PayAction translateFromJsonData(JsonObject dic) {

            try {
                PayAction action = new PayAction();
                action.payChannel = getAsPayChannelString(dic, "pay_channel");
                action.fuyouUrl = GsonUtil.getAsString(dic, "deposit_url");
                return action;
            } catch (Exception ignored) {
                return null;
            }
        }

        public static String getAsPayChannelString(JsonObject dic, String key) {
            String string = GsonUtil.getAsString(dic, key);
            if (string.equals(""))
                return "";
            else if (string.equals(""))
                return "";
            else
                return "";
        }

    }


}
