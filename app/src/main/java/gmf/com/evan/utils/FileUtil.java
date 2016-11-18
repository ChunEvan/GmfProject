package gmf.com.evan.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Evan on 16/7/22 下午4:37.
 */
public class FileUtil {

    public static Context mContext;

    private FileUtil() {
    }

    public static String getCacheDir() {
        return mContext.getCacheDir().getPath();
    }

    public static String getFileDir() {
        return mContext.getFilesDir().getPath();
    }

    public static boolean isFileExist(String path) {
        File file = new File(path);
        return file.exists();
    }

    public static boolean saveToFile(byte[] data, String file) {
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(file);
            output.write(data);
            output.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(output);
            return false;
        }
    }

    public static byte[] readFromFile(String file) {
        File f = new File(file);
        if (f.isFile()) {
            if (f.exists()) {
                FileInputStream input = null;
                try {
                    input = new FileInputStream(file);
                    int byteCount = input.available();
                    byte[] buffer = new byte[byteCount];
                    input.read(buffer, 0, byteCount);
                    input.close();
                    input = null;
                    return buffer;
                } catch (Exception e) {
                    return null;
                } finally {
                    try {
                        if (input != null)
                            input.close();
                    } catch (IOException e) {
                        return null;
                    }
                }
            }
        }
        return null;
    }

    public static void removeFile(String file) {
        File f = new File(file);
        if (f.isFile() && f.exists())
            f.delete();
    }

    private static void close(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
            }
        }
    }

    public static SharedPreferences getSharePreferences(String name, int mode) {
        return mContext.getSharedPreferences(name, mode);
    }

    public static void saveValue(String name, String key, String value) {
        if (name == null || key == null || value == null)
            return;
        SharedPreferences sp = getSharePreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(name, value);
        editor.commit();
    }

    public static String getValue(String name, String key) {
        SharedPreferences sp = getSharePreferences(name, Context.MODE_PRIVATE);
        return sp.getString(key, null);
    }



}
