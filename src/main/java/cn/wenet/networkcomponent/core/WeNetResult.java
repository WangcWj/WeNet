package cn.wenet.networkcomponent.core;

import android.app.Dialog;
import android.content.Context;

import androidx.fragment.app.Fragment;

/**
 * Created to :
 *
 * @author cc.wang
 * @date 2020/3/27
 */
public interface WeNetResult<T> {
    /**
     * 表单提交的参数。
     *
     * @param key  表单提交的key。
     * @param value 表单提交的value。
     * @return 网络请求实体类。
     */
    WeNetRequest addParams(String key, String value);

    /**
     * 表单提交的参数。
     *
     * @param key  表单提交的key。
     * @param value 表单提交的value。
     * @return 网络请求实体类。
     */
    WeNetRequest addParams(String key, Object value);

    /**
     * 请求的参数通过RequestBody上传。
     *
     * @return
     */
    WeNetRequest asBody();

    /**
     * 表单提交key value形式。
     *
     * @return
     */
    WeNetRequest asFrom();

    /**
     * 网络请求的生命周期跟Activity界面绑定。
     *
     * @param context
     * @return WeNetRequest 发起请求。
     */
    WeNetRequest bindLife(Context context);

    /**
     * 网络请求的生命周期跟Fragment绑定。
     *
     * @param fragment
     * @return WeNetRequest 发起请求。
     */
    WeNetRequest bindLife(Fragment fragment);

    /**
     * 网络请求的生命周期跟Dialog绑定。
     * <p>注意：该方法需要在Dialog创建之后，或者构造方法执行之后使用，避免Window对象还没创建。</p>
     * @param dialog
     * @return WeNetRequest 发起请求。
     */
    WeNetRequest bindLife(Dialog dialog);

    /**
     *
     * @param show
     * @return
     */
    WeNetRequest showProgress(boolean show);

    /**
     * 使用接口缓存。
     * @param use true 缓存接口数据。
     * @return
     */
    WeNetRequest isUseCache(boolean use);
}
