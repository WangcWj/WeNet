package cn.wenet.networkcomponent.okhttp.intercepter;

import cn.wenet.networkcomponent.life.RequestLifeCircle;
import okhttp3.Interceptor;

/**
 * @author WANG
 * @date 2018/7/19
 */

public abstract class BaseInterceptor implements Interceptor {

    protected RequestLifeCircle mRequests;

    public void attachRequest(RequestLifeCircle requests){
        this.mRequests = requests;
    }

    public abstract boolean isNetInterceptor();


}
