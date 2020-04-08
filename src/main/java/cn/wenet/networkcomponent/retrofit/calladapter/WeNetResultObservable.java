package cn.wenet.networkcomponent.retrofit.calladapter;

import cn.wenet.networkcomponent.core.WeNetResult;
import cn.wenet.networkcomponent.core.WeNetWork;
import cn.wenet.networkcomponent.request.NetRequest;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.plugins.RxJavaPlugins;
import okhttp3.Request;
import retrofit2.Response;
import retrofit2.adapter.rxjava2.HttpException;

/**
 * Created to : 处理自定义ApiService返回值。
 *
 * @author cc.wang
 * @date 2020/3/27
 */
public class WeNetResultObservable<T> extends Observable<T> implements WeNetResult<T> {
    private final Observable<Response<T>> upstream;
    private Request mCurrentRequest;

    private NetRequest netRequest;

    WeNetResultObservable(Observable<Response<T>> upstream, Request request) {
        super();
        this.upstream = upstream;
        mCurrentRequest = request;
        netRequest = WeNetWork.request();
        netRequest.setNetObservable(this);
    }

    @Override
    protected void subscribeActual(Observer<? super T> observer) {
        if (null != mCurrentRequest && null != netRequest) {
            //要处理多BaseUrl的情况
            String url = mCurrentRequest.url().toString();
            netRequest.attachUrl(url);
        }
        upstream.subscribe(new WeNetResultObservable.BodyObserver<T>(observer));
    }

    @Override
    public NetRequest addParams(String key, String value) {
        netRequest.addParams(key, value);
        return netRequest;
    }

    @Override
    public NetRequest asBody() {
        netRequest.asBody();
        return netRequest;
    }

    @Override
    public NetRequest asFrom() {
        netRequest.asFrom();
        return netRequest;
    }

    public NetRequest getNetRequest() {
        return netRequest;
    }

    private static class BodyObserver<R> implements Observer<Response<R>> {
        private final Observer<? super R> observer;
        private boolean terminated;

        BodyObserver(Observer<? super R> observer) {
            this.observer = observer;
        }

        @Override
        public void onSubscribe(Disposable disposable) {
            observer.onSubscribe(disposable);
        }

        @Override
        public void onNext(Response<R> response) {
            if (response.isSuccessful()) {
                observer.onNext(response.body());
            } else {
                terminated = true;
                Throwable t = new HttpException(response);
                try {
                    observer.onError(t);
                } catch (Throwable inner) {
                    Exceptions.throwIfFatal(inner);
                    RxJavaPlugins.onError(new CompositeException(t, inner));
                }
            }
        }

        @Override
        public void onComplete() {
            if (!terminated) {
                observer.onComplete();
            }
        }

        @Override
        public void onError(Throwable throwable) {
            if (!terminated) {
                observer.onError(throwable);
            } else {
                // This should never happen! onNext handles and forwards errors automatically.
                Throwable broken = new AssertionError(
                        "This should never happen! Report as a bug with the full stacktrace.");
                //noinspection UnnecessaryInitCause Two-arg AssertionError constructor is 1.7+ only.
                broken.initCause(throwable);
                RxJavaPlugins.onError(broken);
            }
        }
    }
}

