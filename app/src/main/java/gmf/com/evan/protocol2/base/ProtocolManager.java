package gmf.com.evan.protocol2.base;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gmf.com.evan.MyApplication;
import gmf.com.evan.controller.business.NotificationCenter;
import gmf.com.evan.manager.dev.RequestLogManger;
import gmf.com.evan.utils.FileUtil;
import gmf.com.evan.utils.OKHttpUtil;
import gmf.com.evan.utils.SecondUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Evan on 16/6/13 下午2:35.
 */
public class ProtocolManager {

    public final Integer versionProtocol = 9;

    public String appToken = null;
    public String tradeToken = null;
    public String snsToken = null;

    public static String sProtocolMangerKey = "ProtocolManager";
    public static String sAppTokenKey = "app_token";
    public static String sTraderTokenKey = "trade_token";
    public static String sSNSTokenKey = "sns_token";
    public static String mUserAgentOrNil = null;

    private List<ProtocolBase> mLoginProtocolQueue = new ArrayList<>();

    private static ProtocolManager sInstance = new ProtocolManager();

    public static ProtocolManager geInstance() {
        return sInstance;
    }

    private ProtocolManager() {
        {
            String token = FileUtil.getValue(sProtocolMangerKey, sAppTokenKey);
            if (token != null) {
                setAppToken(token);
            }
        }

        {
            String token = FileUtil.getValue(sProtocolMangerKey, sSNSTokenKey);
            if (token != null) {
                setSNSToken(token);
            }
        }

        NotificationCenter.loginSubject.subscribe(user -> {
            if (mLoginProtocolQueue.size() > 0) {
                for (ProtocolBase p : mLoginProtocolQueue) {
                    p.startWork();
                }
            }
        });

        NotificationCenter.cancelLoginSubject.subscribe(nil -> {
            if (mLoginProtocolQueue.size() > 0) {
                for (ProtocolBase p : mLoginProtocolQueue) {
                    ProtocolManager.this.processErr(p.returnCode, null, p);
                }
            }
        });

    }

    public void setAppToken(String token) {
        this.appToken = token;
        OKHttpUtil.addCookie(domain1(), sAppTokenKey, this.appToken);
        FileUtil.saveValue(sProtocolMangerKey, sAppTokenKey, this.appToken);
    }

    public void setTradeToken(String token) {
        this.tradeToken = token;
        OKHttpUtil.addCookie(domain1(), sTraderTokenKey, this.tradeToken);
        FileUtil.saveValue(sProtocolMangerKey, sTraderTokenKey, this.tradeToken);
    }

    public void setSNSToken(String token) {
        this.snsToken = token;
        OKHttpUtil.addCookie(domain2(), sSNSTokenKey, this.snsToken);
        FileUtil.saveValue(sProtocolMangerKey, sSNSTokenKey, this.snsToken);
    }

    public String getSNSToken() {
        return this.snsToken;
    }

    private static final MediaType MEDIA_TYPE_POST = MediaType.parse("application/x-www-form-urlencoded");

    public final Call enqueue(final ProtocolBase protocol) {
        protocol.mHourglass.start();

        String url = protocol.url();
        Map<String, String> params = protocol.getParam();
        if (params == null)
            params = new HashMap<>();

        params.put("format", "json");
        params.put("protocol_ver", versionProtocol.toString());
        params.put("os", "android");
        params.put("platform", "android");
        params.put("time", String.valueOf(SecondUtil.currentSecond()));

        url = OKHttpUtil.attachHttpGetParams(url, params);

        String cmd = "GET";
        RequestBody body = null;
        Map<String, Object> postData = protocol.getPostData();
        if (postData != null) {
            cmd = "POST";
            body = RequestBody.create(MEDIA_TYPE_POST, OKHttpUtil.formatParams(postData).getBytes());
        }

        if (mUserAgentOrNil == null) {
            mUserAgentOrNil = OKHttpUtil.generateUserAgent(MyApplication.SHARE_INSTANCE);
        }

        Request request = new Request.Builder()
                .url(url)
                .method(cmd, body)
                .addHeader("User-Agent", mUserAgentOrNil)
                .build();

        return OKHttpUtil.enqueue(request, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                RequestLogManger.addLog(request, e);
                processErr(ProtocolBase.ErrCode.ERR_HTTP, e, protocol);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String responseMessage = response.body().string();
                JsonElement obj = parseJson(responseMessage);
                RequestLogManger.addLog(request, response, responseMessage);

                MyApplication.post(() -> {

                    if (obj == null) {
                        Logger.d("[protocol]url:{%s}\n,return:{null}", protocol.url());
                        processErr(ProtocolBase.ErrCode.ERR_JSON, null, protocol);
                    } else {
                        Logger.d(Thread.currentThread().toString());
                        Logger.d("[protocol]url:{%s}\n,return:{%s}", protocol.url(), obj.toString());

                        if (protocol.parseJson(obj)) {
                            processSuccess(protocol);
                        } else {
                            if (protocol.returnCode == 10000 && protocol.reStart == false) {
                                ProtocolManager.geInstance().setAppToken("");
                                ProtocolManager.geInstance().setTradeToken("");
                                ProtocolManager.geInstance().setSNSToken("");

                                protocol.reStart = true;
                                mLoginProtocolQueue.add(protocol);
                            } else {
                                processErr(protocol.returnCode, null, protocol);
                            }
                        }
                    }
                });

            }
        });
    }

    private JsonElement parseJson(String data) {
        try {
            JsonParser parser = new JsonParser();
            return parser.parse(data);
        } catch (JsonSyntaxException e) {
            return null;
        }
    }

    private static String domain1() {
        String hostName = CHostName.getHost1();
        try {
            URL url = new URL(hostName);
            return url.getHost();
        } catch (Exception ignored) {

        }
        return "";
    }

    private static String domain2() {
        String hostName = CHostName.getHost2();
        try {
            URL url = new URL(hostName);
            return url.getHost();
        } catch (Exception ignored) {

        }
        return "";
    }

    private final void processErr(int errCode, IOException e, final ProtocolBase protocol) {
        protocol.returnCode = errCode;
        String errInfo = String.format("errCode:%d", errCode);
        if (e != null) {
            errInfo += String.format("e:%s", e.toString());
        }
        if (protocol.returnMsg != null) {
            errInfo += String.format("returnMsg:%s", protocol.returnMsg);
        }
        long costMillis = protocol.mHourglass.stop();
        com.orhanobut.logger.Logger.d("[processErr]url:{%s}\n,costMillis:{%d}\n,errInfo:{%s}", protocol.url(), costMillis, errInfo);

        if (protocol != null && protocol.callback != null) {
            protocol.callback.onFailure(protocol, errCode);
        }
    }

    private final void processSuccess(final ProtocolBase protocol) {

        long costMillis = protocol.mHourglass.stop();
        Logger.d("[processSuccess]url:{%s}\n,costMillis:{%d}\n", protocol.url(), costMillis);

        if (protocol != null && protocol.callback != null) {
            protocol.callback.onSuccess(protocol);
        }
    }

}
