package cn.wenet.networkcomponent.life;

import cn.wenet.networkcomponent.request.NetRequestImpl;

/**
 *
 * @author WANG
 * @date 2018/7/19
 */

public interface WeNetLifecycleControl {

   void requestStart(NetRequestImpl request);

   void requestEnd(NetRequestImpl request);

   void pageDestroy();

   void register(ComponentLifeCircle lifeCircle);

   void unRegister(ComponentLifeCircle lifeCircle);

}
