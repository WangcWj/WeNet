package cn.wenet.networkcomponent.life;


import java.util.Map;

import cn.wenet.networkcomponent.core.WeNetRequest;
import cn.wenet.networkcomponent.request.NetRequestImpl;

/**
 * Created to :
 *
 * @author cc.wang
 * @date 2020/3/27
 */
public interface RequestLifeCircle {


    void addRequestParams(NetRequestImpl request);

    NetRequestImpl getRequest(String url);

    Map<String, NetRequestImpl> getRequests();

    void removeRequest(String url);


}
