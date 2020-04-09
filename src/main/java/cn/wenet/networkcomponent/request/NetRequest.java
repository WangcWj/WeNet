package cn.wenet.networkcomponent.request;


import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

import cn.wenet.networkcomponent.core.Control;
import cn.wenet.networkcomponent.core.WeNetworkCallBack;
import cn.wenet.networkcomponent.retrofit.calladapter.WeNetResultObservable;
import cn.wenet.networkcomponent.utils.GsonUtils;
import io.reactivex.Observable;
import okhttp3.MediaType;

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

public class NetRequest {

    private Control netControl;

    private WeNetResultObservable mNetObservable;

    private Observable mObservable;

    private Map<String, Object> mParams;

    private String mUrl;

    private String mBodyJson = "";

    private boolean isBody = false;

    private boolean isForm = true;

    public NetRequest(Control netControl) {
        this.netControl = netControl;
        mParams = new HashMap<>();
        mParams.putAll(netControl.mBaseParams);
    }

    public Map<String, Object> getParams() {
        return mParams;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getBodyJson() {
        return mBodyJson;
    }

    public Observable getObservable() {
        if (null != mNetObservable) {
            //要先执行NetObservable。
            return mNetObservable;
        } else if (null != mObservable) {
            //再执行Observable。
            return mObservable;
        }
        return null;
    }

    public NetRequest asBody() {
        this.isBody = true;
        return this;
    }

    public NetRequest asFrom() {
        this.isForm = true;
        return this;
    }

    public NetRequest bodyToJson(String json) {
        checkBody();
        mBodyJson = json;
        return this;
    }

    public NetRequest bodyToJson(Object o) {
        if (null != o) {
            String toJson = GsonUtils.objectToJson(o);
            if (!TextUtils.isEmpty(toJson)) {
                bodyToJson(toJson);
            } else {
                throw new IllegalArgumentException("NetRequest: NetRequest#bodyToJson()参数类型" + o.getClass().getName() + "不支持");
            }
        }
        return this;
    }

    private void checkBody() {
        if (!isBody) {
            throw new IllegalStateException("NetRequest: 请先调用 NetRequest#asBody() 方法！");
        }
    }

    public NetRequest bodyToRequestBody(Object o) {
        if (null != o) {
            String toJson = GsonUtils.objectToJson(o);
            if (!TextUtils.isEmpty(toJson)) {

            }
        }
        return this;
    }

    public NetRequest addParams(String key, String value) {
        mParams.put(key, value);
        return this;
    }

    public NetRequest addParams(Map params) {
        mParams.putAll(params);
        return this;
    }

    public boolean isBody() {
        return isBody;
    }

    public boolean isForm() {
        return isForm;
    }

    public void setNetObservable(WeNetResultObservable netObservable) {
        mNetObservable = netObservable;
    }

    public <T> NetRequest apiMethod(Observable<T> observable) {
        mObservable = observable;
        return this;
    }

    public <T> void execute(WeNetworkCallBack<T> callback) {
        if (null != mNetObservable) {
            //要先执行NetObservable。
            execute(mNetObservable, callback);
        } else if (null != mObservable) {
            //再执行Observable。
            execute(mObservable, callback);
        }
    }

    private <T> void execute(Observable observable, WeNetworkCallBack<T> callback) {
        //要先执行
        netControl.bindLifeCircle(callback, observable);
    }

    public void attachUrl(String url) {
        mUrl = url;
        netControl.addRequestParams(url, this);
    }
}
