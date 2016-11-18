package gmf.com.evan.controller.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import gmf.com.evan.MyApplication;
import gmf.com.evan.extension.Optional;
import gmf.com.evan.utils.DownloadUtil;
import gmf.com.evan.utils.FormatUtil;
import rx.functions.Action3;

import static gmf.com.evan.utils.DownloadUtil.createDownloadRequest;

/**
 * Created by Evan on 16/7/8 下午6:06.
 */
public class DownloadDialog extends ProgressDialog {

    private static Map<String, DownloadUtil.DownloadRequest> mBackgroundRequestMap = new HashMap<>();
    private final String mDownloadURL;
    private final File mSavePath;
    private final Optional<String> mMd5;
    private boolean isDownloading = false;
    private Handler mHandler = new Handler();

    private Action3<Dialog, Boolean, File> mFinishDownloadListener = (dialog, aBoolean, file) -> {

    };

    public DownloadDialog(Context context, String downloadURL, File savePath, Optional<String> md5) {
        super(context);
        setCancelable(false);
        setMessage("初始化中，请稍后");

        mDownloadURL = downloadURL;
        mSavePath = savePath;
        mMd5 = md5;
    }

    @Override
    public void show() {
        super.show();
        MyApplication.setTopDialog(this);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        MyApplication.setTopDialog(null);
    }

    public static void downloadOnBackground(String downloadURL, File savePath, String md5, boolean onlyWifi) {
        Context context = MyApplication.SHARE_INSTANCE;
        if (onlyWifi) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (networkInfo.isConnected()) {
                downloadOnBackgroundImpl(downloadURL, savePath, Optional.of(md5));
            }
            return;
        }
        downloadOnBackgroundImpl(downloadURL, savePath, Optional.of(md5));
    }


    private static void downloadOnBackgroundImpl(String downloadURL, File savePath, Optional<String> md5) {

        DownloadUtil.DownloadRequest historyDownloadRequestOrNil = mBackgroundRequestMap.get(downloadURL);
        if (historyDownloadRequestOrNil != null && historyDownloadRequestOrNil.isExecuting())
            return;

        new Thread(() -> {
            DownloadUtil.DownloadRequest request = createDownloadRequest(downloadURL, savePath, md5);
            request.setCompleteListener(isSuccess -> mBackgroundRequestMap.remove(downloadURL));
            mBackgroundRequestMap.put(downloadURL, request);
            request.execute();
        }).start();
    }

    private static void cancelBackgroundDownloadImpl(String downloadURL) {
        DownloadUtil.DownloadRequest historyDownloadRequestOrNil = mBackgroundRequestMap.get(downloadURL);
        if (historyDownloadRequestOrNil != null)
            historyDownloadRequestOrNil.cancel();
        mBackgroundRequestMap.remove(downloadURL);
    }

    public void setFinishDownloadListener(Action3<Dialog, Boolean, File> listener) {
        if (listener != null)
            mFinishDownloadListener = listener;
    }

    public void startDownload() {
        if (!isDownloading) {
            isDownloading = true;
            new Thread(() -> {
                DownloadUtil.DownloadRequest request = createDownloadRequest(mDownloadURL, mSavePath, mMd5);
                request.setProgressListener((currentSize, totalSize) -> mHandler.post(() -> onDownloadProgressChanged(currentSize, totalSize)));
                request.setCompleteListener(isSuccess -> mHandler.post(() -> onFinishDownload(isSuccess)));
                request.execute();
            }).start();
        }
    }

    private void onDownloadProgressChanged(int currentSize, int totalSize) {
        setMessage("正在下载" + FormatUtil.formatRatio((double) (currentSize / totalSize), false, 2, 2));
    }

    private void onFinishDownload(boolean isSuccess) {
        isDownloading = false;
        mFinishDownloadListener.call(this, isSuccess, mSavePath);
    }

}
