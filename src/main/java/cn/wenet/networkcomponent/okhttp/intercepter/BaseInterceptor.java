package cn.wenet.networkcomponent.okhttp.intercepter;



import cn.wenet.networkcomponent.core.Control;
import okhttp3.Interceptor;

/**
 * @author WANG
 * @date 2018/7/19
 */

public abstract class BaseInterceptor implements Interceptor {

    protected Control mNetControl;

    public void attachControl(Control control){
        this.mNetControl = control;
    }

    public abstract boolean isNetInterceptor();


}
