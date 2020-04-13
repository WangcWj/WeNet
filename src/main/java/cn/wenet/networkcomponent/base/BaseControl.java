package cn.wenet.networkcomponent.base;


import android.content.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.wenet.networkcomponent.core.Control;
import cn.wenet.networkcomponent.debug.WeDebug;
import cn.wenet.networkcomponent.okhttp.intercepter.BaseInterceptor;
import cn.wenet.networkcomponent.okhttp.intercepter.BaseParamsInterceptor;
import cn.wenet.networkcomponent.okhttp.intercepter.BaseUrlInterceptor;
import cn.wenet.networkcomponent.okhttp.intercepter.NetInterceptorFactory;
import cn.wenet.networkcomponent.okhttp.NetOkHttp;
import cn.wenet.networkcomponent.core.WeNetworkCallBack;
import cn.wenet.networkcomponent.request.NetRequestImpl;
import cn.wenet.networkcomponent.retrofit.NetRetrofit;
import cn.wenet.networkcomponent.rxjava.NetRetryWhen;
import cn.wenet.networkcomponent.utils.ThreadUtils;
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

    protected volatile boolean mHaveInit = false;

    protected NetOkHttp mNetOkHttp;

    protected NetRetrofit mNetRetrofit;

    public Map<String, Object> mBaseParams = new HashMap<>();

    protected Map<String, HttpUrl> mBaseUrls = new HashMap<>();
    private BaseParamsInterceptor paramsInterceptor;

    protected Context getContext() {
        return mApplicationContext;
    }

    public Map<String, Object> getBaseParams() {
        return mBaseParams;
    }

    public void addRequestParams(String url, NetRequestImpl request) {
        if (null != paramsInterceptor) {
            paramsInterceptor.addRequest(url, request);
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
    public NetBaseObserver getBaseObserve(WeNetworkCallBack netCallBack) {
        NetBaseObserver observer = new NetBaseObserver();
        observer.setNetCallBack(netCallBack);
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
        if (!ThreadUtils.isMainThread()) {
            WeDebug.e("请在主线程中初始化该框架！");
            return;
        }
        if (mHaveInit) {
            return;
        }
        mApplicationContext = context;
        mNetOkHttp = NetOkHttp.getInstance();
        mNetRetrofit = NetRetrofit.getInstance();
        paramsInterceptor = (BaseParamsInterceptor) NetInterceptorFactory.baseParamsIntercepter();
        BaseInterceptor interceptor = NetInterceptorFactory.baseUrlInterceptor();
        ((BaseUrlInterceptor) interceptor).setParamsInterceptor(paramsInterceptor);
        mNetOkHttp.addBaseInterceptor(interceptor);
        mNetOkHttp.addBaseInterceptor(NetInterceptorFactory.logInterceptor());
        mNetOkHttp.addBaseInterceptor(paramsInterceptor);
        mHaveInit = true;
    }

    public BaseInterceptor getParamsInterceptor() {
        return paramsInterceptor;
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

}
