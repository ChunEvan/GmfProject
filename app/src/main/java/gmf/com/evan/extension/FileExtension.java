package gmf.com.evan.extension;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Evan on 16/7/8 下午4:15.
 */
public class FileExtension {

    private FileExtension() {
    }

    public static String md5FromFile(File file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] data = readDataOrNilFromFile(file);
            if (data != null) {
                byte[] hash = digest.digest(data);
                StringBuilder hex = new StringBuilder(hash.length * 2);
                for (byte b : hash) {
                    if ((b & 0xFF) < 0x10) hex.append("0");
                    hex.append(Integer.toHexString(b & 0xFF));
                }
                return hex.toString();
            }
        } catch (NoSuchAlgorithmException ignored) {
        }
        return null;
    }

    public static byte[] readDataOrNilFromFile(File file) {
        if (file == null || !file.exists() || file.isDirectory())
            return null;

        BufferedInputStream input = null;
        try {
            input = new BufferedInputStream(new FileInputStream(file));
            int count = input.available();
            byte[] data = new byte[count];
            input.read(data, 0, count);
            input.close();
            input = null;
            return data;
        } catch (Exception ignored) {
        } finally {
            close(input);
        }

        return null;
    }

    private static void close(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException ignored) {
            }
        }
    }
}
