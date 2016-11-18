package gmf.com.evan.protocol2.base;

import java.net.URL;

import gmf.com.evan.MyConfig;

/**
 * Created by Evan on 16/6/15 下午2:56.
 */
public class CHostName {

    public static String HOST1 = "+host1+";
    public static String HOST2 = "+host2+";

    public static String getHost1() {
        return MyConfig.CURRENT_HOST_NAME.first;
    }

    public static String getHost2() {
        return MyConfig.CURRENT_HOST_NAME.second;
    }

    public static String formatUrl(String cgiUrl) {
        if (cgiUrl.contains(HOST1)) {
            return replaceHost(cgiUrl, HOST1, CHostName.getHost1());
        } else if (cgiUrl.contains(HOST2)) {
            return replaceHost(cgiUrl, HOST2, CHostName.getHost2());
        } else {
            if (isUrl(cgiUrl)) {
                return cgiUrl;
            } else {
                return formatUrl(HOST1 + cgiUrl);
            }
        }
    }

    private static String replaceHost(String cgiUrl, String hostKey, String hostValue) {

        int index = cgiUrl.indexOf(hostKey) + hostKey.length();
        if (cgiUrl.charAt(index) != '/')
            return cgiUrl.replace(hostKey, hostValue + "/");
        else
            return cgiUrl.replace(hostKey, hostValue);

    }

    private static boolean isUrl(String cgiUrl) {
        try {
            URL url = new URL(cgiUrl);
            if (url.getProtocol().length() > 0)
                return true;
        } catch (Exception ignored) {

        }
        return false;
    }

    public static String formatUrl(String host, String cgiUrl) {
        return CHostName.formatUrl(host + cgiUrl);
    }

}
