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
import java.nio.charset.Charset;

import cn.wenet.networkcomponent.base.NetBaseResultBean;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 *
 */
public class WeNetGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    private final Gson gson;
    private final TypeAdapter<T> adapter;
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    WeNetGsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
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
}
