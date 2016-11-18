package gmf.com.evan.protocol2;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

import gmf.com.evan.protocol2.base.ProtocolBase;
import gmf.com.evan.protocol2.base.ProtocolCallback;
import gmf.com.evan.utils.GsonUtil;

/**
 * Created by Evan on 16/6/16 下午3:36.
 */
public class RechargeProtocol extends ProtocolBase {


    public double amount;
    public int fundID;
    public String orderID;
    public String verifyCode;
    private String rechargeID;
    private String ticket;

    public RechargeProtocol(ProtocolCallback callback) {
        super(callback);
    }

    public String getRechargeID() {
        return rechargeID;
    }

    private boolean bInvest() {
        return fundID != 0;
    }

    @Override
    protected boolean parseData(JsonElement data) {
        if (super.returnCode == 0 && data.isJsonObject()) {
            JsonObject dicData = data.getAsJsonObject();
            if (this.ticket == null) {
                this.ticket = GsonUtil.getAsString(dicData, "ticket");
                this.orderID = GsonUtil.getAsString(dicData, "order_id");

            }
        }
        return returnCode == 0;
    }

    @Override
    protected String getUrl() {
        return null;
    }

    @Override
    protected Map<String, Object> getPostData() {
        return null;
    }

    @Override
    protected Map<String, String> getParam() {
        return null;
    }
}
