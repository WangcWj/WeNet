package cn.wenet.networkcomponent.core;

import android.content.Context;

import java.util.Map;

import cn.wenet.networkcomponent.base.BaseControl;
import cn.wenet.networkcomponent.base.ComponentLifeCircle;
import cn.wenet.networkcomponent.base.NetBaseObserver;
import cn.wenet.networkcomponent.okhttp.intercepter.BaseInterceptor;
import cn.wenet.networkcomponent.request.NetRequest;
import io.reactivex.Observable;
import okhttp3.HttpUrl;

/**
 * 整个网络请求的总线 单例模式
 *
 * @author WANG
 */

public class Control extends BaseControl implements ComponentLifeCircle {

    private Control() {

    }

    private static Control instance = null;

    static Control getInstance() {
        if (null == instance) {
            synchronized (Control.class) {
                if (null == instance) {
                    instance = new Control();
                }
            }
        }
        return instance;
    }

    public final static String GLOBAL_HEADER = "baseUrl-Header";

    public final static String BASE_URL_HEADER = ":BaseUrl";

    public final static String DEFAULT_BASE_URL_FLAG = GLOBAL_HEADER + BASE_URL_HEADER;

    private WeNetLifeCircleManager mLifeManager;

    public void addBaseInterceptor(BaseInterceptor baseInterceptor) {
        mNetOkHttp.addBaseInterceptor(baseInterceptor);
    }

    public void addBaseParams(String key, Object value) {
        checkNull("addBaseParams", "key Or value", key, value);
        mBaseParams.put(key, value);
    }

    public void addBaseParams(Map<String, Object> params) {
        checkNull("addBaseParams", "params", params);
        mBaseParams.clear();
        mBaseParams.putAll(params);
    }

    public void addBaseUrl(String flag, String url) {
        checkNull("init", "baseUrl", url);
        transformationUrl(flag, url);
    }

    public Map<String, HttpUrl> getBaseUrls() {
        return mBaseUrls;
    }

    public boolean isHaveInit() {
        return mHaveInit;
    }

    public WeNetLifeCircleManager getLifeManager(Context context) {
        return null;
    }

    /**
     * 开始网络请求
     *
     * @return
     */
    public NetRequest request() {
        return new NetRequest(Control.getInstance());
    }

    /**
     * 开始网络请求
     *
     * @return
     */
    public NetRequest requestJson() {
        return new NetRequest(Control.getInstance());
    }

    public void preExecute(WeNetworkCallBack callback, Observable observable) {
        WeNetLifeCircleManager lifeManager = getLifeManager(callback.getContext());
        NetBaseObserver baseObserver = getBaseObserve(callback);
        baseObserver.setLifeCircleManager(lifeManager);
        subscribe(observable, baseObserver);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {
      instance = null;

    }
}
