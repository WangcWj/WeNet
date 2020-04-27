package cn.wenet.networkcomponent.debug;

import android.text.TextUtils;
import android.util.Log;

/**
 * create to : 框架的日志打印.
 *
 * @author WANG
 * @date 2019-8-19
 */
public class WeDebug {

    public static String TAG = "cc.wang";

    public static boolean DEBUG = true;

    public static boolean LOG_REQUEST_HEADER = false;

    public static void e(String... message) {
        e(TextUtils.concat(message).toString());
    }

    public static void e(String message) {
        if (DEBUG) {
            Log.e(TAG, TextUtils.concat("WeNet: ", message).toString() + "\n");
        }
    }

    public static void logD(String... message) {
        logD(TextUtils.concat(message).toString());
    }

    public static void logD(String message) {
        if (DEBUG) {
            Log.d(TAG, TextUtils.concat("WeNet: ", message).toString() + "\n");
        }
    }

    public static void d(String message) {
        if (DEBUG) {
            Log.d(TAG, TextUtils.concat("WeNet: ", message).toString() + "\n");
        }
    }

    public static String getNullPointerErrorInfo(String method, String fileInfo) {
        return TextUtils.concat("Control.", method, ": ", fileInfo, " cannot be null!").toString();
    }


}
