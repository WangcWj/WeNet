package cn.wenet.networkcomponent.okhttp.intercepter;


import android.text.TextUtils;
import android.util.Log;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import cn.wenet.networkcomponent.debug.WeDebug;
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

            List<String> strings = httpUrl.encodedPathSegments();
            if (strings.size() > 0) {
                Log.e("WANG", "BaseLogInterceptor.intercept" + strings);
            }
            String url = httpUrl.toString();
            if (!TextUtils.isEmpty(url)) {
                WeDebug.e("URL is : " + url);
            }
            String method = request.method();
            if (!TextUtils.isEmpty(method)) {
                WeDebug.e("Method is : " + method);
            }
            RequestBody body = request.body();
            if (null != body) {
                String bodyStr = body.toString();
                WeDebug.e("RequestBody is :" + bodyStr);
            }
            if (WeDebug.LOG_REQUEST_HEADER) {
                Headers headers = request.headers();
                if (null != headers) {
                    String headerStr = headers.toString();
                    WeDebug.e("Headers is :" + headerStr);
                }
            }
            Response response = chain.proceed(request);
            ResponseBody responseBody = response.body();
            if (null != responseBody) {
                BufferedSource source = responseBody.source();
                source.request(Long.MAX_VALUE);
                Buffer buffer = source.buffer();
                MediaType contentType = responseBody.contentType();
                boolean plaintext = isPlaintext(buffer);
                if (!plaintext) {
                    WeDebug.e("请求结果不是文本，其类型是：" + contentType);
                    return response;
                }
                Charset charset = UTF8;
                if (null != contentType) {
                    charset = contentType.charset(UTF8);
                }
                long contentLength = responseBody.contentLength();
                if (0 != contentLength) {
                    String json = buffer.clone().readString(charset);
                    WeDebug.e("Json :" + json);
                }
            }
            return response;
        } else {
            return chain.proceed(request);
        }
    }

    private static boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            // Truncated UTF-8 sequence.
            return false;
        }
    }

    @Override
    public boolean isNetInterceptor() {
        return true;
    }
}
