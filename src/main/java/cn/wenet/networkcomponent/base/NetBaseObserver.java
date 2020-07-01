package cn.wenet.networkcomponent.base;


import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import cn.wenet.networkcomponent.debug.WeDebug;
import cn.wenet.networkcomponent.life.ComponentLifeCircle;
import cn.wenet.networkcomponent.life.PageLifeManager;
import cn.wenet.networkcomponent.debug.exception.NetException;
import cn.wenet.networkcomponent.core.WeNetworkCallBack;
import cn.wenet.networkcomponent.request.NetRequestImpl;
import cn.wenet.networkcomponent.utils.ThreadUtils;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @author WANG
 * @date 17/11/23
 * 这边处理结果有两种:
 * 1.是标准的返回结果,code message data.这样框架将自己处理数据返回是否正常.
 * 2.是非标准的.数据原样返回.
 */

public class NetBaseObserver<T> implements Observer<T>, ComponentLifeCircle {

    private NetRequestImpl mCurrentRequest;
    private WeNetworkCallBack netCallBack;
    private PageLifeManager lifeCircleManager;

    public void setRequest(NetRequestImpl request) {
        this.mCurrentRequest = request;
    }

    void setNetCallBack(WeNetworkCallBack netCallBack) {
        this.netCallBack = netCallBack;
    }

    public void setLifeCircleManager(@Nullable PageLifeManager lifeCircleManager) {
        this.lifeCircleManager = lifeCircleManager;
    }

    private void requestFinish() {
        if (null != lifeCircleManager) {
            lifeCircleManager.unRegister(this);
            lifeCircleManager.requestEnd(mCurrentRequest);
        }
        clearData();
    }

    @Override
    public void onError(Throwable e) {
        if (ThreadUtils.isMainThread()) {
            if (null != netCallBack) {
                NetException netException = new NetException(e);
                netCallBack.onError(netException);
            }
            requestFinish();
        }
    }

    @Override
    public void onComplete() {

    }

    /**
     * 这个方法执行之前，mCurrentRequest中的Url还是null。
     *
     * @param d
     */
    @Override
    public void onSubscribe(Disposable d) {
        mCurrentRequest.setCurrentDisposable(d);
        if (null != lifeCircleManager) {
            Context context = lifeCircleManager.getContext();
            if (null != context && mCurrentRequest.isShowProgress()) {
                handlerProgress(context, true);
            }
            lifeCircleManager.register(this);
            lifeCircleManager.requestStart(mCurrentRequest);
        }
        if (null != netCallBack) {
            netCallBack.pageLifeCircle(d);
        }
    }

    @Override
    public void onNext(T t) {
        if (null == netCallBack) {
            return;
        }
        NetException netException = null;
        try {
            if (t instanceof NetBaseResultBean) {
                NetBaseResultBean resultBean = (NetBaseResultBean) t;
                netException = new NetException(resultBean.getCode(), resultBean.getMsg());
                boolean success = netException.success();
                if (success) {
                    Object data = resultBean.getData();
                    netCallBack.onSuccess(data);
                } else {
                    netCallBack.onError(netException);
                }
            } else {
                netCallBack.onSuccess(t);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (null == netException) {
                netException = new NetException(e);
            }
            netCallBack.onError(netException);
        } finally {
            requestFinish();
        }
    }

    @Override
    public void onDestroy() {
        clearData();
        handlerProgress(null, false);
    }

    private void handlerProgress(Context context, boolean show) {
        if (null != netCallBack) {
            netCallBack.showProgress(context, show);
        }
    }

    private void clearData() {
        netCallBack = null;
        lifeCircleManager = null;
        mCurrentRequest = null;
    }
}
