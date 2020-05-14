package cn.wenet.networkcomponent.retrofit.retrywhen;

import android.accounts.NetworkErrorException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import cn.wenet.networkcomponent.base.NetBaseParam;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.internal.schedulers.SingleScheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * 错误重连机制,可以自定义重连的次数和下次请求发出之间的时间间隔
 *
 * @author WANG
 * @date 2018/7/18
 */

public class NetRetryWhen implements Function<Observable<Throwable>, ObservableSource<?>> {

    private int mRetryCount;
    private int mRetryCurrent = 0;
    private long mRetryWhenTime;

    public void reset(int mRetryCount, long mRetryWhenTime){
        mRetryCurrent = 0;
        this.mRetryCount = mRetryCount;
        this.mRetryWhenTime = mRetryWhenTime;
    }

    @Override
    public ObservableSource<?> apply(Observable<Throwable> throwableObservable) throws Exception {
        return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(@NonNull Throwable throwable) throws Exception {
                boolean isRun = mRetryCurrent < mRetryCount;
                if (!isRun) {
                    return Observable.error(throwable).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
                }
                boolean isNetError = throwable instanceof NetworkErrorException || throwable instanceof SocketTimeoutException;
                boolean isConnectionError = throwable instanceof ConnectException || throwable instanceof TimeoutException;
                if (isNetError || isConnectionError) {
                    mRetryCurrent++;
                    SingleScheduler scheduler = new SingleScheduler();
                    return Observable.timer(mRetryWhenTime, TimeUnit.MILLISECONDS, scheduler);
                }
                return Observable.error(throwable).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            }
        });
    }
}
