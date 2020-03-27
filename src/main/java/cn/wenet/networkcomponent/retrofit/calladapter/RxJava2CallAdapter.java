package cn.wenet.networkcomponent.retrofit.calladapter;

import androidx.annotation.Nullable;

import java.lang.reflect.Type;

import cn.wenet.networkcomponent.core.WeNetObservable;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Response;

/**
 * Created to :
 *
 * @author cc.wang
 * @date 2020/3/27
 */
public class RxJava2CallAdapter<R> implements CallAdapter<R, Object> {
    private final Type responseType;
    private final @Nullable
    Scheduler scheduler;
    private final boolean isAsync;

    RxJava2CallAdapter(Type responseType, @Nullable Scheduler scheduler, boolean isAsync) {
        this.responseType = responseType;
        this.scheduler = scheduler;
        this.isAsync = isAsync;
    }

    @Override
    public Type responseType() {
        return responseType;
    }

    @Override
    public Object adapt(Call<R> call) {

        Observable<Response<R>> responseObservable = isAsync
                ? new CallEnqueueObservable<>(call)
                : new CallExecuteObservable<>(call);
        Observable<?> observable = new BodyObservable<>(responseObservable,call.request());
        if (scheduler != null) {
            observable = observable.subscribeOn(scheduler);
        }
        if(observable instanceof WeNetObservable){
           //do some thing
        }
        return observable;
    }
}
