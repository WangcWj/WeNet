package cn.wenet.networkcomponent.base;


import cn.wenet.networkcomponent.core.WeNetLifeCircleManager;
import cn.wenet.networkcomponent.exception.NetException;
import cn.wenet.networkcomponent.core.WeNetworkCallBack;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @author WANG
 * @date 17/11/23
 * 这边处理结果有两种:
 * 1.是标准的返回结果,code message data.这样框架将自己处理数据返回是否正常.
 * 2.是非标准的.数据原样返回.
 */

public class NetBaseObserver<T> implements Observer<T> {


    private WeNetworkCallBack netCallBack;
    private WeNetLifeCircleManager lifeCircleManager;

    public void setNetCallBack(WeNetworkCallBack netCallBack) {
        this.netCallBack = netCallBack;
    }

    public void setLifeCircleManager(WeNetLifeCircleManager lifeCircleManager) {
        this.lifeCircleManager = lifeCircleManager;
    }

    @Override
    public void onError(Throwable e) {
        NetException netException = new NetException(e);
        netCallBack.onError(netException);
    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onSubscribe(Disposable d) {
        if (null != lifeCircleManager) {
            lifeCircleManager.addDisposable(d);
        }
    }

    @Override
    public void onNext(T t) {
        if (null == netCallBack) {
            return;
        }
        if (t instanceof NetBaseResultBean) {
            NetBaseResultBean resultBean = (NetBaseResultBean) t;
            NetException netException = new NetException(resultBean.getCode(), resultBean.getStatus(), resultBean.getMsg());
            boolean success = netException.success();
            if (success) {
                Object data = resultBean.getData();
                if (null == data) {
                    netException.setMessage("Data数据为null!");
                    netCallBack.onError(netException);
                } else {
                    netCallBack.onSuccess(data);
                }
            } else {
                netCallBack.onError(netException);
            }
        } else {
            netCallBack.onSuccess(t);
        }
    }


}