package cn.wenet.networkcomponent.life;


import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Created to : 一个Fragment，用来管理Activity的生命周期。
 *
 * @author cc.wang
 * @date 2020/4/7
 */
public class WeNetRequestManagerFragment extends Fragment {

    private PageLifeManager pageLifeManager;

    public WeNetRequestManagerFragment() {
        pageLifeManager = new PageLifeManager();
    }

    public static WeNetRequestManagerFragment getInstance() {
        return new WeNetRequestManagerFragment();
    }

    PageLifeManager getPageLifeManager() {
        return pageLifeManager;
    }

    @Override
    public void onDestroy() {
        if(null != pageLifeManager) {
            pageLifeManager.pageDestroy();
        }
        super.onDestroy();
        Log.e("cc.wang","WeNetRequestManagerFragment.onDestroy.");
        pageLifeManager = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("cc.wang","WeNetRequestManagerFragment.onCreate.");
    }
}
