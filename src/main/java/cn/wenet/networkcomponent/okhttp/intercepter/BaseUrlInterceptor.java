package cn.wenet.networkcomponent.okhttp.intercepter;


import android.text.TextUtils;

import java.io.IOException;
import java.util.Map;

import cn.wenet.networkcomponent.core.Control;
import cn.wenet.networkcomponent.core.WeNetWork;
import cn.wenet.networkcomponent.debug.WeDebug;
import cn.wenet.networkcomponent.life.WeNetLifeCircleManager;
import cn.wenet.networkcomponent.okhttp.parse.WeUrlParse;
import cn.wenet.networkcomponent.request.NetRequestImpl;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * https://github.com/JessYanCoding/RetrofitUrlManager/tree/master/manager/src/main/java/me/jessyan/retrofiturlmanager
 * <p>
 * URL的组成： http://10.1.192.66:8080/zentao/index.php?m=task&f=view&task=4640。
 * protocol协议,http或者https://域名地址+端口/服务器的路径？参数。
 * </p>
 *
 * @author WANG
 * @date 2018/5/3
 */

public class BaseUrlInterceptor extends BaseInterceptor implements Interceptor {

    private WeUrlParse urlParse;

    public BaseUrlInterceptor() {
        urlParse = new WeUrlParse();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Headers headers = request.headers();
        String header = headers.get(Control.GLOBAL_HEADER);
        WeDebug.e("Header  is  " + header);
        if (!TextUtils.isEmpty(header) && !Control.BASE_URL_HEADER.equals(header)) {
            WeDebug.e("检测到新的BaseUrl。。。。");
            Map<String, HttpUrl> baseUrls = WeNetWork.getBaseUrls();
            HttpUrl httpUrl = baseUrls.get(header);
            if (null != httpUrl) {
                HttpUrl newHttpUrl = urlParse.parseUrl(httpUrl, request.url());
                WeDebug.e("新的Url是：" + newHttpUrl);
                Request.Builder newBuilder = request.newBuilder();
                newBuilder.removeHeader(Control.GLOBAL_HEADER);
                Request newRequest = newBuilder.url(newHttpUrl).build();
                //这个代码解决的是当BaseUrl需要替换的时候，需要通过新的Url取基础参数。
                String u = request.url().toString();
                if (null != mRequests && mRequests.getRequests().size() > 0) {
                    Map<String, NetRequestImpl> params = mRequests.getRequests();
                    if (null != params) {
                        String key = WeNetLifeCircleManager.mergeHttp(u);
                        if (params.containsKey(key)) {
                            NetRequestImpl netRequestImpl = params.get(key);
                            if (null != netRequestImpl) {
                                params.remove(key);
                                String url = newHttpUrl.toString();
                                netRequestImpl.updateUrl(url);
                                params.put(WeNetLifeCircleManager.mergeHttp(url), netRequestImpl);
                            }
                        }
                    }
                }
                return chain.proceed(newRequest);
            }
        }
        return chain.proceed(request);
    }

    @Override
    public boolean isNetInterceptor() {
        return false;
    }
}
