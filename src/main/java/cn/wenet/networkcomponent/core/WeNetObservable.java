package cn.wenet.networkcomponent.core;

import android.util.Log;

import cn.wenet.networkcomponent.request.NetRequest;
import cn.wenet.networkcomponent.retrofit.calladapter.BodyObservable;
import io.reactivex.Observable;
import io.reactivex.Observer;
import okhttp3.Request;

/**
 * Created to :
 *
 * @author cc.wang
 * @date 2020/3/27
 */
public class WeNetObservable<T> extends Observable<T> {

    @Override
    protected void subscribeActual(Observer<? super T> observer) {
       //do something
    }

    private NetRequest netRequest;

    public WeNetObservable() {
        netRequest = new NetRequest(Control.getInstance());
        if(this instanceof BodyObservable) {
            netRequest.setNetObservable((BodyObservable) this);
        }
    }

    protected void subscribeActual(Request request) {
        if (null != request) {
            String url = request.url().toString();
            Log.e("cc.wang", "WeNetObservable.subscribeActual." + url);
            Control.getInstance().addRequestParams(url, netRequest);
        }
    }

    public NetRequest addParams(String key, String value) {
        netRequest.addParams(key, value);
        return netRequest;
    }
}
