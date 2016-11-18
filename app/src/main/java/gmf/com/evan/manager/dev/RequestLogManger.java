package gmf.com.evan.manager.dev;

import android.text.TextUtils;

import java.util.LinkedList;
import java.util.List;

import gmf.com.evan.extension.ObjectExtension;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Evan on 16/6/15 下午3:51.
 */
public class RequestLogManger {


    private static LinkedList<RequestLog> sDataSet = new LinkedList<>();

    private RequestLogManger() {
    }

    public static void addLog(Request request, Response response, String responseMessage) {
        if (request != null && response != null) {
            ObjectExtension.safeCall(() -> addLog(request.url(), request.method(), buildRequestMessage(request), responseMessage, response.code()));
        }
    }

    public static void addLog(Request request, Exception exception) {
        if (request != null && exception != null) {
            ObjectExtension.safeCall(() -> addLog(request.url(), request.method(), buildRequestMessage(request), buildResponseMessage(exception), -1));
        }
    }

    public static void addLog(HttpUrl url, String method, String requestMessage, String responseMessage, int responseCode) {
        sDataSet.add(0, new RequestLog(url, method, requestMessage, responseMessage, responseCode));
    }

    private static String buildRequestMessage(Request request) {
        HttpUrl url = request.url();
        StringBuilder requestMessage = new StringBuilder("");
        if (!TextUtils.isEmpty(url.query())) {
            requestMessage.append(request.method()).append(" ").append(url.encodedPath()).append("?").append(url.query()).append("\n");
        } else {
            requestMessage.append(request.method()).append(" ").append(url.encodedPath()).append("\n");
        }

        requestMessage.append("Host: ").append(url.host());
        if (url.port() != -1) {
            requestMessage.append(":").append(url.port());
        }
        requestMessage.append("\n");
        if (request.headers() != null && request.headers().size() > 0) {
            requestMessage.append("Headers:\n");
            requestMessage.append(request.headers().toString());
        }
        if (request.body() != null) {
            requestMessage.append("Body:\n");
            requestMessage.append(request.body().toString());
        }
        return requestMessage.toString();
    }

    private static String buildResponseMessage(Exception e) {
        StringBuilder sb = new StringBuilder();
        if (e.getStackTrace() != null) {
            for (StackTraceElement element : e.getStackTrace()) {
                sb.append(element.toString()).append("\n");
            }
        }
        return sb.toString();
    }

    public static List<RequestLog> getLogs() {
        return sDataSet;
    }

    public static void clean() {
        sDataSet.clear();
    }

    public static class RequestLog {

        private HttpUrl mUrl;
        private String mMethod;
        private String mRequestMessage;
        private String mResponseMessage;
        private int mResponseCode;
        private long mResponseTimeMillis;

        public RequestLog(HttpUrl url, String method, String requestMessage, String responseMessage, int responseCode) {

            mUrl = url;
            mMethod = method;
            mRequestMessage = requestMessage;
            mResponseMessage = responseMessage;
            mResponseCode = responseCode;
            mResponseTimeMillis = System.currentTimeMillis();
        }

    }
}
