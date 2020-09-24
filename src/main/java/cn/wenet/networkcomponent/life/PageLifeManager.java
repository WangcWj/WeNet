package cn.wenet.networkcomponent.life;


import android.content.Context;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import cn.wenet.networkcomponent.debug.WeDebug;
import cn.wenet.networkcomponent.request.NetRequestImpl;
import cn.wenet.networkcomponent.utils.ThreadUtils;
import io.reactivex.disposables.CompositeDisposable;



/**
 * Created to : 管理一个应用界面中的网络请求，界面载体只能是Activity、Fragment。
 * 一个PageLifeManager实例对应一个界面，界面结束实例也会被销毁掉。
 *
 * @author cc.wang
 * @date 2020/4/7
 */
public class PageLifeManager implements WeNetLifecycleControl {

    private RequestLifeCircle mManagerRequest;
    private CompositeDisposable mDisposable;
    private Context mContext;
    private final Set<ComponentLifeCircle> mLifeCircles = Collections.newSetFromMap(new WeakHashMap<ComponentLifeCircle, Boolean>());

    PageLifeManager(RequestLifeCircle managerRequest) {
        this.mManagerRequest = managerRequest;
    }

    public void setContext(Context mContext) {
        this.mContext = mContext;
    }

    public boolean isCreateSuccess(){
        return null != mManagerRequest;
    }

    /**
     * 可能为null。
     *
     * @return Context。
     */
    public Context getContext() {
        return mContext;
    }

    private void addRequestParams(NetRequestImpl request) {
        mManagerRequest.addRequest(request);
    }

    private void removeRequest(String url) {
        mManagerRequest.removeRequest(url);
    }

    @Override
    public void register(ComponentLifeCircle lifeCircle) {
        mLifeCircles.add(lifeCircle);
    }

    @Override
    public void unRegister(ComponentLifeCircle lifeCircle) {
        mLifeCircles.remove(lifeCircle);
    }

    @Override
    public void requestStart(NetRequestImpl request) {
        if (null == request) {
            return;
        }
        if (null == mDisposable) {
            mDisposable = new CompositeDisposable();
        }
        addRequestParams(request);
        if (null != request.getCurrentDisposable()) {
            WeDebug.d("PageLifeManager requestStart" + mDisposable.size());
            mDisposable.add(request.getCurrentDisposable());
        }
    }

    @Override
    public void requestEnd(NetRequestImpl request) {
        if (null == request) {
            return;
        }
        removeRequest(request.getUrl());
        if (null != request.getCurrentDisposable() && null != mDisposable) {
            WeDebug.d("PageLifeManager requestEnd" + mDisposable.size());
            mDisposable.remove(request.getCurrentDisposable());
        }
    }

    @Override
    public void pageDestroy() {
        //取消掉未结束的网络请求，比如说取消掉接口超时时的网络重试机制。
        if (null != mDisposable && !mDisposable.isDisposed() && mDisposable.size() > 0) {
            WeDebug.d("PageLifeManager pageDestroy 有网络没取消掉 " + mDisposable.size());
            mDisposable.dispose();
        }
        //NetBaseObserver的生命周期的控制。
        for (ComponentLifeCircle lifecycleListener : ThreadUtils.getSnapshot(mLifeCircles)) {
            lifecycleListener.onDestroy();
        }
        mLifeCircles.clear();
        mContext = null;
        mDisposable = null;
        mManagerRequest = null;
        WeDebug.d("PageLifeManager pageDestroy");
    }


}

