package cn.wenet.networkcomponent.core;


import cn.wenet.networkcomponent.debug.exception.NetException;

/**
 * Created to :
 *
 * @author WANG
 * @date 2018/12/18
 */

public abstract class WeNetworkCallBack<T> {

    public abstract void onSuccess(T t);

    public abstract void onError(NetException e);

}
