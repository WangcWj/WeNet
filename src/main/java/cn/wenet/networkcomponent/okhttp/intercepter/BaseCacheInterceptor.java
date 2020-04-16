package cn.wenet.networkcomponent.okhttp.intercepter;

import java.io.IOException;

import cn.wenet.networkcomponent.core.WeNetWork;
import okhttp3.Response;

/**
 * Created to :
 *
 * @author WANG
 * @date 2020/4/16
 */
public class BaseCacheInterceptor extends BaseInterceptor {



    @Override
    public boolean isNetInterceptor() {
        return false;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {




        return null;
    }
}
