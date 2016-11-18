package gmf.com.evan.protocol2.base;

import com.google.gson.JsonElement;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Evan on 16/6/15 上午11:50.
 */
public class CommonProtocol extends ProtocolBase {

    private String mUrl;
    private ParamParse mParams;
    CommonCallback mCommonCallback;
    ProtocolCallback mCallback = new ProtocolCallback() {
        @Override
        public void onFailure(ProtocolBase protocol, int errCode) {
            if (CommonProtocol.this.mCommonCallback != null)
                CommonProtocol.this.mCommonCallback.onFailure(CommonProtocol.this, errCode, protocol.returnMsg);
        }

        @Override
        public void onSuccess(ProtocolBase protocol) {
            if (CommonProtocol.this.mCommonCallback != null)
                CommonProtocol.this.mCommonCallback.onSuccess(CommonProtocol.this, results);

        }
    };


    CommonProtocol() {
        super.callback = mCallback;
    }

    CommonProtocol(ProtocolCallback callback) {
        super(callback);
    }

    private CommonProtocol(Builder builder) {
        super.callback = mCallback;
        mUrl = builder.mUrl;
        mParams = builder.mParams;
        mCommonCallback = builder.mCallback;
    }

    @Override
    protected boolean parseData(JsonElement data) {
        return (data != null && (data.isJsonObject() || data.isJsonArray()) && this.returnCode == 0);
    }

    public static ParamParse buildParams(String... keyOrValue) {

        ParamParse.ParamBuilder builder = new ParamParse.ParamBuilder();

        int i = 0;
        String key = null;
        for (String param : keyOrValue) {
            if (i % 2 == 0) {
                key = param;
            } else {
                String value = param;
                builder.add(key, value);
            }
            i++;
        }
        return builder.build();
    }

    @Override
    protected String getUrl() {
        return mUrl;
    }

    @Override
    protected Map<String, Object> getPostData() {
        return null;
    }

    @Override
    protected Map<String, String> getParam() {
        return mParams == null ? null : mParams.mParams;
    }


    public static class ParamParse {

        Map<String, String> mParams = new HashMap<>();

        ParamParse(Map<String, String> params) {
            mParams.putAll(params);
        }

        ParamParse(ParamBuilder builder) {
            mParams.putAll(builder.mParams);
        }

        public Map<String, String> getParams() {
            return mParams;
        }

        public static class ParamBuilder {
            private Map<String, String> mParams = new HashMap<>();

            public ParamBuilder add(Map<String, String> p) {
                if (p == null)
                    return this;
                mParams.putAll(p);
                return this;
            }

            public ParamBuilder add(String key, String value) {
                if (key == null || value == null)
                    return this;
                mParams.put(key, value);
                return this;
            }

            public ParamBuilder add(String key, int value) {
                if (key == null)
                    return this;
                mParams.put(key, String.valueOf(value));
                return this;
            }

            public ParamBuilder add(String key, double value) {
                if (key == null)
                    return this;
                mParams.put(key, String.valueOf(value));
                return this;
            }

            public ParamBuilder add(String key, long value) {
                if (key == null)
                    return this;
                mParams.put(key, String.valueOf(value));
                return this;
            }

            public ParamBuilder add(String key, boolean value) {
                if (key == null)
                    return this;
                mParams.put(key, value ? "1" : "0");
                return this;
            }

            public ParamParse build() {
                return new ParamParse(this);
            }
        }
    }

    public static class Builder {

        private String mUrl;
        private ParamParse mParams;
        private CommonCallback mCallback;

        public Builder() {
        }

        public Builder url(String url) {
            if (url == null)
                throw new IllegalArgumentException("mUrl==null");
            mUrl = url;
            return this;
        }

        public Builder params(Map<String, String> params) {
            if (params != null) {
                mParams = new ParamParse(params);
            }
            return this;
        }

        public Builder params(ParamParse params) {
            if (params != null) {
                mParams = params;
            }
            return this;
        }

        public Builder params(ParamParse.ParamBuilder params) {
            if (params != null) {
                mParams = new ParamParse(params);
            }
            return this;
        }

        public Builder callback(CommonCallback callback) {
            if (callback != null) {
                mCallback = callback;
            }
            return this;
        }

        public CommonProtocol build() {
            if (mUrl == null)
                throw new IllegalStateException("mUrl==null");
            return new CommonProtocol(this);
        }
    }

    public interface CommonCallback {
        void onFailure(CommonProtocol protocol, int errCode, String errMsg);

        void onSuccess(CommonProtocol protocol, JsonElement ret);
    }
}
