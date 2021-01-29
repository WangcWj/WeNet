package cn.wenet.networkcomponent.cache;

import android.content.Context;

import cn.wenet.networkcomponent.core.WeNetworkCallBack;
import cn.wenet.networkcomponent.debug.WeDebug;
import cn.wenet.networkcomponent.debug.exception.NetException;
import cn.wenet.networkcomponent.request.NetRequestImpl;
import io.reactivex.disposables.Disposable;
import okhttp3.HttpUrl;
import okhttp3.Response;
import okio.ByteString;

/**
 * Created to :
 *
 * @author cc.wang
 * @date 2020/4/14
 */
public class WeNetCache implements WeNetworkCallBack {

    private WeNetworkCallBack mResultCallBack;
    private String mUrl;

    public void attach(NetRequestImpl imp, WeNetworkCallBack networkCallBack) {
        this.mResultCallBack = networkCallBack;
        this.mUrl = imp.getUrl();
        imp.autoShowProgress();
        getCacheData(mUrl);
    }

    private void getCacheData(String url) {
        WeDebug.e("Cache  Url is  ", url);
    }

    @Override
    public void onSuccess(Object obj) {

    }

    @Override
    public void onError(NetException e) {

    }

    @Override
    public void pageLifeCircle(Disposable disposable) {

    }

    @Override
    public void showProgress(Context context, boolean canShow) {

    }


    Response get(String mUrl){

    }


    public static String key(HttpUrl url) {
        return ByteString.encodeUtf8(url.toString()).md5().hex();
    }

}
