package cn.wenet.networkcomponent.utils;

import android.text.TextUtils;

import com.google.gson.stream.JsonWriter;

import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

import cn.wenet.networkcomponent.debug.WeDebug;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;

/**
 * Created to : 对RequestBody的一些操作方法。
 *
 * @author cc.wang
 * @date 2020/4/8
 */
public class RequestBodyUtils {

    private static final Charset UTF8 = Charset.forName("UTF-8");
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=UTF-8");

    public static String requestBodyToString(RequestBody requestBody) throws IOException {
        Buffer buffer = new Buffer();
        requestBody.writeTo(buffer);
        Charset charset = UTF8;
        MediaType contentType = requestBody.contentType();
        if (contentType != null) {
            charset = contentType.charset(UTF8);
        }
        if (null == charset) {
            charset = UTF8;
        }
        if (isPlaintext(buffer)) {
            return buffer.readString(charset);
        } else {
            WeDebug.d("RequestBody 内容不是文本格式！");
        }
        return "";
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
