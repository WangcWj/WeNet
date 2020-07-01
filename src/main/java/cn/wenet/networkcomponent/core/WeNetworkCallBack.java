package cn.wenet.networkcomponent.core;


import android.content.Context;

import cn.wenet.networkcomponent.debug.exception.NetException;
import io.reactivex.disposables.Disposable;

/**
 * Created to :
 *
 * @author WANG
 * @date 2018/12/18
 */

public interface WeNetworkCallBack<T> {

    /**
     * 最好再改方法执行中捕获可能出现的异常信息。
     * @param t
     */
    void onSuccess(T t);

    /**
     * 最好再改方法执行中捕获可能出现的异常信息。
     * @param e
     */
    void onError(NetException e);

    /**
     * 配合每次请求
     *
     * @param disposable
     */
    void pageLifeCircle(Disposable disposable);

    /**
     *
     * @param context
     */
    void showProgress(Context context,boolean canShow);


}
