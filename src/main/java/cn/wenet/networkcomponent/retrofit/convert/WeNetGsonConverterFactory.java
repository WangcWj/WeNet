package cn.wenet.networkcomponent.retrofit.convert;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * 重写GsonConverterFactory，对接口的请求结果做出预处理。
 * @author cc.wang
 */
public class WeNetGsonConverterFactory extends Converter.Factory {

    private final Gson gson;

    private WeNetGsonConverterFactory(Gson gson) {
        if (null == gson) {
            throw new NullPointerException("gson is null");
        }
        this.gson = gson;
    }

    public static WeNetGsonConverterFactory create() {
        return create(new Gson());
    }

    public static WeNetGsonConverterFactory create(Gson gson) {
        return new WeNetGsonConverterFactory(gson);
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {

        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new WeNetGsonResponseBodyConverter<>(gson, adapter);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new WeNetGsonRequestBodyConverter<>(gson, adapter);
    }

}
