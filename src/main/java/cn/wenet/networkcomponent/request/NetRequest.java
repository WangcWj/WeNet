package cn.wenet.networkcomponent.request;


import java.util.HashMap;
import java.util.Map;

import cn.wenet.networkcomponent.core.Control;
import cn.wenet.networkcomponent.core.WeNetworkCallBack;
import cn.wenet.networkcomponent.retrofit.calladapter.WeNetResultObservable;
import io.reactivex.Observable;

/**
 * @author WANG
 * @date 2018/5/4
 * 请求从这里发起.
 * 1.这里管理基础参数
 * 2.管理Header的添加
 * 3.管理文件上传RequestBody
 */

public class NetRequest {

    private Control netControl;

    private WeNetResultObservable mNetObservable;

    private Observable mObservable;

    private Map<String, Object> mParams;

    private String mUrl;

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
