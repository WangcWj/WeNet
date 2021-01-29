package cn.wenet.networkcomponent.request;


import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;

import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.Map;

import cn.wenet.networkcomponent.core.Control;
import cn.wenet.networkcomponent.core.WeNetRequest;
import cn.wenet.networkcomponent.core.WeNetworkCallBack;
import cn.wenet.networkcomponent.life.PageLifeManager;
import cn.wenet.networkcomponent.retrofit.calladapter.WeNetResultObservable;
import cn.wenet.networkcomponent.utils.WeNetGsonUtils;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

/**
 * @author WANG
 * @date 2018/5/4
 * 请求从这里发起.
 * 1.这里管理基础参数
 * 2.管理Header的添加
 * 3.管理文件上传RequestBody
 * <p>
 * GsonConverterFactory处理的参数注解有： @Body、@Part、@PartMap。
 * <p>
 * 链式调用支持的请求方式：
 * {
 * POST请求：有请求体。
 * <p>
 * 1.表单请求，也就是RequestBody为FormBody。
 * 支持的注解{@link retrofit2.http.Field,retrofit2.http.FieldMap}。
 * <p>
 * 2.RequestBody请求，也就是请求的实体就是RequestBody。
 * 支持的注解{@link retrofit2.http.Body}。
 * <p>
 * GET请求：没有请求体。
 * <p>
 * 支持的注解{@link retrofit2.http.Query,retrofit2.http.QueryMap}
 * <p>
 * }
 * <p>
 * { 如果使用了@Body的参数注解，该注解的类型可以是Object，一般的使用是 (@Body RequestBody body)或者(@Body Object obj)
 * 以下几种情况：
 * 1.参数类型是RequestBody：处理该注解的Converter是默认的BuiltInConverters。
 * 2.参数类型是实体类，基本数据类型：处理该注解的Converter是GsonRequestBodyConverter。
 * 3.参数类型是String：处理该注解的Converter是ToStringConverterFactory自定义的。
 * <p>
 *
 *
 * <p>
 * }
 */

public class NetRequestImpl implements WeNetRequest {

    private Control netControl;

    private WeNetResultObservable mNetObservable;

    private PageLifeManager mPageLifeManager;

    private Observable mObservable;

    private Disposable mCurrentDisposable;

    private Map<String, Object> mParams;

    private String mUrl;

    private String mBodyJson = "{}";

    private boolean isBody = true;

    private boolean isForm = false;

    private boolean mIsShowProgress = false;

    private boolean mUserCache = false;

    public NetRequestImpl(Control netControl) {
        this.netControl = netControl;
        mParams = new HashMap<>();
    }

    @Override
    public Map<String, Object> getParams() {
        return mParams;
    }

    @Override
    public String getUrl() {
        return mUrl;
    }

    @Override
    public Observable getObservable() {
        if (null != mNetObservable) {
            return mNetObservable;
        } else if (null != mObservable) {
            return mObservable;
        }
        return null;
    }

    @Override
    public boolean isBody() {
        return isBody;
    }

    @Override
    public boolean isForm() {
        return isForm;
    }

    @Override
    public void autoShowProgress() {

    }

    @Override
    public void cacheMode(boolean useCache) {

    }

    @Override
    public NetRequestImpl asBody() {
        this.isBody = true;
        return this;
    }

    @Override
    public NetRequestImpl asFrom() {
        this.isForm = true;
        return this;
    }

    @Override
    public NetRequestImpl bodyToJson(String json) {
        checkBody();
        mBodyJson = json;
        return this;
    }

    @Override
    public NetRequestImpl bodyToJson(Object o) {
        if (null != o) {
            String toJson = WeNetGsonUtils.objectToJson(o);
            if (!TextUtils.isEmpty(toJson)) {
                bodyToJson(toJson);
            } else {
                throw new IllegalArgumentException("NetRequestImpl: NetRequestImpl#bodyToJson()参数类型" + o.getClass().getName() + "不支持");
            }
        }
        return this;
    }

    @Override
    public NetRequestImpl addParams(String key, String value) {
        mParams.put(key, value);
        return this;
    }

    @Override
    public WeNetRequest addParams(String key, Object value) {
        mParams.put(key, value);
        return this;
    }

    @Override
    public NetRequestImpl addParams(Map<String, Object> params) {
        mParams.putAll(params);
        return this;
    }

    @Override
    public WeNetRequest bindLife(Context context) {
        if (null == mPageLifeManager) {
            mPageLifeManager = netControl.bindContext(context);
        }
        return this;
    }

    @Override
    public WeNetRequest bindLife(Fragment fragment) {
        if (null == mPageLifeManager) {
            mPageLifeManager = netControl.bindFragment(fragment);
        }
        return this;
    }

    @Override
    public WeNetRequest bindLife(Dialog dialog) {
        if (null == mPageLifeManager) {
            mPageLifeManager = netControl.bindDialog(dialog);
        }
        return this;
    }

    @Override
    public WeNetRequest showProgress(boolean show) {
        mIsShowProgress = show;
        return this;
    }

    @Override
    public WeNetRequest isUseCache(boolean use) {
        mUserCache = use;
        return this;
    }

    @Override
    public <T> NetRequestImpl apiMethod(Observable<T> observable) {
        mObservable = observable;
        return this;
    }

    @Override
    public <T> void execute(WeNetworkCallBack<T> callback) {
        if (null != mNetObservable) {
            //要先执行NetObservable。
            execute(mNetObservable, callback);
        } else if (null != mObservable) {
            //再执行Observable。
            execute(mObservable, callback);
        }
    }

    @Override
    public Disposable getCurrentDisposable() {
        return mCurrentDisposable;
    }

    public boolean isUseCache() {
        return mUserCache;
    }

    public boolean isShowProgress() {
        return mIsShowProgress;
    }

    public void updateUrl(String url) {
        mUrl = url;
    }

    public void setCurrentDisposable(Disposable mCurrentDisposable) {
        this.mCurrentDisposable = mCurrentDisposable;
    }

    public void setNetObservable(WeNetResultObservable netObservable) {
        mNetObservable = netObservable;
    }

    public void attachUrl(String url) {
        mUrl = url;
    }

    public String getBodyJson() {
        if(mParams.size() > 0){
            mBodyJson = WeNetGsonUtils.objectToJson(mParams);
        }
        return mBodyJson;
    }

    private void checkBody() {
        if (!isBody) {
            throw new IllegalStateException("NetRequestImpl: 请先调用 NetRequestImpl#asBody() 方法！");
        }
    }

    private <T> void execute(Observable observable, WeNetworkCallBack<T> callback) {
        //要先执行
        netControl.execute(this, mPageLifeManager, observable, callback);
    }


    @Override
    public String toString() {
        return "NetRequestImpl{" +
                "mUrl='" + mUrl + '\'' +
                '}';
    }
}
