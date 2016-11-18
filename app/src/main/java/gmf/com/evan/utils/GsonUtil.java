package gmf.com.evan.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Created by Evan on 16/6/13 下午3:13.
 */

public class GsonUtil {

    private interface Transformer<T, R> {
        R onTransform(T in) throws Exception;
    }

    private static <T> T getValue(JsonElement json, T defValue, Transformer<JsonElement, T> transformer, String... keys) {

        try {
            for (String key : keys) {
                json = getAsJsonElement(json, key);
            }
            return transformer.onTransform(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defValue;
    }

    public static JsonElement getAsJsonElement(JsonElement json, String key) {
        return json.getAsJsonObject().get(key);
    }

    public static JsonObject getAsJsonObject(JsonElement json, String... keys) {

        return getValue(json, null, it -> it.getAsJsonObject(), keys);
    }

    public static JsonArray getAsJsonArray(JsonElement json, String... keys) {
        return getValue(json, new JsonArray(), it -> it.getAsJsonArray(), keys);
    }

    public static int getAsInt(JsonElement json, String... keys) {
        return getValue(json, 0, it -> it.getAsInt(), keys);
    }

    public static String getAsNullableString(JsonElement json, String... keys) {
        return getValue(json, null, it -> it.getAsString(), keys);
    }

    public static String getAsString(JsonElement json, String... keys) {
        return getValue(json, "", it -> it.getAsString(), keys);
    }

    public static Double getAsNullableDouble(JsonElement json, String... keys) {
        return getValue(json, null, it -> it.getAsDouble(), keys);
    }

    public static double getAsDouble(JsonElement json, String... keys) {
        return getValue(json, 0.0, it -> it.getAsDouble(), keys);
    }

    public static Long getAsNullableLong(JsonElement json, String... keys) {
        return getValue(json, null, it -> it.getAsLong(), keys);
    }

    public static long getAsLong(JsonElement json, String... keys) {
        return getValue(json, 0L, it -> it.getAsLong(), keys);
    }

    public static boolean getAsBoolean(JsonElement json, String... keys) {
        return getAsInt(json, keys) == 1;
    }

    public static JsonObject getChildAsJsonObjst(JsonElement element, int i) {
        try {
            if (element.getAsJsonArray().size() > 0) {
                JsonElement value = element.getAsJsonArray().getAsJsonArray().get(i);
                if (value.isJsonObject()) {
                    return value.getAsJsonObject();
                }
            }
        } catch (Exception ignored) {

        }
        return null;
    }

    public static int getChildAsInt(JsonElement element, int i) {
        try {
            if (element.getAsJsonArray().size() > 0) {
                JsonElement value = element.getAsJsonArray().getAsJsonArray().get(i);
                if (value.isJsonPrimitive()) {
                    return value.getAsInt();
                }
            }
        } catch (Exception ignored) {

        }
        return 0;
    }

    public static String getChildAsString(JsonElement element, int i) {
        try {
            if (element.getAsJsonArray().size() > 0) {
                JsonElement value = element.getAsJsonArray().getAsJsonArray().get(i);
                if (value.isJsonPrimitive()) {
                    return value.getAsString();
                }
            }
        } catch (Exception ignored) {

        }
        return "";
    }
}
