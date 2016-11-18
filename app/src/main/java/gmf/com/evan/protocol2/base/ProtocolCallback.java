package gmf.com.evan.protocol2.base;

/**
 * Created by Evan on 16/6/13 下午2:40.
 */
public interface ProtocolCallback {

    void onFailure(ProtocolBase protocol, int errCode);

    void onSuccess(ProtocolBase protocol);
}
