package cn.wenet.networkcomponent.utils;

import android.text.TextUtils;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;

import cn.wenet.networkcomponent.debug.WeDebug;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;

/**
 * Created to :
 *
 * @author cc.wang
 * @date 2020/4/8
 */
public class RequestBodyUtils {

    private static final Charset UTF8 = Charset.forName("UTF-8");

    public static String requestBodyToString(RequestBody requestBody) throws IOException {
        Buffer buffer = new Buffer();
        requestBody.writeTo(buffer);
        Charset charset = UTF8;
        MediaType contentType = requestBody.contentType();
        if (contentType != null) {
            charset = contentType.charset(UTF8);
        }
        if (isPlaintext(buffer)) {
            String readString = buffer.readString(charset);
            return readString;
        } else {
            WeDebug.e("RequestBody 内容不是文本格式！");
        }
        return "";
    }

    public static void appendToRequestBody(RequestBody requestBody)throws IOException{
        String read = requestBodyToString(requestBody);
        //city=%E6%B4%9B%E9%98%B3&key=a1ae58f53edaf0518c72f41adc3987a9
        if(!TextUtils.isEmpty(read)){
            WeDebug.e("准备 拼接到 RequestBody");
        }
    }

    public static boolean isPlaintext(Buffer buffer) {
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
}
