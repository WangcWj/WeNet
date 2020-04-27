package cn.wenet.networkcomponent.core;

import java.util.Map;

import cn.wenet.networkcomponent.retrofit.calladapter.WeNetResultObservable;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

/**
 * Created to :
 *
 * @author cc.wang
 * @date 2020/4/10
 */
public interface WeNetRequest extends WeNetResult {

    /**
     * 获取表单请求的参数。
     *
     * @return 没添加基础参数或者额外参数的话，获取的集合大小是0。
     */
    Map<String, Object> getParams();

    /**
     * 获取本次请求的完整Url。
     *
     * @return
     */
    String getUrl();

    /**
     * 获取本次请求的Observable对象。返回值有两种，根据ApiService的接口返回值类型：
     * {@link WeNetResultObservable}和{@link Observable}
     *
     * @return
     */
    Observable getObservable();

    /**
     * 接口通过RequestBody传Json类型的参数。
     *
     * @param json 需要传给服务的Json。
     * @return
     */
    WeNetRequest bodyToJson(String json);

    /**
     * 接口通过RequestBody传Object类型的参数。
     *
     * @param obj 需要传给服务端的数据。
     * @return
     */
    WeNetRequest bodyToJson(Object obj);

    /**
     * 添加Map类型的参数。
     *
     * @param params
     * @return
     */
    WeNetRequest addParams(Map<String, Object> params);

    /**
     * 本次请求的参数是通过RequestBody提交的。
     * 在使用{@link #bodyToJson(Object),#bodyToJson(String)}的场景中，必须调用该方法。
     *
     * @return true 表示参数是通过RequestBody提交的。
     */
    boolean isBody();

    /**
     * 本次请求的参数是表单提交FormBody的。默认的类型。
     *
     * @return
     */
    boolean isForm();

    /**
     * 自动展示进度框。
     *
     * @return
     */
    void autoShowProgress();

    /**
     * 接口缓存的模式。
     *
     * @return
     */
    void cacheMode(boolean useCache);

    /**
     * 直接执行Observable对象，跟Retrofit的基本使用是一样的。
     *
     * @param observable
     * @param <T>
     * @return
     */
    <T> WeNetRequest apiMethod(Observable<T> observable);

    /**
     * 发起网络请求。
     *
     * @param callback 请求结果的回调。
     * @param <T>
     */
    <T> void execute(WeNetworkCallBack<T> callback);

    Disposable getCurrentDisposable();

}
