package cn.wenet.networkcomponent.core;

import android.app.Dialog;
import android.content.Context;

import androidx.fragment.app.Fragment;

import java.util.Map;

import cn.wenet.networkcomponent.base.BaseControl;
import cn.wenet.networkcomponent.life.ComponentLifeCircle;
import cn.wenet.networkcomponent.base.NetBaseObserver;
import cn.wenet.networkcomponent.life.PageLifeManager;
import cn.wenet.networkcomponent.life.WeNetLifeCircleManager;
import cn.wenet.networkcomponent.okhttp.intercepter.BaseInterceptor;
import cn.wenet.networkcomponent.request.NetRequestImpl;
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

    }

    private volatile static Control instance = null;

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

    /**
     * 开始网络请求
     *
     * @return
     */
    public <T> WeNetRequest request(WeNetResult<T> observable) {
        if (observable instanceof WeNetResultObservable) {
            WeNetResultObservable<T> resultObservable = (WeNetResultObservable<T>) observable;
            return resultObservable.getNetRequestImpl();
        } else {
            throw new IllegalArgumentException("WeNet: Parameter type must be WeNetResult!");
        }
    }

    /**
     * 开始网络请求
     *
     * @return
     */
    public <T> NetRequestImpl request(Observable<T> observable) {
        NetRequestImpl request = request();
        request.apiMethod(observable);
        return request;
    }

    /**
     * 开始网络请求
     *
     * @return
     */
    NetRequestImpl request() {
        return new NetRequestImpl(this);
    }

    public PageLifeManager bindApplication() {
        initLifeManager();
        return mLifeManager.bindApplication();
    }

    /**
     * 绑定Activity的生命周期，加入的是Fragment。
     *
     * @param context 需要是Activity类型。
     * @return
     */
    public PageLifeManager bindContext(Context context) {
        initLifeManager();
        return mLifeManager.bindContext(context);
    }

    /**
     * 绑定Fragment的生命周期，加入的是子Fragment。
     *
     * @param fragment
     * @return
     */
    public PageLifeManager bindFragment(Fragment fragment) {
        initLifeManager();
        return mLifeManager.bindFragment(fragment);
    }

    /**
     * 绑定Dialog的生命周期，加入的是子View。
     *
     * @param dialog
     * @return
     */
    public PageLifeManager bindDialog(Dialog dialog) {
        initLifeManager();
        return mLifeManager.bindDialog(dialog);
    }

    private void initLifeManager(){
        if (mLifeManager == null) {
            mLifeManager = new WeNetLifeCircleManager();
            attachInterceptor(mLifeManager);
        }
    }

    /**
     * 开始网络请求之前，绑定生命周期。
     *
     * @param callback
     * @param observable
     */
    public void execute(NetRequestImpl imp, PageLifeManager lifeManager, Observable observable, WeNetworkCallBack callback) {
        if(null == lifeManager){
            lifeManager = bindApplication();
        }
        NetBaseObserver baseObserver = getBaseObserve(imp,callback);
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
