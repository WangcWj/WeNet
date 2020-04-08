package cn.wenet.networkcomponent.okhttp.intercepter;


import android.text.TextUtils;
import android.util.Log;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import cn.wenet.networkcomponent.debug.WeDebug;
import cn.wenet.networkcomponent.utils.RequestBodyUtils;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * @author WANG
 * @date 2018/5/3
 */

public class BaseLogInterceptor extends BaseInterceptor implements Interceptor {
    private static final Charset UTF8 = Charset.forName("UTF-8");

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (WeDebug.DEBUG) {
            HttpUrl httpUrl = request.url();

            List<String> methods = httpUrl.encodedPathSegments();
            if (methods.size() > 0) {
                WeDebug.e("method name  is", methods.get(0));
            }
            String url = httpUrl.toString();
            if (!TextUtils.isEmpty(url)) {
                WeDebug.e("URL is : ", url);
            }
            String method = request.method();
            if (!TextUtils.isEmpty(method)) {
                WeDebug.e("Method is : ", method);
            }
            RequestBody body = request.body();
            if (body != null) {
                String readString = RequestBodyUtils.requestBodyToString(body);
                if(!TextUtils.isEmpty(readString)) {
                    WeDebug.e("RequestBody is : " + readString);
                }
            }
            if (WeDebug.LOG_REQUEST_HEADER) {
                Headers headers = request.headers();
                if (null != headers) {
                    String headerStr = headers.toString();
                    WeDebug.e("Headers is :", headerStr);
                }
            }
            Response response = chain.proceed(request);
            ResponseBody responseBody = response.body();
            if (null != responseBody) {
                BufferedSource source = responseBody.source();
                source.request(Long.MAX_VALUE);
                Buffer buffer = source.buffer();
                MediaType contentType = responseBody.contentType();
                boolean plaintext = RequestBodyUtils.isPlaintext(buffer);
                if (!plaintext) {
                    WeDebug.e("请求结果不是文本，其类型是：", contentType.toString());
                    return response;
                }
                Charset charset = UTF8;
                if (null != contentType) {
                    charset = contentType.charset(UTF8);
                }
                long contentLength = responseBody.contentLength();
                if (0 != contentLength) {
                    String json = buffer.clone().readString(charset);
                    WeDebug.e("Json : \n", json);
                }
            }
            return response;
        } else {
            return chain.proceed(request);
        }
    }


    @Override
    public boolean isNetInterceptor() {
        return true;
    }
}
