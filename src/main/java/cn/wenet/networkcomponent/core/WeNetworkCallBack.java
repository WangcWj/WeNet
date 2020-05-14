package cn.wenet.networkcomponent.core;


import cn.wenet.networkcomponent.debug.exception.NetException;

/**
 * Created to :
 *
 * @author WANG
 * @date 2018/12/18
 */

public abstract class WeNetworkCallBack<T> {

    /**
     * 最好再改方法执行中捕获可能出现的异常信息。
     * @param t
     */
    public abstract void onSuccess(T t);

    /**
     * 最好再改方法执行中捕获可能出现的异常信息。
     * @param e
     */
    public abstract void onError(NetException e);

}
