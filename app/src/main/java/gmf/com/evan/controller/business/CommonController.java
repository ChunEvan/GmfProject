package gmf.com.evan.controller.business;

import gmf.com.evan.base.MResults;
import gmf.com.evan.extension.MResultExtension;
import gmf.com.evan.manager.common.CommonManager;
import gmf.com.evan.manager.common.UpdateInfo;
import rx.Observable;

/**
 * Created by Evan on 16/7/8 下午3:04.
 */
public class CommonController {

    private CommonController() {
    }


    public static Observable<MResults.MResultsInfo<UpdateInfo>> checkUpdate() {
        return Observable.create(sub -> CommonManager.getInstance().getNewVersion(MResultExtension.crateObservableMResult(sub)));
    }
}
