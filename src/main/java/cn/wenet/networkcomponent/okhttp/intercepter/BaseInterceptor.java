package cn.wenet.networkcomponent.okhttp.intercepter;



import cn.wenet.networkcomponent.request.InnerRequestProvider;
import okhttp3.Interceptor;

/**
 * @author WANG
 * @date 2018/7/19
 */

public abstract class BaseInterceptor implements Interceptor {

    protected InnerRequestProvider mRequestProvider;

    public abstract boolean isNetInterceptor();

    public void setRequestProvider(InnerRequestProvider mRequestProvider) {
        this.mRequestProvider = mRequestProvider;
    }
}
