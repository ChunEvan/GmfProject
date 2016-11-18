package gmf.com.evan.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.File;

import gmf.com.evan.extension.ObjectExtension;
import gmf.com.evan.manager.mine.Mine;

/**
 * Created by Evan on 16/7/7 下午3:42.
 */
public class ModelSerialization<T> {

    private static String sModelSerializationKey = "ModelSerialization";
    private T mData;
    public String fileName;

    public ModelSerialization(T data) {
        mData = data;
    }

    public void saveByKey(String key) {
        saveByKey(key, false);
    }

    public void saveByKey(String key, boolean userOnly) {
        if (key == null)
            return;
        ObjectExtension.safeCall(() -> {
            Gson gson = new Gson();
            String data = gson.toJson(mData);
        });
    }

    private static String getNewFileName(String key, String className, boolean userOnly) {
        String userPhone = "";
        if (userOnly) {
            userPhone = getString(Mine.sBackMinePhone);
        }
        return getFileName(FileUtil.getCacheDir(), key, className, userPhone);
    }

    private static String getOldFileName(String key, String className, boolean userOnly) {
        String userPhone = "";
        if (userOnly) {
            userPhone = getString(Mine.sBackMinePhone);
        }
        return getFileName(FileUtil.getCacheDir(), key, className, userPhone);
    }

    private static String getRemoveFileName(String key, String className, boolean userOnly) {
        String userPhone = "";
        if (userOnly) {
            userPhone = getString(Mine.sBackMinePhone);
        }
        return getFileName(FileUtil.getCacheDir(), key, className, userPhone);
    }

    private static String getFileName(String dir, String key, String className, String userPhone) {
        return dir + File.separator + key + "." + userPhone + "." + className;
    }

    public static void saveString(String key, String value) {
        FileUtil.saveValue(sModelSerializationKey, key, value);
    }

    public static String getString(String key) {
        return FileUtil.getValue(sModelSerializationKey, key);
    }

    public void removeByKey(String key) {
        removeByKey(key, false);
    }

    public void removeByKey(String key, boolean uerOnly) {
        removeByKey(key, mData.getClass(), uerOnly);
    }

    public static <T> void removeByKey(String key, Class<T> classOfT, boolean userOnly) {
        String fileName = getRemoveFileName(key, classOfT.getSimpleName(), userOnly);
        FileUtil.removeFile(fileName);
    }

    public static <T> T loadByKey(String key, Class<T> classOfT) {
        return loadByKey(key, classOfT, false);
    }

    public static <T> T loadByKey(String key, Class<T> classOfT, boolean userOnly) {

        if (key == null) {
            return null;
        }

        String loadFile = getNewFileName(key, classOfT.getSimpleName(), userOnly);
        if (!FileUtil.isFileExist(loadFile)) {
            loadFile = getOldFileName(key, classOfT.getSimpleName(), userOnly);
        }

        try {
            byte[] data = FileUtil.readFromFile(loadFile);
            if (data != null) {
                String fileData = new String(data);
                if (fileData.length() > 0) {
                    Gson gson = new Gson();
                    return gson.fromJson(fileData, classOfT);
                }
            }
        } catch (JsonSyntaxException e) {
            return null;
        }
        return null;
    }


    public static JsonElement loadJsonByKey(String key) {
        return loadJsonByKey(key, false);
    }

    public static JsonElement loadJsonByKey(String key, boolean userOnly) {
        try {
            String data = loadByKey(key, String.class, userOnly);
            JsonParser parser = new JsonParser();
            JsonElement obj = parser.parse(data);
            return obj;
        } catch (Exception e) {
            return null;
        }
    }

}
