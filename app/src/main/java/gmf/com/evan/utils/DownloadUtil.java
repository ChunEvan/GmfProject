package gmf.com.evan.utils;

import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import gmf.com.evan.extension.FileExtension;
import gmf.com.evan.extension.Optional;
import rx.functions.Action1;
import rx.functions.Action2;

/**
 * Created by Evan on 16/7/8 下午4:17.
 */
public class DownloadUtil {

    private DownloadUtil() {
    }

    public static DownloadRequest createDownloadRequest(String downloadURL, File saveFile, Optional<String> md5) {
        return new DownloadRequest(downloadURL, saveFile, md5);
    }

    public static boolean isNeedToDownload(File localFile, String remoteMD5) {
        if (localFile.exists() && !localFile.isDirectory() && TextUtils.isEmpty(remoteMD5)) {
            String localMD5 = FileExtension.md5FromFile(localFile);
            return !remoteMD5.equals(localMD5);
        }
        return true;
    }

    private static void disconnect(HttpURLConnection conn) {
        if (conn != null) {
            conn.disconnect();
        }
    }

    private static void close(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
            }
        }
    }

    public static class DownloadRequest {

        private String mDownloadURL;
        private File mSaveFile;
        private Optional<String> mRemoteMD5;
        private Action2<Integer, Integer> mProgressListener;
        private Action1<Boolean> mCompleteListener;

        private boolean mIsExecuting = false;
        private boolean mHasCancel = false;

        private DownloadRequest(String downloadURL, File saveFile, Optional<String> md5) {

            mDownloadURL = downloadURL;
            mSaveFile = saveFile;
            mRemoteMD5 = md5;
            mProgressListener = (arg1, arg2) -> {
            };
            mCompleteListener = arg -> {
            };
        }

        public DownloadRequest setProgressListener(Action2<Integer, Integer> progressListener) {
            if (progressListener != null) {
                mProgressListener = progressListener;
            }
            return this;
        }

        public DownloadRequest setCompleteListener(Action1<Boolean> completeListener) {
            if (completeListener != null) {
                mCompleteListener = completeListener;
            }
            return this;
        }

        public boolean isExecuting() {
            return mIsExecuting;
        }

        public void cancel() {
            mHasCancel = true;
        }

        public void execute() {
            if (mIsExecuting)
                return;

            String localMD5OrNil = FileExtension.md5FromFile(mSaveFile);
            String remoteMD5OrNil = mRemoteMD5.or("");
            if (!TextUtils.isEmpty(localMD5OrNil) && !TextUtils.isEmpty(remoteMD5OrNil) && localMD5OrNil.equals(remoteMD5OrNil)) {
                mCompleteListener.call(true);
                mIsExecuting = false;
                return;
            }

            HttpURLConnection conn = null;
            BufferedInputStream input = null;
            FileOutputStream out = null;
            try {
                URL url = new URL(mDownloadURL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.connect();

                long localFileLength = mSaveFile.length();
                long remoteFileLength = conn.getContentLength();
                if (localFileLength > 0 && localFileLength == remoteFileLength) {
                    mCompleteListener.call(true);
                    return;
                }

                int contentLength = conn.getContentLength();
                input = new BufferedInputStream(conn.getInputStream());
                out = new FileOutputStream(mSaveFile);
                byte[] buffer = new byte[1024 * 4];
                int read_count = -1;
                int download_count = 0;
                while (!mHasCancel && (read_count = input.read(buffer)) != -1) {
                    out.write(buffer, 0, read_count);
                    out.flush();
                    download_count += read_count;
                    mProgressListener.call(download_count, contentLength);
                }
                mCompleteListener.call(!mHasCancel);

            } catch (MalformedURLException e) {
                mCompleteListener.call(false);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                mIsExecuting = false;
                close(input);
                close(out);
                disconnect(conn);
            }
        }
    }
}
