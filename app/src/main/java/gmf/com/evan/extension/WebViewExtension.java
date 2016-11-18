package gmf.com.evan.extension;

import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import gmf.com.evan.MyApplication;
import gmf.com.evan.base.StringPair;
import gmf.com.evan.protocol2.base.ProtocolManager;

import static gmf.com.evan.extension.ObjectExtension.opt;
import static gmf.com.evan.extension.ObjectExtension.safeCall;

/**
 * Created by Evan on 16/7/19 下午9:03.
 */
public class WebViewExtension {

    private WebViewExtension() {
    }

    public static void syncCookiesImmediately(String urlStr) {
        safeCall(() -> {

            MyApplication application = MyApplication.SHARE_INSTANCE;
            List<StringPair> cookies = new LinkedList<>();
            cookies.add(StringPair.create(ProtocolManager.sAppTokenKey, opt(ProtocolManager.geInstance().appToken).or("")));
            cookies.add(StringPair.create(ProtocolManager.sSNSTokenKey, opt(ProtocolManager.geInstance().getSNSToken()).or("")));
            if (CookieSyncManager.getInstance() == null) {
                CookieSyncManager.createInstance(application);
            }
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);

            URL url = new URL(urlStr);
            String domain = url.getProtocol() + "://" + url.getHost() + ":" + url.getPort();
            for (StringPair cookie : cookies) {
                cookieManager.setCookie(domain, cookie.first + "=" + cookie.second + ";");
            }
            CookieSyncManager.getInstance().sync();
        });
    }

    public static void removeCookiesImmediately() {
        safeCall(() -> {
            MyApplication application = MyApplication.SHARE_INSTANCE;
            if (CookieSyncManager.getInstance() == null) {
                CookieSyncManager.createInstance(application);
            }
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            CookieSyncManager.getInstance().sync();
        });
    }

}
