package cn.wenet.networkcomponent.life;

import io.reactivex.disposables.Disposable;

/**
 *
 * @author WANG
 * @date 2018/7/19
 */

public interface WeNetLifecycleControl {

   void requestStart(Disposable disposable);

   void requestEnd(Disposable disposable);

   void pageDestroy();

}
