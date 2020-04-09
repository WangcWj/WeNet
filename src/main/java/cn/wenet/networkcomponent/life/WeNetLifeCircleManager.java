package cn.wenet.networkcomponent.life;


import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

/**
 * Created to :
 *
 * @author cc.wang
 * @date 2020/3/27
 */
public class WeNetLifeCircleManager {

    static final String FRAGMENT_TAG = "com.wenet.manager";


    public PageLifeManager bindContext(Context context) {
        if (null == context) {
            throw new IllegalArgumentException("You cannot start a load on a null Context");
        } else if (context instanceof FragmentActivity) {
            return bindActivity((FragmentActivity) context);
        }
        return null;
    }

    private PageLifeManager bindActivity(FragmentActivity fragmentActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && fragmentActivity.isDestroyed()) {
            throw new IllegalArgumentException("You cannot start a load for a destroyed activity");
        }
        FragmentManager supportFragmentManager = fragmentActivity.getSupportFragmentManager();
        return supportFragmentGet(fragmentActivity, supportFragmentManager);
    }

    private PageLifeManager supportFragmentGet(Context context, FragmentManager fragmentManager) {
        WeNetRequestManagerFragment tag = (WeNetRequestManagerFragment) fragmentManager.findFragmentByTag(FRAGMENT_TAG);
        if (null == tag) {
            tag = new WeNetRequestManagerFragment();
            fragmentManager.beginTransaction().add(tag, FRAGMENT_TAG).commitAllowingStateLoss();
        }
        return tag.getPageLifeManager();
    }

}
