package cn.wenet.networkcomponent.request;


import java.util.HashMap;
import java.util.Map;

import cn.wenet.networkcomponent.core.Control;
import cn.wenet.networkcomponent.core.WeNetworkCallBack;
import cn.wenet.networkcomponent.retrofit.calladapter.BodyObservable;
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

    private BodyObservable mNetObservable;

    private Observable mObservable;


    protected Map<String, Object> mParams;

    public NetRequest(Control netControl) {
        this.netControl = netControl;
        mParams = new HashMap<>();
        mParams.putAll(netControl.mBaseParams);
    }

    public NetRequest addParams(String key, String value) {
        mParams.put(key, value);
        return this;
    }

    public NetRequest addParams(Map params) {
        mParams.putAll(params);
        return this;
    }

    public Map<String, Object> getParams() {
        return mParams;
    }

    public <T> NetRequest apiMethod(Observable<T> observable) {
        mObservable = observable;
        return this;
    }

    public void setNetObservable(BodyObservable mNetObservable) {
        this.mNetObservable = mNetObservable;
    }

    public <T> void execute(WeNetworkCallBack<T> callback) {
        if (null != mNetObservable) {
            //要先执行
            execute(mNetObservable, callback);
        } else if (null != mObservable) {
            execute(mObservable, callback);
        }
    }

    public <T> void execute(Observable observable, WeNetworkCallBack<T> callback) {
        //要先执行
        netControl.preExecute(callback, observable);
    }


}
