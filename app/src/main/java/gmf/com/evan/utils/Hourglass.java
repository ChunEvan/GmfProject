package gmf.com.evan.utils;

/**
 * Created by Evan on 16/6/13 下午2:44.
 */
public class Hourglass {

    private long time = 0;
    private long lastTime = 0;

    public Hourglass() {

    }

    public void start() {
        if (lastTime == 0)
            lastTime = current();
    }

    public long stop() {
        if (lastTime > 0)
            time = time + (current() - lastTime);
        long retTime = time;
        time = 0;
        lastTime = 0;
        return retTime;

    }

    public long peek() {
        if (lastTime > 0) {
            return time + (current() - lastTime);
        } else {
            return time;
        }
    }

    public static long current() {
        return System.currentTimeMillis();
    }

}
