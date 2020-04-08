package cn.wenet.networkcomponent.life;

import android.util.Log;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created to : 管理一个应用界面中的网络请求，界面载体只能是Activity、Fragment。
 * 一个PageLifeManager实例对应一个界面，界面结束实例也会被销毁掉。
 *
 * @author cc.wang
 * @date 2020/4/7
 */
public class PageLifeManager implements WeNetLifecycleControl {


    private CompositeDisposable mDisposable;

    @Override
    public void requestStart(Disposable disposable) {
        if (null == disposable) {
            return;
        }
        if (null == mDisposable) {
            mDisposable = new CompositeDisposable();
        }
        mDisposable.add(disposable);
    }

    @Override
    public void requestEnd(Disposable disposable) {
        if (null != disposable && null != mDisposable) {
            mDisposable.remove(disposable);
        }
    }

    @Override
    public void pageDestroy() {
        if (null != mDisposable && mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
        mDisposable = null;
        Log.e("cc.wang", "PageLifeManager.pageDestroy.");
    }


}
