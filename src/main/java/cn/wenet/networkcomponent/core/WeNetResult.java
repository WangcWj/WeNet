package cn.wenet.networkcomponent.core;

import cn.wenet.networkcomponent.request.NetRequest;

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
    NetRequest addParams(String key, String value);

    NetRequest asBody();

    NetRequest asFrom();

}
