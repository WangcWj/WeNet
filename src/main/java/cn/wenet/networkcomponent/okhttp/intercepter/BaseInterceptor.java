package cn.wenet.networkcomponent.okhttp.intercepter;


import java.util.HashMap;
import java.util.Map;

import cn.wenet.networkcomponent.request.InnerRequestProvider;
import cn.wenet.networkcomponent.request.NetRequestImpl;
import okhttp3.Interceptor;

/**
 * @author WANG
 * @date 2018/7/19
 */

public abstract class BaseInterceptor implements Interceptor {

    protected Map<String, NetRequestImpl> mPatams;

    public void copyRequestParams(Map<String, NetRequestImpl> params) {
        if (mPatams == null) {
            mPatams = new HashMap<>();
        }
        mPatams.putAll(params);
    }

    public abstract boolean isNetInterceptor();


}
