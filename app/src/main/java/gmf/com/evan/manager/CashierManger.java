package gmf.com.evan.manager;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.JsonElement;

import gmf.com.evan.base.MResults;
import gmf.com.evan.protocol2.base.CHostName;
import gmf.com.evan.protocol2.base.CommonProtocol;
import gmf.com.evan.utils.GsonUtil;


/**
 * Created by Evan on 16/6/14 上午11:15.
 */
public class CashierManger {







    Handler mHandler = new Handler(Looper.getMainLooper());

    private void queryResult(String rechargeID, int count, int maxCount, final MResults<Integer> results) {
        if (count == 0) {
            MResults.MResultsInfo<Integer> info = MResults.MResultsInfo.FailureComRet();
            info.msg = "系统无法判断上次操作是否成功，请查询后重试";
            MResults.MResultsInfo.safeOnResult(results, info);
        } else {
            queryResult(rechargeID, info -> {
                if (info.data == 0) {
                    int second = Math.min(Math.max(1, 2 ^ (maxCount - count)), 3);
                    mHandler.postDelayed(() -> queryResult(rechargeID, count - 1, maxCount, results), second * 1000L);
                } else if (info.data == 2) {
                    MResults.MResultsInfo.safeOnResult(results, info);
                } else {
                    MResults.MResultsInfo<Integer> info2 = MResults.MResultsInfo.FailureComRet();
                    MResults.MResultsInfo.safeOnResult(results, info2);
                }
            });
        }
    }

    private void queryResult(String rechargeID, MResults<Integer> results) {

        new CommonProtocol.Builder()
                .url(CHostName.HOST1 + "payment/check-pay-order-result")
                .params(CommonProtocol.buildParams("pay_order_id", rechargeID))
                .callback(new CommonProtocol.CommonCallback() {
                    @Override
                    public void onFailure(CommonProtocol protocol, int errCode, String errMsg) {
                        MResults.MResultsInfo.safeOnResult(results, protocol.<Integer>buildRet().setData(-1));
                    }

                    @Override
                    public void onSuccess(CommonProtocol protocol, JsonElement ret) {

                        int status = GsonUtil.getAsInt(ret, "status");
                        MResults.MResultsInfo.safeOnResult(results, protocol.<Integer>buildRet().setData(status));
                    }
                })
                .build()
                .startWork();
    }


}
