package cn.wenet.networkcomponent.retrofit.convert;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.List;

import cn.wenet.networkcomponent.base.NetBaseResultBean;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * 对接口请求进行预处理。
 */
public class WeNetGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    private final Gson gson;
    private final TypeAdapter<T> adapter;
    private Type mReturnType;
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    WeNetGsonResponseBodyConverter(Gson gson, Type returnType, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
        this.mReturnType = returnType;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        String response = value.string();
        MediaType contentType = value.contentType();
        Charset charset = contentType != null ? contentType.charset(UTF_8) : UTF_8;
        InputStream inputStream = new ByteArrayInputStream(response.getBytes());
        Reader reader = new InputStreamReader(inputStream, charset);
        JsonReader jsonReader = gson.newJsonReader(reader);
        try {
            JSONObject jsonObject = new JSONObject(response);
            int code = jsonObject.optInt("code");
            if (code == 200) {
                return adapter.read(jsonReader);
            } else {
                JSONObject jsonObject1 = new JSONObject(response);
                int code1 = jsonObject1.getInt("code");
                String message = jsonObject.getString("msg");
                return (T) new NetBaseResultBean<>();
            }
        } catch (Exception e) {

        } finally {
            value.close();
        }
        return null;
    }

    private boolean checkResult(JSONObject jsonObject) {
        try {
            //只检查参数类型的，比如BaseRequest<String>。
            if (mReturnType instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) mReturnType;
                Type actualTypeArgument = pt.getActualTypeArguments()[0];
                if (actualTypeArgument instanceof Class) {
                    return handlerResult((Class<?>) actualTypeArgument, jsonObject);
                } else if (actualTypeArgument instanceof ParameterizedType) {
                    ParameterizedType innerPt = (ParameterizedType) actualTypeArgument;
                    Class<?> rawType = (Class<?>) innerPt.getRawType();
                    return handlerResult(rawType, jsonObject);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean handlerResult(Class<?> clz, JSONObject jsonObject) throws Exception {
        String data = String.valueOf(jsonObject.get("data"));
        if (clz == List.class) {
            //BaseRequest<T> T为List。
            return data.startsWith("[") && data.endsWith("]");
        } else if (clz == String.class) {
            //do something
        } else {
            //BaseRequest<T> T为Object。
            return data.startsWith("{") && data.endsWith("}");
        }
        return true;
    }
}
