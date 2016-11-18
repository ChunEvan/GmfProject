package gmf.com.evan.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import gmf.com.evan.protocol2.base.ProtocolManager;
import gmf.com.evan.utils.OKHttpUtil;


/**
 * Created by Evan on 16/7/16 下午2:50.
 */
public class GMFWebView extends WebView {

    private static String USER_AGENT;
    private HashMap<String, InternalCallback> mCallbackMap = new HashMap<>();

    public GMFWebView(Context context) {
        this(context, null);
    }

    public GMFWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GMFWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFocusable(false);
        setFocusableInTouchMode(false);
        if (!isInEditMode()) {
            WebSettings settings = getSettings();
            settings.setDomStorageEnabled(true);
            settings.setJavaScriptEnabled(true);
            settings.setUserAgentString(getUserAgent(context));
            settings.setUseWideViewPort(true);
        }
    }

    private String getUserAgent(Context context) {
        if (USER_AGENT != null) {
            USER_AGENT = getSettings().getUserAgentString() + " " + OKHttpUtil.generateUserAgent(context);
        }
        return USER_AGENT;
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mCallbackMap.clear();
    }

    @Override
    protected int computeHorizontalScrollRange() {
        return super.computeHorizontalScrollRange();
    }

    @Override
    protected int computeVerticalScrollRange() {
        return super.computeVerticalScrollRange();
    }

    @Override
    public boolean onCheckIsTextEditor() {
        return true;
    }

    @Override
    public void loadUrl(String url) {
        super.loadUrl(decorateRequestURL(url));
    }

    @Override
    public void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
        super.loadUrl(decorateRequestURL(url), additionalHttpHeaders);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        if (mode == MeasureSpec.UNSPECIFIED && getMinimumHeight() > 0) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(getMinimumHeight(), MeasureSpec.AT_MOST);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private static String decorateRequestURL(String requestURL) {
        try {
            URL url = new URL(requestURL);
            if (url.getQuery() != null) {
                return requestURL + "?platform=android&os=android&protocol_ver=" + ProtocolManager.geInstance().versionProtocol;
            } else {
                return requestURL + "&platform=android&os=android&protocol_ver=" + ProtocolManager.geInstance().versionProtocol;
            }
        } catch (MalformedURLException e) {
            return requestURL;
        }
    }

    public void callJS(String methodName, Object... value) {
        callJS(null, methodName, value);
    }


    public void callJS(ResultCallback callback, String methodName, Object... value) {
        JSFunctionBuilder builder = new JSFunctionBuilder(methodName, callback != null);
        for (Object object : value) {
            builder.addParam(object);
        }
        JSFunction function = builder.build();
        if (callback != null) {
            mCallbackMap.put("" + function.id, callback);
        }
        loadUrl(function.script);
    }

    private static abstract class InternalCallback {
        abstract void onCallback(String callerId, String receiveValueOrNil);

        protected abstract void onCallback(String receiveValueOrNil);
    }

    private static abstract class ResultCallback extends InternalCallback {
        @Override
        void onCallback(String callerId, String receiveValueOrNil) {
            onCallback(receiveValueOrNil);
        }
    }

    private static class JSFunctionBuilder {

        private static int COUNTER = 1;
        private StringBuilder mContent;
        private int mParamCount = 0;
        private int mFunctionId = -1;

        public JSFunctionBuilder(String name, boolean hasReturnValue) {
            mContent = new StringBuilder("javascript:" + name + "(");
            if (hasReturnValue) {
                mFunctionId = COUNTER++;
                addParam(mFunctionId);
            }
        }

        public JSFunctionBuilder addParam(Object value) {
            if (mParamCount > 0) {
                mContent.append(",");
            }

            if (value instanceof String) {
                mContent.append("'").append(value).append("'");
            } else {
                mContent.append(value);
            }
            mParamCount++;
            return this;
        }

        public JSFunction build() {
            return new JSFunction(mFunctionId, mContent.toString() + ")");
        }

    }

    private static class JSFunction {
        private final int id;
        private final String script;

        JSFunction(int id, String script) {
            this.id = id;
            this.script = script;
        }

    }


}
