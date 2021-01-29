package cn.wenet.networkcomponent.utils;

import com.google.gson.Gson;

/**
 * Created to :
 *
 * @author cc.wang
 * @date 2020/4/9
 */
public class WeNetGsonUtils {

    public static String objectToJson(Object o) {
        try {
            Gson gson = new Gson();
            return gson.toJson(o);
        } catch (Exception e) {
            return "";
        }
    }

}
