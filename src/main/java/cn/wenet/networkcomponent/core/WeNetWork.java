package cn.wenet.networkcomponent.core;

import android.content.Context;

import androidx.annotation.NonNull;


import java.io.File;
import java.util.Map;

import cn.wenet.networkcomponent.base.NetBaseParam;
import cn.wenet.networkcomponent.okhttp.intercepter.BaseInterceptor;
import io.reactivex.Observable;
import okhttp3.HttpUrl;

/**
 * @author WANG
 */
public class WeNetWork {

    private WeNetWork() {

    }

    /**
     * 请务必调用开启框架的初始化。
     *
     * @param context
     * @return
     */
    public static WeNetWork init(Context context) {
        Control.getInstance().init(context);
        return new WeNetWork();
    }

    public static Map<String, HttpUrl> getBaseUrls() {
        return Control.getInstance().getBaseUrls();
    }

    public WeNetWork addBaseInterceptor(@NonNull BaseInterceptor interceptor) {
        Control.getInstance().addBaseInterceptor(interceptor);
        return this;
    }

    public WeNetWork successCode(int code) {
        NetBaseParam.SUCCESS_CODE = code;
        return this;
    }

    /**
     * 框架会默认一个Url为基础Url，该Url必须支持大部分的接口。个别不同域名的Url就需要在Url中添加特定的Header，
     * 该Key为{@link Control#GLOBAL_HEADER}，值必须区别于{@link Control#BASE_URL_HEADER}。
     *
     * @param flag 添加到Url中的Header值。组成类似："baseUrl-Header:BaseUrl";
     * @param url  新域名的Url。
     * @return
     */
    public WeNetWork addBaseUrl(String flag, String url) {
        Control.getInstance().addBaseUrl(flag, url);
        return this;
    }

    public static <T> T apiMethod(Class<T> clz) {
        return Control.getInstance().getApiService(clz);
    }

    /**
     * 开始网络请求，每个网络请求都是从这里开始的。
     *
     * @return
     */
    public static <T> WeNetRequest request(WeNetResult<T> observable) {
        return Control.getInstance().request(observable);
    }

    /**
     * 开始网络请求，每个网络请求都是从这里开始的。
     *
     * @return
     */
    public static <T> WeNetRequest request(Observable<T> observable) {
        return Control.getInstance().request(observable);
    }

    public static Map<String, Object> getBaseParams() {
        return Control.getInstance().getBaseParams();
    }

    public static File getCacheFile() {
        return Control.getInstance().getCacheFile();
    }

    /**
     * 开始网络请求，每个网络请求都是从这里开始的。
     *
     * @return
     */
    public static WeNetRequest request() {
        return Control.getInstance().request();
    }

    /**
     * 获取ApiService。
     *
     * @param clz
     * @param <T>
     * @return
     */
    public static <T> T getApiServiceInstance(Class<T> clz) {
        return Control.getInstance().getApiService(clz);
    }

    /**
     * 这里用来手动销毁资源
     */
    public static void destroy() {

    }

}
