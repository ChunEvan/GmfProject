package gmf.com.evan.controller.protocol;


import gmf.com.evan.controller.dialog.ShareDialog;
import gmf.com.evan.manager.common.ShareInfo;

/**
 * Created by Evan on 16/7/19 下午3:46.
 */
public interface UMShareHandlerProtocol {

    void onPerformShare(ShareInfo shareInfo, ShareDialog.SharePlatform[] platforms);

    void onPerformShare(ShareInfo shareInfo, ShareDialog.SharePlatform platform);
}
