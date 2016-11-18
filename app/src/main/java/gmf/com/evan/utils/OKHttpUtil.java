package gmf.com.evan.utils;

import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import gmf.com.evan.MyApplication;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.Util;


/**
 * Created by Evan on 16/6/13 上午9:46.
 */
public class OKHttpUtil {

    private static final SetCookieCache mCookieCache = new SetCookieCache();
    private static final OkHttpClient mOkHttpClient = new OkHttpClient().newBuilder()
            .connectTimeout(2000, TimeUnit.MILLISECONDS)
            .dispatcher(new Dispatcher(new ThreadPoolExecutor(2, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
                    new LinkedBlockingDeque<Runnable>(), Util.threadFactory("OkHttp-my-Dipatcher", false))))
            .cookieJar(new PersistentCookieJar(mCookieCache, new SharedPrefsCookiePersistor(MyApplication.SHARE_INSTANCE)))
            .build();
    //
    //    static {
    //        CookieManager cookieManager = new CookieManager();
    //        CookieHandler.setDefault(cookieManager);
    //        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
    //        mOkHttpClient.setCookieHandler(cookieManager);
    //        mOkHttpClient.setDispatcher(new Dispatcher(new ThreadPoolExecutor(2, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
    //                new LinkedBlockingDeque<Runnable>(), Util.threadFactory("OkHttp-my-Dispatcher", false))));
    //        mOkHttpClient.setConnectTimeout(2000, TimeUnit.MILLISECONDS);
    //    }

    private static final String CHARSET_NAME = "UTF-8";

    public static String generateUserAgent(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager vm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        vm.getDefaultDisplay().getMetrics(metrics);
        String scale = new BigDecimal(metrics.densityDpi / 160).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
        String deviceVersion = Build.VERSION.SDK_INT + "";
        return "GoldMoreFund/" + AppUtil.getVersionName(context) + Build.DEVICE + "; Android " + deviceVersion + "; Scale" + scale + ")";
    }

    public static String formatParams(Map<String, Object> params) {
        LinkedList<BasicNameValuePair> temp = new LinkedList<>();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getValue() instanceof String) {
                temp.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
            } else if (entry.getValue() instanceof Number) {
                temp.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
            } else if (entry.getValue() instanceof ArrayList) {
                try {
                    ArrayList<String> list = (ArrayList<String>) entry.getValue();
                    if (list.size() > 0) {
                        for (String value : list) {
                            temp.add(new BasicNameValuePair(entry.getKey(), value));
                        }
                    }
                } catch (Exception ignored) {

                }
            }
        }
        return URLEncodedUtils.format(temp, CHARSET_NAME);
    }

    public static String formatStringParams(Map<String, String> params) {
        LinkedList<BasicNameValuePair> temp = new LinkedList<>();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            temp.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        return URLEncodedUtils.format(temp, CHARSET_NAME);
    }

    public static String attachHttpGetParams(String url, Map<String, String> params) {
        if (url.indexOf("?") == -1)
            return url + "?" + formatStringParams(params);
        else
            return url + "&" + formatStringParams(params);
    }

    public static String attachHttpGetParam(String url, String name, String value) {
        if (url.indexOf("?") == -1)
            return url + "?" + name + "=" + value;
        else
            return url + "&" + name + "=value";
    }

    public static Call enqueue(Request request, Callback responseCallback) {
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(responseCallback);
        return call;
    }

    public static Response enqueue_sync(Request request) throws IOException {
        return mOkHttpClient.newCall(request).execute();
    }


    public static void addCookie(String domain, String name, String value) {
        //        HttpCookie cookie = new HttpCookie(name, value);
        //        cookie.setDomain(domain);
        //        cookie.setPath("/");
        //        cookie.setMaxAge(7200);
        //        cookie.setVersion(0);
        //
        //        CookieManager manager = (CookieManager) mOkHttpClient.getCookieHandler();
        //        manager.getCookieStore().add(URI.create(domain), cookie);
        mCookieCache.addAll(Collections.singleton(new Cookie.Builder().name(name).value(value).domain(domain).path("/").build()));
    }

}
