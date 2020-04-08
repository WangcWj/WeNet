package cn.wenet.networkcomponent.retrofit.calladapter;

import androidx.annotation.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import cn.wenet.networkcomponent.base.NetBaseResultBean;
import cn.wenet.networkcomponent.core.WeNetResult;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;

/**
 * Created to : 仿照RxJava2CallAdapterFactory。
 *
 * @author cc.wang
 * @date 2020/3/27
 */
public class WeNetCallAdapterFactory extends CallAdapter.Factory {


    /**
     * Returns an instance which creates synchronous observables that do not operate on any scheduler
     * by default.
     */
    public static WeNetCallAdapterFactory create() {
        return new WeNetCallAdapterFactory(null, false);
    }

    /**
     * Returns an instance which creates asynchronous observables. Applying
     * {@link Observable#subscribeOn} has no effect on stream types created by this factory.
     */
    public static WeNetCallAdapterFactory createAsync() {
        return new WeNetCallAdapterFactory(null, true);
    }

    /**
     * Returns an instance which creates synchronous observables that
     * {@linkplain Observable#subscribeOn(Scheduler) subscribe on} {@code scheduler} by default.
     */
    @SuppressWarnings("ConstantConditions") // Guarding public API nullability.
    public static WeNetCallAdapterFactory createWithScheduler(Scheduler scheduler) {
        if (scheduler == null) {
            throw new NullPointerException("scheduler == null");
        }
        return new WeNetCallAdapterFactory(scheduler, false);
    }

    private final @Nullable
    Scheduler scheduler;
    private final boolean isAsync;

    private WeNetCallAdapterFactory(@Nullable Scheduler scheduler, boolean isAsync) {
        this.scheduler = scheduler;
        this.isAsync = isAsync;
    }

    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        Class<?> rawType = getRawType(returnType);
        if (rawType != WeNetResult.class) {
            return null;
        }
        if (!(returnType instanceof ParameterizedType)) {
            String name = returnType.toString();
            throw new IllegalStateException(name + " return type must be parameterized"
                    + " as " + name + "<Foo> or " + name + "<? extends Foo>");
        }
        //获取Observable<T>中T泛型的上限父类。比如 Observable<List< a extends WeNet>>,获取到的类型就是WeNet。
        Type observableType = getParameterUpperBound(0, (ParameterizedType) returnType);
        //获取Observable<T>中T的泛型。比如 Observable<List< a extends WeNet>>,获取到的类型就是List。
        Class<?> rawObservableType = getRawType(observableType);
        if(rawObservableType == NetBaseResultBean.class){
            //do something

        }
        return new WeNet2CallAdapter(observableType, scheduler, isAsync);
    }
}
