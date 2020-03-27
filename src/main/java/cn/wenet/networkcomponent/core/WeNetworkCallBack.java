package cn.wenet.networkcomponent.core;


import android.content.Context;

import cn.wenet.networkcomponent.exception.NetException;

/**
 * Created to :
 *
 * @author WANG
 * @date 2018/12/18
 */

public abstract class WeNetworkCallBack<T> {

    private Context mContext;

    public WeNetworkCallBack(Context context) {
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    public abstract void onSuccess(T t);

    public abstract void onError(NetException e);

}
