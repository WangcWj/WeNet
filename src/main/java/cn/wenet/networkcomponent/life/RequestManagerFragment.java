package cn.wenet.networkcomponent.life;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import cn.wenet.networkcomponent.core.WeNetWork;
import cn.wenet.networkcomponent.debug.WeDebug;

/**
 * Created to : 一个Fragment，用来管理Activity的生命周期。
 *
 * @author cc.wang
 * @date 2020/4/7
 */
public class RequestManagerFragment extends Fragment {

    private PageLifeManager pageLifeManager;

    /**
     * 崩溃重建的时候，可能会执行该方法。
     */
    public RequestManagerFragment() {
        this(null, WeNetWork.getLifeCircleManager());
    }

    public RequestManagerFragment(Context context, WeNetLifeCircleManager manager) {
        if (null != manager) {
            pageLifeManager = new PageLifeManager(manager);
            pageLifeManager.setContext(context);
        }
    }

    PageLifeManager getPageLifeManager() {
        return pageLifeManager;
    }

    @Override
    public void onDestroy() {
        if (null != pageLifeManager) {
            pageLifeManager.pageDestroy();
        }
        super.onDestroy();
        WeDebug.d("RequestManagerFragment.onDestroy");
        pageLifeManager = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != pageLifeManager) {
            pageLifeManager.setContext(getContext());
        }
        WeDebug.d("RequestManagerFragment.onCreate");
    }
}
