package cn.wenet.networkcomponent.base;


import android.content.Context;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.wenet.networkcomponent.cache.WeNetCache;
import cn.wenet.networkcomponent.core.Control;
import cn.wenet.networkcomponent.debug.WeDebug;
import cn.wenet.networkcomponent.life.RequestLifeCircle;
import cn.wenet.networkcomponent.okhttp.intercepter.BaseInterceptor;
import cn.wenet.networkcomponent.okhttp.intercepter.NetInterceptorFactory;
import cn.wenet.networkcomponent.okhttp.NetOkHttp;
import cn.wenet.networkcomponent.core.WeNetworkCallBack;
import cn.wenet.networkcomponent.request.NetRequestImpl;
import cn.wenet.networkcomponent.retrofit.NetRetrofit;
import cn.wenet.networkcomponent.retrofit.retrywhen.NetRetryWhen;
import cn.wenet.networkcomponent.utils.WeNetThreadUtils;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.HttpUrl;
import retrofit2.Retrofit;

/**
 * Created to :
 *
 * @author WANG
 * @date 2019/8/28
 */
public class BaseControl {

    private Context mApplicationContext;

    private NetRetryWhen retryWhen;

    private volatile boolean mHaveInit = false;

    protected NetOkHttp mNetOkHttp;

    protected NetRetrofit mNetRetrofit;

    public Map<String, Object> mBaseParams = new HashMap<>();

    protected Map<String, HttpUrl> mBaseUrls = new HashMap<>();

    protected Context getContext() {
        return mApplicationContext;
    }

    public Map<String, Object> getBaseParams() {
        return mBaseParams;
    }

    /**
     * 拦截器跟{@link cn.wenet.networkcomponent.life.WeNetLifeCircleManager}关联起来。
     *
     * @param lifeCircle
     */
    protected void attachInterceptor(RequestLifeCircle lifeCircle) {
        if (null != mNetOkHttp) {
            ArrayList<BaseInterceptor> baseInterceptors = mNetOkHttp.getBaseInterceptors();
            for (BaseInterceptor interceptor : baseInterceptors) {
                interceptor.attachRequest(lifeCircle);
            }
        }
    }

    /**
     * 每次发起网络请求的时候都需要去重组Retrofit和OkHttp
     */
    protected void combination() {
        if (!mHaveInit) {
            throw new RuntimeException("初始化过程有误!");
        }

        boolean haveChange = mNetOkHttp.isHaveChange();
        if (haveChange) {
            mNetRetrofit.transform(mNetOkHttp.getOkHttpClient());
        }
    }

    /**
     * 订阅
     *
     * @param observable
     * @param callback
     */
    public void subscribe(Observable observable, NetBaseObserver callback) {
        checkNull("subscribe", "callback", callback);
        toSubscribe(observable, callback);
    }

    /**
     * 获取接收结果的Observe
     *
     * @param netCallBack
     * @return
     */
    public NetBaseObserver getBaseObserve(NetRequestImpl imp, WeNetworkCallBack netCallBack) {
        NetBaseObserver observer = new NetBaseObserver();
        observer.setRequest(imp);
        if (imp.isUseCache()) {
            WeNetCache cacheCallback = new WeNetCache();
            cacheCallback.attach(imp, netCallBack);
            observer.setNetCallBack(cacheCallback);
        } else {
            observer.setNetCallBack(netCallBack);
        }
        return observer;
    }

    /**
     * RxJava订阅事件
     *
     * @param observable
     * @param observer
     * @param <T>
     */
    protected <T> void toSubscribe(Observable<T> observable, NetBaseObserver<T> observer) {
        if (null == retryWhen) {
            //错误重连的次数跟每次重连之间的间隔.
            retryWhen = new NetRetryWhen();
        }
        retryWhen.reset(NetBaseParam.RETRYWHEN_COUNT, NetBaseParam.RETRYWHEN_TIME);
        observable
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(NetBaseParam.READ_TIMEOUT, TimeUnit.SECONDS)
                .retryWhen(retryWhen)
                .subscribe(observer);
    }

    /**
     * 获取API
     *
     * @param clz
     * @param <T>
     * @return
     */
    public <T> T getApiService(Class<T> clz) {
        T apiService = null;
        Retrofit retrofit = mNetRetrofit.getRetrofit();
        if (retrofit != null) {
            apiService = retrofit.create(clz);
        }
        return apiService;
    }


    public void init(Context context) {
        if (!WeNetThreadUtils.isMainThread()) {
            WeDebug.e("请在主线程中初始化该框架！");
            return;
        }
        if (mHaveInit) {
            return;
        }
        mApplicationContext = context;
        mNetOkHttp = NetOkHttp.getInstance(this);
        mNetRetrofit = NetRetrofit.getInstance();
        mNetOkHttp.addBaseInterceptor(NetInterceptorFactory.baseUrlInterceptor());
        mNetOkHttp.addBaseInterceptor(NetInterceptorFactory.logInterceptor());
        mNetOkHttp.addBaseInterceptor(NetInterceptorFactory.baseParamsIntercepter());
        mHaveInit = true;
    }


    protected void transformationUrl(String flag, String url) {
        String[] split = flag.split(":");
        if (split.length <= 0) {
            throw new IllegalArgumentException("Please check that your parameters are correct !");
        }
        String base = Control.BASE_URL_HEADER.replace(":", "").trim();
        if (base.equals(split[1])) {
            mNetRetrofit.setBaseUrl(url);
            combination();
        }
        HttpUrl httpUrl = HttpUrl.parse(url);
        mBaseUrls.put(split[1], httpUrl);
    }

    protected void checkNull(String method, String filename, Object... o) {
        for (int i = 0; i < o.length; i++) {
            if (null == o[i]) {
                throw new NullPointerException(WeDebug.getNullPointerErrorInfo(method, filename));
            }
        }
    }

    public File getCacheFile() {
        if (null != mApplicationContext) {
            File cacheDir = mApplicationContext.getExternalCacheDir();
            if (null != cacheDir) {
                String path = cacheDir.getAbsolutePath() + File.separator + "WeNet";
                return new File(path);
            }

        }
        return null;
    }
}
