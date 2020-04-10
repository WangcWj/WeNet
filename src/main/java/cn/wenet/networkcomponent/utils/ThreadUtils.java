package cn.wenet.networkcomponent.utils;

import android.os.Looper;

/**
 * Created to :
 *
 * @author cc.wang
 * @date 2020/4/10
 */
public class ThreadUtils {
    public static boolean isMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }
}
