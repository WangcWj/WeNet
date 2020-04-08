package cn.wenet.networkcomponent.core;

import android.content.Context;

import java.util.Map;

import cn.wenet.networkcomponent.base.BaseControl;
import cn.wenet.networkcomponent.life.ComponentLifeCircle;
import cn.wenet.networkcomponent.base.NetBaseObserver;
import cn.wenet.networkcomponent.life.PageLifeManager;
import cn.wenet.networkcomponent.life.WeNetLifeCircleManager;
import cn.wenet.networkcomponent.okhttp.intercepter.BaseInterceptor;
import cn.wenet.networkcomponent.request.NetRequest;
import cn.wenet.networkcomponent.retrofit.calladapter.WeNetResultObservable;
import io.reactivex.Observable;
import okhttp3.HttpUrl;

/**
 * 整个网络请求的总线 单例模式
 *
 * @author WANG
 */

public class Control extends BaseControl implements ComponentLifeCircle {

    private Control() {
        mLifeManager = new WeNetLifeCircleManager();
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

    /**
     * 获取网络请求生命周期的管理类。
     *
     * @param context
     * @return {@link PageLifeManager} 管理一个界面的网络请求，可以是Activity可以是Fragment。
     */
    public PageLifeManager getLifeManager(Context context) {
        //全局的单例模式。
        if (mLifeManager == null) {
            mLifeManager = new WeNetLifeCircleManager();
        }
        return mLifeManager.bindContext(context);
    }

    /**
     * 开始网络请求
     *
     * @return
     */
    public <T> NetRequest request(WeNetResult<T> observable) {
        if (observable instanceof WeNetResultObservable) {
            WeNetResultObservable<T> resultObservable = (WeNetResultObservable<T>) observable;
            return resultObservable.getNetRequest();
        } else {
            throw new IllegalArgumentException("WeNet: Parameter type must be WeNetResult!");
        }
    }

    /**
     * 开始网络请求
     *
     * @return
     */
    public <T> NetRequest request(Observable<T> observable) {
        NetRequest request = request();
        request.apiMethod(observable);
        return request;
    }

    /**
     * 开始网络请求
     *
     * @return
     */
    NetRequest request() {
        return new NetRequest(Control.getInstance());
    }

    /**
     * 开始网络请求
     *
     * @return
     */
    NetRequest requestJson() {
        return new NetRequest(Control.getInstance());
    }

    /**
     * 开始网络请求之前，绑定生命周期。
     *
     * @param callback
     * @param observable
     */
    public void bindLifeCircle(WeNetworkCallBack callback, Observable observable) {
        PageLifeManager lifeManager = getLifeManager(callback.getContext());
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
