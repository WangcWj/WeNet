package cn.wenet.networkcomponent.retrofit.calladapter;

import android.app.Dialog;
import android.content.Context;

import androidx.fragment.app.Fragment;

import cn.wenet.networkcomponent.core.WeNetRequest;
import cn.wenet.networkcomponent.core.WeNetResult;
import cn.wenet.networkcomponent.core.WeNetWork;
import cn.wenet.networkcomponent.request.NetRequestImpl;
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
    private WeNetRequest mNetRequest;

    WeNetResultObservable(Observable<Response<T>> upstream, Request request) {
        super();
        this.upstream = upstream;
        mCurrentRequest = request;
        mNetRequest = WeNetWork.request();
        ((NetRequestImpl)mNetRequest).setNetObservable(this);
        if (null != mCurrentRequest && null != mNetRequest) {
            //要处理多BaseUrl的情况
            String url = mCurrentRequest.url().toString();
            ((NetRequestImpl)mNetRequest).attachUrl(url);
        }
    }

    @Override
    protected void subscribeActual(Observer<? super T> observer) {
        upstream.subscribe(new WeNetResultObservable.BodyObserver<T>(observer));
    }

    @Override
    public WeNetRequest addParams(String key, String value) {
        mNetRequest.addParams(key, value);
        return mNetRequest;
    }

    @Override
    public WeNetRequest addParams(String key, Object value) {
        mNetRequest.addParams(key, value);
        return mNetRequest;
    }

    @Override
    public WeNetRequest asBody() {
        mNetRequest.asBody();
        return mNetRequest;
    }

    @Override
    public WeNetRequest asFrom() {
        mNetRequest.asFrom();
        return mNetRequest;
    }

    @Override
    public WeNetRequest bindLife(Context context) {
        mNetRequest.bindLife(context);
        return mNetRequest;
    }

    @Override
    public WeNetRequest bindLife(Fragment fragment) {
        mNetRequest.bindLife(fragment);
        return mNetRequest;
    }

    @Override
    public WeNetRequest bindLife(Dialog dialog) {
        mNetRequest.bindLife(dialog);
        return mNetRequest;
    }

    @Override
    public WeNetRequest showProgress(boolean show) {
        mNetRequest.showProgress(show);
        return mNetRequest;
    }

    @Override
    public WeNetRequest isUseCache(boolean use) {
        mNetRequest.isUseCache(use);
        return mNetRequest;
    }

    public WeNetRequest getNetRequestImpl() {
        return mNetRequest;
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

